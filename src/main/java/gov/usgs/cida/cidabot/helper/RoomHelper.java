package gov.usgs.cida.cidabot.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.core.types.STUserInstance;
import com.lotus.sametime.im.Im;
import gov.usgs.cida.cidabot.CIDABot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomHelper {

	private final Integer confId;
	private final String confName;
	private Queue<String> history;
	private int historyLength;
	private Map<String, STUser> users;
	private Set<Im> ims;
	private BufferedWriter chatLog;
	private String lastDate;

	private final boolean PSEUDO;
	private static Logger log = Logger.getLogger(RoomHelper.class);
	
	public RoomHelper(Integer id, String confName, int historyLength, boolean pseudo) {
		this.confId = id;
		this.confName = confName;
		this.history = new LinkedList<String>();
		this.historyLength = historyLength;
		this.users = new HashMap<String, STUser>();
		this.ims = new HashSet<Im>();
		this.lastDate = "xxx";
		this.chatLog = null;
		this.PSEUDO = pseudo;
	}

	private void openRoom() {
		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		try {
			File logFile = new File(CIDABot.LOG_PATH + "/" + confName + "." +
					dateString + ".log");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			if (!logFile.canWrite()) {
				log.error("Cannot write to file");
			}
			this.chatLog = new BufferedWriter(new FileWriter(logFile, true));
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	
	public Integer getId() {
		return confId;
	}
	
	public String getName() {
		return confName;
	}
	
	private void addHistory(String str) {
		if (historyLength <= 0) {
			return;
		}
		else if (history.size() < historyLength) {
			history.add(str);
		}
		else {
			history.remove();
			history.add(str);
		}
		log.debug("added \"" + str + "\" to history");
	}
	
	public String getHistory() {
		StringBuilder buf = new StringBuilder();
		Iterator<String> it = history.iterator();
		//for (int i=0; i < history.size(); i++) {
		while (it.hasNext()) {
			String head = it.next();
			//history.add(head);
			buf.append(head).append("\n");
		}
		return buf.toString();
	}

	public void addUser(Im im) {
		ims.add(im);
		addUser(im.getPartnerDetails());
		String name = im.getPartnerDetails().getName();
		sendToUsers(im, name + " has entered the room");
	}

	public void addUser(STUserInstance stu) {
		log.debug("user " + stu.getName() + " has id " + stu.getId().getId());
		if (!users.keySet().contains(stu.getName())) {
			users.put(stu.getLoginId().getId(), stu);
		}
	}

	public void removeUser(Im im) {
		ims.remove(im);
		removeUser(im.getPartnerDetails().getLoginId().getId());
		String name = im.getPartnerDetails().getName();
		sendToUsers(im, name + " has left the room");
	}

	public void removeUser(String id) {
		users.remove(id);
	}
	
	public STUser getUser(String id) {
		// this will return null if user was in room when bot arrives
		return users.get(id);
	}

	public void writeToLog(String text, String user) throws IOException {
		String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
		if (!now.equals(lastDate)) {
			closeRoom();
			openRoom();
		}
		lastDate = now;
		String chatLine = createChatLine(text, user);
		chatLog.write(chatLine);
		chatLog.newLine();
		addHistory(chatLine);
		chatLog.flush();
	}

	public void closeRoom() {
		if (chatLog == null) {
			return;
		}
		try {
			chatLog.flush();
			chatLog.close();
		}
		catch(IOException ioe) {
			log.debug("problem closing file");
		}
	}

	public Collection<STUser> getUsers() {
		return users.values();
	}

	public void sendToUsers(Im im, String text) {
		for (Im user : ims) {
			if (!user.equals(im)) {
					user.sendText(true, text);
			}
		}
	}

	private String createChatLine(String user, String text) {
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		return "(" + time + ") " + user + ": " + text;
	}
}