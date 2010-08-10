package gov.usgs.cida.cidabot.helper;

import java.util.LinkedList;
import java.util.Queue;

public class RoomHelper {

	private final Integer confId;
	private final String confName;
	private Queue<String> history;
	private int historyLength;
	
	public RoomHelper(Integer id, String confName, int historyLength) {
		this.confId = id;
		this.confName = confName;
		this.history = new LinkedList<String>();
		this.historyLength = historyLength;
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
		while (history.size() > 0) {
			buf.append(history.remove() + "\n");
		}
		return buf.toString();
	}
}
