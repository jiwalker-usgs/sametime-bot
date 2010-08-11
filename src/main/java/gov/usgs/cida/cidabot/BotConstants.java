package gov.usgs.cida.cidabot;

public final class BotConstants {
	public static final String INVITE_TEXT = "You're invited to chat";
	public static final String INVITE_FAILED = "Could not invite you to room, try again.";
	public static final String INVITED = "You have been invited to the chat";
	public static final String ROOM_LIST_HEAD = "Here are the open rooms:\n";
	public static final String UNKNOWN_CMD = "Sorry, I don't know what that means.";
	public static final String BAD_SYNTAX = "Invalid syntax, see /help for commands";
	public static final String ADD_FAILED = "Unable to create room, try a different name";
	public static final String DELETED = "Room deleted successfully";
	public static final String DELETE_FAILED = "Failed to delete room";
	public static final String KEYWORD_ADDED = "Keyword added to the list";
	public static final int DEFAULT_HISTORY_SIZE = 25;
	
	public static final String HELP_TEXT = "Command list (supports /cmd or !cmd)\n" +
		"\t/help - this dialog\n" +
		"\t/list - lists available rooms\n" +
		"\t/join <num> - join room, prints available rooms if number unknown\n" +
		"\t/add <room name> - creates a room with given name and invites you\n" +
		"\t/del <num> - deletes a room from the system\n" +
		//"\t/history <num> - prints out recent history from specified room\n" +
		"\t/keyword <word> <phrase> - adds a keyword to watch for in chats\n";
}
