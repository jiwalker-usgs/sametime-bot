package gov.usgs.cida.cidabot.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KeywordHelper {
	
	public static Map<String, List<String>> keywords = new HashMap<String, List<String>>();
	public static Random random = new Random();
	
	public static String checkForKeywords(String line) {
		for (String keyword : keywords.keySet()) {
			if (line.toLowerCase().contains(keyword)) {
				return getRandom(keywords.get(keyword));
			}
		}
		return null;
	}
	
	public static synchronized void addToKeywordMap(String keyword, String phrase) {
		if (keywords.containsKey(keyword)) {
			// add to list
			keywords.get(keyword).add(phrase);
		}
		else {
			// create list and add
			ArrayList<String> list = new ArrayList<String>();
			list.add(phrase);
			keywords.put(keyword, list);
		}
	}
	
	private static String getRandom(List<String> phraseList) {
		return phraseList.get(random.nextInt(phraseList.size()));
	}
}
