package gov.usgs.cida.cidabot;

import com.lotus.sametime.core.types.STUser;

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
	public String runCommand(STUser user, String cmd, String args) {
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
		return HELP_TEXT;
	}
	
	private String join(STUser user, String roomArgs) {
		if (roomArgs == null || roomArgs.equals("")) {
			return printRoomList();
		}
		String[] roomList = confMan.roomList();
		try {
			String selection = roomList[Integer.parseInt(roomArgs)];
			if (confMan.inviteConf(selection, user)) {
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
	
	private String add(STUser user, String roomArgs) {
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
			return printRoomList();
		}
		String[] roomList = confMan.roomList();
		try {
			String selection = roomList[Integer.parseInt(roomArgs)];
			if (confMan.removeConf(selection)) {
				return DELETED;
			}
			else {
				return DELETE_FAILED;
			}
		}
		catch (NumberFormatException nfe) {
			return DELETE_FAILED + "\n" + printRoomList();
		}
	}
	
	private String printRoomList() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(ROOM_LIST_HEAD);
		String[] roomList = confMan.roomList();
		for (int i=0; i<roomList.length; i++) {
			strbuf.append("[" + i + "]: " + roomList[i] + "\n"); 
		}
		return strbuf.toString();
	}
}
