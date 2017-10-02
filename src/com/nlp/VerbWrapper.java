package com.nlp;

import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class VerbWrapper extends WordWrapper{

	private boolean verb = false;

	public VerbWrapper(Map<String, CoreLabel> tokenMap, boolean verb, String verbStr) {
		super(tokenMap, verbStr);
		this.verb = verb;
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

	public int numOfWords() {
		return this.getWordString().split(" ").length;
	}

	public boolean isVerb() {
		return verb;
	}

	public void setVerb(boolean verb) {
		this.verb = verb;
	}

	public String getVerbStr() {
		return this.getWordString();
	}

	public void setVerbStr(String verbStr) {
		this.setWordString(verbStr);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (verb ? 1231 : 1237);
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
		VerbWrapper other = (VerbWrapper) obj;
		if (verb != other.verb)
			return false;
		if (this.getWordString() == null) {
			if (other.getWordString() != null)
				return false;
		} else if (!this.getWordString().equals(other.getWordString()))
			return false;
		return true;
	}

}
