package com.nlp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public abstract class WordWrapper {

	private Map<String, CoreLabel> tokenMap = null;
	private String wordString =  "";	
	

	public WordWrapper(Map<String, CoreLabel> tokenMap, String wordString) {
		super();
		this.tokenMap = tokenMap;
		this.wordString = wordString;
	}

	public WordWrapper(Map<String, CoreLabel> tokenMap) {
		this.tokenMap = tokenMap;
	}
	
	public String getNthWord(int index) {
		return this.getWordString().split(" ")[index];
	}

	public Collection<String> getAllNER() {

		int num = numOfWords();
		Set<String> ners = new HashSet<String>();

		for (int i = 0; i < num; i++) {
			ners.add(getNER(i));
		}
		return ners;
	}
	
	public String getNER(int index) {
		String word = getNthWord(index);
		CoreLabel token = this.getTokenMap().get(word.trim().toLowerCase());

		if (token == null) {
			return "";
		}

		String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
		return ner;
	}


	public int numOfWords() {
		return this.getWordString().split(" ").length;
	}
	

	public String getWordString() {
		return wordString;
	}

	public void setWordString(String wordString) {
		this.wordString = wordString;
	}

	public Map<String, CoreLabel> getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(Map<String, CoreLabel> tokenMap) {
		this.tokenMap = tokenMap;
	}

}
