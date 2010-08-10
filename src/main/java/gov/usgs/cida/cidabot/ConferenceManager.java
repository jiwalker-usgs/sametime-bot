package gov.usgs.cida.cidabot;

import gov.usgs.cida.cidabot.helper.RoomHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.lotus.sametime.conf.ConfInfo;
import com.lotus.sametime.conf.ConfListener;
import com.lotus.sametime.conf.ConfService;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.types.STLoginId;
import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.core.types.STUserInstance;

import static gov.usgs.cida.cidabot.BotConstants.*;

public class ConferenceManager implements ConfListener {

	private ConfService confService;
	private Map<String, RoomHelper> rooms;
	private BotCommands coms;
	
	public ConferenceManager(STSession session) {
		rooms = new HashMap<String, RoomHelper>();
		confService = (ConfService)session.getCompApi(ConfService.COMP_NAME);
		confService.addConfListener(this);
		coms = new BotCommands(this);
	}
	
	public boolean createConf(String roomName) {
		if (rooms.containsKey(roomName)) {
			return false;
		}
		ConfInfo info = new ConfInfo(roomName, roomName);
		if (confService != null) {
			Integer confId = confService.createConference(info, EncLevel.ENC_LEVEL_DONT_CARE);
			System.out.println("create conference returned: " + confId);
			confService.joinToConference(confId);
			RoomHelper confObj = new RoomHelper(confId, roomName, DEFAULT_HISTORY_SIZE);
			rooms.put(roomName, confObj);
		}
		return true;
	}
	
	public boolean inviteConf(String roomName, STUser user) {
		if (!rooms.containsKey(roomName)) {
			return false;
		}
		else {
			Integer confId = rooms.get(roomName).getId();
			confService.inviteToConference(confId, user.getId(), INVITE_TEXT, new STLoginId(user.getId().getId(),
					"CIDA"), INVITE_TEXT);
					//commService.getLogin().getCommunityId()), INVITE_TEXT);
			return true;
		}
	}
	
	public boolean removeConf(String roomName) {
		if (!rooms.containsKey(roomName)) {
			return false;
		}
		
		Integer confId = rooms.get(roomName).getId();
		confService.destroyConference(confId);
		rooms.remove(roomName);
		return true;
	}
	
	public String[] roomList() {
		String[] roomList = new String[rooms.size()];
		rooms.keySet().toArray(roomList);
		return roomList;
	}
	
	public String runCommand(STUser user, String cmd, String args) {
		return coms.runCommand(user, cmd, args);
	}

	@Override
	public void conferenceCreated(Integer arg0, ConfInfo arg1, EncLevel arg2,
			STUserInstance[] arg3) {}

	@Override
	public void conferenceDenied(Integer arg0, int arg1) {}

	@Override
	public void conferenceDestroyed(Integer arg0, int arg1) {}

	@Override
	public void conferenceIntruded(Integer arg0, STUserInstance arg1, short arg2) {}

	@Override
	public void dataReceived(Integer arg0, boolean arg1, STLoginId arg2,
			int arg3, int arg4, byte[] arg5) {}

	@Override
	public void invitationDeclined(Integer arg0, STUserInstance arg1, int arg2) {}

	@Override
	public void invitedToConference(Integer arg0, ConfInfo arg1,
			STUserInstance arg2, EncLevel arg3, boolean arg4, String arg5) {}

	@Override
	public void serviceAvailable() {}

	@Override
	public void serviceUnavailable() {}

	@Override
	// I think that the boolean is something to do with encryption,
	// for now just pass it through to sendText()
	public void textReceived(Integer confId, boolean encrypted, STLoginId login,
			String text) {
		//System.out.println(login.getId() + ": " + text);
		String username = login.getCommunityName();
		System.out.println("username: " + username);
		if (text.toLowerCase().contains("blodgett")) {
			confService.sendText(confId, encrypted, "That's some undergrad shit");
		}
	}

	@Override
	public void userEntered(Integer arg0, STUserInstance arg1) {}

	@Override
	public void userLeft(Integer arg0, STLoginId arg1) {}
}
