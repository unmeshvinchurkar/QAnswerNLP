package com.nlp;

import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class SubjectWrapper extends WordWrapper {

	public SubjectWrapper(Map<String, CoreLabel> tokenMap, String subStr) {
		super(tokenMap, subStr);
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

	public String getSubStr() {
		return this.getWordString();
	}

	public void setSubStr(String subStr) {
		this.setWordString(subStr);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		SubjectWrapper other = (SubjectWrapper) obj;
		if (this.getWordString() == null) {
			if (other.getWordString() != null)
				return false;
		} else if (!this.getWordString().equals(other.getWordString()))
			return false;
		return true;
	}

}
