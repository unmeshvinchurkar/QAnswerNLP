package com.nlp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class ObjectWrapper  extends WordWrapper{

	private boolean noun = true;

	public ObjectWrapper(Map<String, CoreLabel> tokenMap, boolean noun, String objStr) {
		super(tokenMap, objStr);
		this.noun = noun;
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

	public int numOfWords() {
		return this.getWordString().split(" ").length;
	}

	public boolean isNoun() {
		return noun;
	}

	public void setNoun(boolean noun) {
		this.noun = noun;
	}

	public String getObjStr() {
		return this.getWordString();
	}

	public void setObjStr(String objStr) {
		this.setWordString(objStr);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (noun ? 1231 : 1237);
		result = prime * result + ((this.getWordString() == null) ? 0 : this.getWordString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectWrapper other = (ObjectWrapper) obj;
		if (noun != other.noun)
			return false;
		if (this.getWordString() == null) {
			if (other.getWordString() != null)
				return false;
		} else if (!this.getWordString().equals(other.getWordString()))
			return false;
		return true;
	}

}
