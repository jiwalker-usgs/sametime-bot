package gov.usgs.cida.cidabot;

import com.lotus.sametime.awareness.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.im.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import static gov.usgs.cida.cidabot.BotConstants.*;

public class CIDABot implements Runnable, LoginListener, ImServiceListener, ImListener {

	private STSession session;
	private CommunityService communityService;
	private InstantMessagingService imService;

	private ConferenceManager confMan;
	private Thread engine;
	private String[] defaultRooms = { "JavaDev", "GenDev", "PM", "CIDA" };
	
	private static Logger log = Logger.getLogger(CIDABot.class);
	
	private Pattern cmdPatt = Pattern.compile("[!|/](\\w+)\\s*(.*)");
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			log.debug("usage: cidabot <server name> <user ID>; (will prompt for password)");
			System.exit(1);
		}
		System.out.print("Enter password: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String password = reader.readLine();
		CIDABot cidaSametimeBot = new CIDABot(args[0], args[1], password);
		cidaSametimeBot.start();
	}

	public CIDABot(String serverName, String userId, String password) {
		try {
			session = new STSession("CIDASametimeBot");
		} catch (DuplicateObjectException e) {
			e.printStackTrace();
			return;
		}

		session.loadSemanticComponents();
		session.start();

		communityService = (CommunityService)session.getCompApi(CommunityService.COMP_NAME);
		communityService.addLoginListener(this);
		communityService.loginByPassword(serverName, userId, password.toCharArray());
		
		confMan = new ConferenceManager(session);

	}

	public void loggedIn(LoginEvent e) {
		log.debug("Logged In");
		imService = (InstantMessagingService)session.getCompApi(InstantMessagingService.COMP_NAME);
		imService.registerImType(ImTypes.IM_TYPE_CHAT);
		imService.addImServiceListener(this);

		confMan.setMyLoginId(communityService.getLogin().getMyUserInstance().getLoginId());
		
		// TODO move default rooms to persistent file
		for (String room : defaultRooms) {
			if (confMan.createConf(room)) {
				log.info("Added room " + room);
			}
		}
	}

	public void loggedOut(LoginEvent e) {
		log.debug("Logged Out");
		session = null;
		
	}


	public void imReceived(ImEvent e) {
		e.getIm().addImListener(this);
		log.debug("IM Received");
	}

	public void dataReceived(ImEvent e) {}

	public void imClosed(ImEvent e) {}

	public void imOpened(ImEvent e) {}

	public void openImFailed(ImEvent e) {}

	public void textReceived(ImEvent e) {
		Im im = e.getIm();
		String message = e.getText();
		STUserInstance sender = im.getPartnerDetails();
		String response = parseMessage(message, sender);
		im.sendText(true, response);
		log.info("Message received from " + sender.getName());
		log.info(message);
	}

	public void serviceAvailable(AwarenessServiceEvent e) {}

	public void serviceUnavailable(AwarenessServiceEvent e) {}
	
	public void start() {
		if (engine == null) {
			engine = new Thread(this, "CIDABot");
			engine.start();
		}
	}

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

	private String parseMessage(String text, STUserInstance user) {
		Matcher cmdMatch = cmdPatt.matcher(text);
		if (cmdMatch.matches()) {
			String cmd = cmdMatch.group(1);
			String args = cmdMatch.group(2);
			if (cmd.equals("")) {
				return confMan.runCommand(user, "help", null);
			}
			else {
				return confMan.runCommand(user, cmd, args);
			}
		}
		else {
			log.info("Command not matching");
			return HELP_TEXT;
		}
	}
}