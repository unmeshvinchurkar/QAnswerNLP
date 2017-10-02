package com.nlp;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public abstract class AbstractWrapper {

	private Map<String, CoreLabel> tokenMap = null;
	private String wordString = "";

	public AbstractWrapper(Map<String, CoreLabel> tokenMap, String wordString) {
		this.tokenMap = tokenMap;
		this.wordString = wordString;
	}

	public AbstractWrapper(Map<String, CoreLabel> tokenMap) {
		this.tokenMap = tokenMap;
	}

	public String[] getAllWords() {
		return this.getWordString().split(" ");
	}

	public String getNthWord(int index) {
		return this.getWordString().split(" ")[index];
	}

	public boolean doWordsHaveSamePos() {
		return getAllPos().size() <= 1;
	}

	public boolean doWordsHaveSameNER() {
		return getAllNER().size() <= 1;
	}

	public boolean containsSingleWord() {
		return numOfWords() == 1;
	}

	public boolean isEmpty() {
		return numOfWords() == 0;
	}

	public String getLemma(String word) {		 
		word = getWord(word);
		CoreLabel token = tokenMap.get(word);
		String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
		return lemma;
	}

	public Collection<String> getAllNER() {

		int num = numOfWords();
		Set<String> ners = new LinkedHashSet<String>();

		for (int i = 0; i < num; i++) {
			ners.add(getNER(i));
		}
		return ners;
	}

	public Collection<String> getAllPos() {

		int num = numOfWords();
		Set<String> poss = new LinkedHashSet<String>();

		for (int i = 0; i < num; i++) {
			poss.add(getPos(i));
		}
		return poss;
	}

	public String getPos(int index) {

		String word = getNthWord(index);
		CoreLabel token = this.getTokenMap().get(word.trim().toLowerCase());

		if (token == null) {
			return "";
		}

		return token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
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

	public Collection<String> getWordsByNER(String ner) {

		int num = numOfWords();
		Set<String> words = new LinkedHashSet<String>();

		for (int i = 0; i < num; i++) {
			String wNer = getNER(i);

			if (ner.equals(wNer)) {
				words.add(getNthWord(i));
			}
		}

		return words;
	}

	public Collection<String> getWordsByPos(String pos) {

		int num = numOfWords();
		Set<String> words = new LinkedHashSet<String>();

		for (int i = 0; i < num; i++) {
			String wPos = getPos(i);

			if (pos.equals(wPos)) {
				words.add(getNthWord(i));
			}
		}

		return words;
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

	public static int getIndex(String word) {
		int i = word.indexOf("-");
		return Integer.valueOf(word.substring(i + 1));
	}

	public static String getWord(String word) {
		int i = word.indexOf("-");

		if (i > 0) {
			return word.substring(0, i);
		}
		return word;
	}

	public boolean isSubject() {
		return false;
	}

	public boolean isVerb() {
		return false;
	}

	public boolean isObject() {
		return false;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[word=" + wordString + "]";
	}

}
