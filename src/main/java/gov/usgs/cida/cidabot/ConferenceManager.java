package gov.usgs.cida.cidabot;

import java.io.EOFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import gov.usgs.cida.cidabot.helper.KeywordHelper;
import gov.usgs.cida.cidabot.helper.RoomHelper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lotus.sametime.conf.ConfInfo;
import com.lotus.sametime.conf.ConfListener;
import com.lotus.sametime.conf.ConfService;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.core.types.STLoginId;
import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.core.types.STUserInstance;
import com.lotus.sametime.places.Place;
import com.lotus.sametime.places.PlacesConstants;
import com.lotus.sametime.places.PlacesService;
import com.lotus.sametime.conf.ConfsTable;

import static gov.usgs.cida.cidabot.BotConstants.*;

public class ConferenceManager implements ConfListener {

	private ConfService confService;
	private Map<String, RoomHelper> nameRoomMap;
	private Map<Integer, RoomHelper> intRoomMap;
	private STLoginId myLoginId;
	private ConfsTable confsTable;
	
	private PlacesService placesService;
	
	private static Logger log = Logger.getLogger(ConferenceManager.class);

	public ConferenceManager(STSession session) {
		nameRoomMap = new LinkedHashMap<String, RoomHelper>();
		intRoomMap = new LinkedHashMap<Integer, RoomHelper>();

		confService = (ConfService)session.getCompApi(ConfService.COMP_NAME);
		confService.addConfListener(this);
		placesService = (PlacesService)session.getCompApi(PlacesService.COMP_NAME);
		confsTable = new ConfsTable();
	}
	
	public boolean createConf(String roomName) throws EOFException {
		
		String lcRoomName = roomName.toLowerCase();
		if (nameRoomMap.containsKey(lcRoomName)) {
			return false;
		}
		ConfInfo info = new ConfInfo(roomName, roomName);
		
		if (confService != null) {
			Integer confId = confService.createConference(info, EncLevel.ENC_LEVEL_NONE);
			//confService.autoInviteToConference(arg0, arg1, arg2, arg3, arg4);
			log.debug("create conference returned: " + confId);
			confService.joinToConference(confId);
			RoomHelper confObj = new RoomHelper(confId, roomName, DEFAULT_HISTORY_SIZE, false);
			nameRoomMap.put(lcRoomName, confObj);
			intRoomMap.put(confId, confObj);
		}
		return true;
	}
	
	public boolean inviteConf(String roomName, STUserInstance user) {
		String lcRoomName = roomName.toLowerCase();
		if (!nameRoomMap.containsKey(lcRoomName)) {
			return false;
		}
		else {
			Integer confId = nameRoomMap.get(lcRoomName).getId();
			confService.inviteToConference(confId, user.getId(), roomName, user.getLoginId(), roomName);
			return true;
			
			//confService.autoInviteToConference(confId, user.getId(), INVITE_TEXT, null, INVITE_TEXT);
		}
	}
	
	public boolean removeConf(String roomName) {
		String lcRoomName = roomName.toLowerCase();
		if (!nameRoomMap.containsKey(lcRoomName)) {
			return false;
		}
		
		Integer confId = nameRoomMap.get(lcRoomName).getId();
		confService.destroyConference(confId);
		nameRoomMap.remove(lcRoomName);
		intRoomMap.remove(confId);
		return true;
	}
	
	public String[] roomList() {
		String[] roomList = new String[nameRoomMap.size()];
		Iterator<String> it = nameRoomMap.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			roomList[i] = nameRoomMap.get(it.next()).getName();
			i++;
		}
		
		return roomList;
	}
	
	public String getHistory(String roomName) {
		RoomHelper room = nameRoomMap.get(roomName);
		if (room != null) {
			return room.getHistory();
		}
		else {
			return "";
		}
	}

	@Override
	public void conferenceCreated(Integer confId, ConfInfo info, EncLevel encrypt,
			STUserInstance[] users) {
		Place place = placesService.createPlace(info.getName(), info.getDisplayName(), encrypt, ImTypes.IM_TYPE_CHAT, PlacesConstants.PLACE_PUBLISHED);
		log.debug("entered room" + place.getName());
		place.enter();
	}

	@Override
	public void conferenceDenied(Integer arg0, int arg1) {}

	@Override
	public void conferenceDestroyed(Integer arg0, int arg1) {
		RoomHelper room = intRoomMap.get(arg0);
		room.closeRoom();
	}

	@Override
	public void conferenceIntruded(Integer confId, STUserInstance userInst, short dontknowwhatthisis) {
	}

	@Override
	public void dataReceived(Integer arg0, boolean arg1, STLoginId arg2,
			int arg3, int arg4, byte[] arg5) {}

	@Override
	public void invitationDeclined(Integer arg0, STUserInstance arg1, int arg2) {}

	@Override
	public void invitedToConference(Integer confId, ConfInfo info,
			STUserInstance userInst, EncLevel enc, boolean arg4, String arg5) {
	}

	@Override
	public void serviceAvailable() {}

	@Override
	public void serviceUnavailable() {}

	@Override
	// I think that the boolean is something to do with encryption,
	// for now just pass it through to sendText()
	public void textReceived(Integer confId, boolean encrypted, STLoginId login,
			String text) {
		RoomHelper rh = intRoomMap.get(confId);
		if (rh != null) {
			log.debug("user " + login.getId() + " sent message " + text);
			STUser userObj = rh.getUser(login.getId());
			String userName = "unknown";
			if (userObj != null) {
				userName = userObj.getName();
				log.debug("username is " + userName);
			}
			try {
					rh.writeToLog(userName, text);
				}
			catch (IOException ioe) {
					log.debug(ioe.getMessage());
			}
		}
		String keywordResult = KeywordHelper.checkForKeywords(text);
		if (keywordResult != null && !login.equals(getMyLoginId())) {
			confService.sendText(confId, encrypted, keywordResult);
		}
	}

	@Override
	public void userEntered(Integer confId, STUserInstance userInst) {
		confsTable.put(confId, (short)(intRoomMap.size()+1), userInst.getLoginId());
		RoomHelper room = intRoomMap.get(confId);
		log.debug("user entered: " + userInst.getDisplayName());
		if (room != null) {
			room.addUser(userInst);
			log.debug("user joining has id " + userInst.getLoginId().getId());
		}
	}

	@Override
	public void userLeft(Integer confId, STLoginId login) {
		//confsTable.remove(confId, STUserStatus.ST_USER_STATUS_DONTCARE, userInst.getLoginId());
		RoomHelper room = intRoomMap.get(confId);
		if (room != null) { 
			room.removeUser(login.getId());
		}
	}
	
	public void setMyLoginId(STLoginId myLoginId) {
		this.myLoginId = myLoginId;
	}

	public STLoginId getMyLoginId() {
		return myLoginId;
	}
}
