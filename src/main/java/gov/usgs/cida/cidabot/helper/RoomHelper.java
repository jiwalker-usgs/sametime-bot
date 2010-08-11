package gov.usgs.cida.cidabot.helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.lotus.sametime.core.types.STUser;

public class RoomHelper {

	private final Integer confId;
	private final String confName;
	private Queue<String> history;
	private int historyLength;
	private Map<String, STUser> users;
	
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
	}
	
	public String getHistory() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i < history.size(); i++) {
			String head = history.remove();
			history.add(head);
			buf.append(head + "\n");
		}
		return buf.toString();
	}
	
	public void addUser(STUser stu) {
		if (!users.keySet().contains(stu)) {
			users.put(stu.getId().getId(), stu);
		}
	}
	
	public void removeUser(STUser stu) {
		users.remove(stu);
	}
	
	public STUser getUser(String id) {
		return users.get(id);
	}
}
