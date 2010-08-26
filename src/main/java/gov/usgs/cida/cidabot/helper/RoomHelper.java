package gov.usgs.cida.cidabot.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.core.types.STUserInstance;

public class RoomHelper {

	private final Integer confId;
	private final String confName;
	private Queue<String> history;
	private int historyLength;
	private Map<String, STUser> users;
	
	private static Logger log = Logger.getLogger(RoomHelper.class);
	
	public RoomHelper(Integer id, String confName, int historyLength) {
		this.confId = id;
		this.confName = confName;
		this.history = new LinkedList<String>();
		this.historyLength = historyLength;
		this.users = new HashMap<String, STUser>();
	}
	
	public Integer getId() {
		return confId;
	}
	
	public String getName() {
		return confName;
	}
	
	public void addHistory(String str) {
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
		log.info("added \"" + str + "\" to history");
	}
	
	public String getHistory() {
		StringBuffer buf = new StringBuffer();
		Iterator<String> it = history.iterator();
		//for (int i=0; i < history.size(); i++) {
		while (it.hasNext()) {
			String head = it.next();
			//history.add(head);
			buf.append(head + "\n");
		}
		return buf.toString();
	}
	
	public void addUser(STUserInstance stu) {
		log.debug("user " + stu.getName() + " has id " + stu.getId().getId());
		if (!users.keySet().contains(stu)) {
			users.put(stu.getLoginId().getId(), stu);
		}
	}
	
	public void removeUser(String id) {
		users.remove(id);
	}
	
	public STUser getUser(String id) {
		return users.get(id);
	}
}