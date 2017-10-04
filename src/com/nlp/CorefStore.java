package com.nlp;

import java.util.HashMap;
import java.util.Map;

public class CorefStore {

	private Map<String, Map<String, String>> senNo_corefMap = new HashMap<>();

	public CorefStore() {

	}

	public String getCoRef(String sentNo, String pronoun) {
		Map<String, String> coRefMap = senNo_corefMap.get(sentNo);

		if (coRefMap == null) {
			return null;
		}

		return coRefMap.get(pronoun.trim());

	}

	public void addCoRef(String sentNo, String pronoun, String noun) {

		Map<String, String> coRefMap = senNo_corefMap.get(sentNo);

		if (coRefMap == null) {
			coRefMap = new HashMap<>();
			senNo_corefMap.put(sentNo, coRefMap);
		}

		coRefMap.put(pronoun.trim(), noun.trim());
	}

}
