package gov.usgs.cida.cidabot.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.core.types.STUserInstance;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static gov.usgs.cida.cidabot.BotConstants.LOG_PATH;

public class RoomHelper {

	private final Integer confId;
	private final String confName;
	private Queue<String> history;
	private int historyLength;
	private Map<String, STUser> users;
	private BufferedWriter chatLog;
	private String lastDate;
	
	private static Logger log = Logger.getLogger(RoomHelper.class);
	
	public RoomHelper(Integer id, String confName, int historyLength) {
		this.confId = id;
		this.confName = confName;
		this.history = new LinkedList<String>();
		this.historyLength = historyLength;
		this.users = new HashMap<String, STUser>();
		this.lastDate = "xxx";
		this.chatLog = null;
	}

	private void openRoom() {
		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		try {
			File logFile = new File(LOG_PATH + "/" + confName + "." +
					dateString + ".log");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			if (!logFile.canWrite()) {
				log.error("Cannot write to file");
			}
			this.chatLog = new BufferedWriter(new FileWriter(logFile));
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
	
	public void addUser(STUserInstance stu) {
		log.debug("user " + stu.getName() + " has id " + stu.getId().getId());
		if (!users.keySet().contains(stu.getName())) {
			users.put(stu.getLoginId().getId(), stu);
		}
	}
	
	public void removeUser(String id) {
		users.remove(id);
	}
	
	public STUser getUser(String id) {
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

	private String createChatLine(String user, String text) {
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		return "(" + time + ") " + user + ": " + text;
	}
}