package gov.usgs.cida.cidabot;

import java.util.List;
import gov.usgs.cida.cidabot.helper.PropertyFactory;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.EOFException;
import com.lotus.sametime.awareness.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.im.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import static gov.usgs.cida.cidabot.BotConstants.*;

public class CIDABot implements Runnable, LoginListener, ImServiceListener, ImListener {

	private STSession session;
	private CommunityService communityService;
	private InstantMessagingService imService;
	private BotCommands cmds;

	// This is for multi-user rooms
	private ConferenceManager confMan;
	
	// This is for psuedo-rooms
	private RoomManager roomMan;

	private Thread engine;

	private List<String> defaultRooms;// = { "JavaDev", "GenDev", "PM", "CIDA" };
	public static String LOG_PATH;
	
	private static Logger log = Logger.getLogger(CIDABot.class);
	
	private Pattern cmdPatt = Pattern.compile("[!|/](\\w+)\\s*(.*)");
	
//	public static void main(String[] args) throws IOException {
//		if (args.length != 2) {
//			log.debug("usage: cidabot <server name> <user ID>; (will prompt for password)");
//			System.exit(1);
//		}
//		System.out.print("Enter password: ");
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		String password = reader.readLine();
//		CIDABot cidaSametimeBot = new CIDABot(args[0], args[1], password);
//		cidaSametimeBot.start();
//	}

	public CIDABot(String serverName, String userId, String password) {
		try {
			session = new STSession("CIDASametimeBot");
		}
		catch (DuplicateObjectException e) {
			e.printStackTrace();
			return;
		}

		LOG_PATH = PropertyFactory.getProperty("chatlog.location");
		defaultRooms = PropertyFactory.getValueList("default.rooms");
		
		
		session.loadSemanticComponents();
		session.start();

		communityService = (CommunityService)session.getCompApi(CommunityService.COMP_NAME);
		communityService.addLoginListener(this);
		communityService.loginByPassword(serverName, userId, password.toCharArray());
		
		confMan = new ConferenceManager(session);
		roomMan = new RoomManager();

		cmds = new BotCommands(confMan, roomMan);
	}

	@Override
	public void loggedIn(LoginEvent e) {
		log.debug("Logged In");
		imService = (InstantMessagingService)session.getCompApi(InstantMessagingService.COMP_NAME);
		imService.registerImType(ImTypes.IM_TYPE_CHAT);
		imService.addImServiceListener(this);

		confMan.setMyLoginId(communityService.getLogin().getMyUserInstance().getLoginId());
		
		// TODO move default rooms to persistent file
		for (String room : defaultRooms) {
			try {
				if (confMan.createConf(room)) {
					log.info("Added room " + room);
				}
			}
			catch (EOFException eofe) {
				// this is thrown for some reason
			}
		}
	}

	@Override
	public void loggedOut(LoginEvent e) {
		log.debug("Logged Out");
		session.stop();
		session.unloadSession();
	}


	@Override
	public void imReceived(ImEvent e) {
		e.getIm().addImListener(this);
		log.debug("IM Received");
	}

	@Override
	public void dataReceived(ImEvent e) {}

	@Override
	public void imClosed(ImEvent e) {}

	@Override
	public void imOpened(ImEvent e) {}

	@Override
	public void openImFailed(ImEvent e) {}

	@Override
	public void textReceived(ImEvent e) {
		Im im = e.getIm();
		String message = e.getText();
		STUserInstance sender = im.getPartnerDetails();
		String response = parseMessage(message, im);
		if (response != null) {
			im.sendText(true, response);
		}
		log.info("Message received from " + sender.getName());
	}

	public void serviceAvailable(AwarenessServiceEvent e) {}

	public void serviceUnavailable(AwarenessServiceEvent e) {}
	
	public void start() {
		if (engine == null) {
			engine = new Thread(this, "CIDABot");
			engine.start();
		}
	}

	@Override
	public void run() {
		Thread myThread = Thread.currentThread();
		while (engine == myThread) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
	
	public void close() {
		communityService.logout();
		session.stop();
	}

	public boolean isLoggedIn() {
		if (communityService == null || session == null) {
			return false;
		}
		return communityService.isLoggedIn() && session.isActive();
	}

	private String parseMessage(String text, Im im) {
		Matcher cmdMatch = cmdPatt.matcher(text);
		if (cmdMatch.matches()) {
			String cmd = cmdMatch.group(1);
			String args = cmdMatch.group(2);
			if (cmd.equals("")) {
				return cmds.runCommand(im, "help", null);
			}
			else {
				return cmds.runCommand(im, cmd, args);
			}
		}
		else {
			roomMan.addToChat(im, text);
			return null;
		}
	}
}