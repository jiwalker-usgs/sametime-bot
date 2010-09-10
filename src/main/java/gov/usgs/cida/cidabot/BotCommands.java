package gov.usgs.cida.cidabot;

import gov.usgs.cida.cidabot.helper.KeywordHelper;

import com.lotus.sametime.core.types.STUserInstance;

import static gov.usgs.cida.cidabot.BotConstants.*;

public class BotCommands {
	
	private ConferenceManager confMan;
	
	public BotCommands(ConferenceManager confMan) {
		this.confMan = confMan;
	}
	
	/* Run command associated with String cmd, return resulting output
	 * Be sure to check for null args
	 * @param user User object to perform action for
	 * @param cmd Name of command to call
	 * @param args argument list for command (may be null)
	 * @return result string from command
	 */
	public String runCommand(STUserInstance user, String cmd, String args) {
		if (cmd.equalsIgnoreCase("join")) {
			return join(user, args);
		}
		else if (cmd.equalsIgnoreCase("list")) {
			return printRoomList();
		}
		else if (cmd.equalsIgnoreCase("add")) {
			return add(user, args);
		}
		else if (cmd.equalsIgnoreCase("del")) {
			return del(args);
		}
		else if (cmd.equalsIgnoreCase("keyword")) {
			return keyword(args);
		}
		else if (cmd.equalsIgnoreCase("history")) {
			return history(args);
		}
		return HELP_TEXT;
	}
	
	private String history(String args) {
		if (args == null || args.equals("")) {
			return printRoomList();
		}

		String history = confMan.getHistory(args);
		if (history == null) {
			return printRoomList();
		}
		else {
			return history;
		}

	}

	private String keyword(String args) {
		String [] words = args.split(" ");
		if (args == null || args.equals("") || words.length < 2) {
			return printRoomList();
		}
		StringBuilder phrase = new StringBuilder();
		for (int i=1; i<words.length; i++) {
			phrase.append(words[i]);
			phrase.append(" ");
		}
		KeywordHelper.addToKeywordMap(words[0], phrase.toString());
		return KEYWORD_ADDED;
	}

	private String join(STUserInstance user, String roomArgs) {
		if (roomArgs == null || roomArgs.equals("")) {
			return printRoomList();
		}
		try {
			if (confMan.inviteConf(roomArgs, user)) {
				System.err.println("invited " + user.getName() + " to room " + roomArgs);
				return INVITED;
			}
			else {
				return INVITE_FAILED + "\n" + printRoomList();
			}
		}
		catch(NumberFormatException nfe) {
			return INVITE_FAILED + "\n" + printRoomList();
		}
	}
	
	private String add(STUserInstance user, String roomArgs) {
		if (roomArgs == null || roomArgs.equals("")) {
			return ADD_FAILED;
		}
		if (confMan.createConf(roomArgs)) {
			if (confMan.inviteConf(roomArgs, user)) {
				return INVITED;
			}
			else {
				return INVITE_FAILED;
			}
		}
		else {
			return ADD_FAILED;
		}
	}
	
	private String del(String roomArgs) {
		if (roomArgs == null || roomArgs.equals("")) {
			return HELP_TEXT;
		}
		if (confMan.removeConf(roomArgs)) {
			return DELETED;
		}
		else {
			return DELETE_FAILED;
		}
	}
	
	private String printRoomList() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(ROOM_LIST_HEAD);
		String[] roomList = confMan.roomList();
		for (String roomname : roomList) {
			strbuf.append(roomname + "\n");
		}
		return strbuf.toString();
	}
}
