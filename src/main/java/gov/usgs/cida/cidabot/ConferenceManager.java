package gov.usgs.cida.cidabot;

import java.util.HashMap;
import java.util.Map;

import com.lotus.sametime.conf.ConfInfo;
import com.lotus.sametime.conf.ConfService;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.types.STLoginId;
import com.lotus.sametime.core.types.STUser;

import static gov.usgs.cida.cidabot.BotConstants.*;

public class ConferenceManager {
	
	private ConfService confService;
	private Map<String, Integer> rooms;
	private BotCommands coms;
	
	public ConferenceManager(STSession session) {
		rooms = new HashMap<String, Integer>();
		confService = (ConfService)session.getCompApi(ConfService.COMP_NAME);
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
			rooms.put(roomName, confId);
		}
		return true;
	}
	
	public boolean inviteConf(String roomName, STUser user) {
		if (!rooms.containsKey(roomName)) {
			return false;
		}
		else {
			Integer confId = rooms.get(roomName);
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
		
		Integer confId = rooms.get(roomName);
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
}
