package gov.usgs.cida.cidabot;

import java.util.Collection;
import com.lotus.sametime.im.Im;
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


/**
 * This is a much simplified version of the Conference manager
 * It has fewer associated commands and should be less fragile to version
 * changes.
 * @author jwalker
 */
public class RoomManager {

	private Map<String, RoomHelper> nameRoomMap;
	private Map<Im, RoomHelper> userRoomMap;
	
	private static Logger log = Logger.getLogger(RoomManager.class);

	public RoomManager() {
		nameRoomMap = new LinkedHashMap<String, RoomHelper>();
		userRoomMap = new LinkedHashMap<Im, RoomHelper>();
	}

	public void addToChat(Im im, String text) {
		RoomHelper room = userRoomMap.get(im);
		if (room != null) {
			STUser userObj = room.getUser(im.getPartner().getId().getId());
			String userName = "unknown";
			if (userObj != null) {
				userName = userObj.getName();
				log.debug("username is " + userName);
			}
			try {
				room.writeToLog(userName, text);
			}
			catch (IOException ioe) {
				log.debug(ioe.getMessage());
			}
			String chatLine = userName + ": " + text;
			room.sendToUsers(im, chatLine);
		}
	}

	public boolean enterRoom(Im im, String roomName) {
		leaveRoom(im);
		String lcRoomName = roomName.toLowerCase();
		if (nameRoomMap.containsKey(lcRoomName)) {
			userRoomMap.put(im, nameRoomMap.get(roomName));
		}
		else {
			RoomHelper room = new RoomHelper(0, roomName, DEFAULT_HISTORY_SIZE, false);
			room.addUser(im);
			nameRoomMap.put(lcRoomName, room);
			userRoomMap.put(im, room);
		}
		return true;
	}
	
	public boolean leaveRoom(Im im) {
		RoomHelper room = userRoomMap.get(im);
		if (room != null) {
			room.removeUser(im);
			if (room.getUsers().isEmpty()) {
				nameRoomMap.remove(room.getName());
			}
			userRoomMap.remove(im);
		}
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
}
