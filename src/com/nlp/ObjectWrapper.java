package com.nlp;

import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;

public class ObjectWrapper extends AbstractWrapper {

	private boolean noun = true;

	public ObjectWrapper(Map<String, CoreLabel> tokenMap, boolean noun, String objStr) {
		super(tokenMap, objStr);
		this.noun = noun;
	}

	public boolean isNoun() {
		return noun;
	}

	public boolean isObject() {
		return true;
	}

	public void setNoun(boolean noun) {
		this.noun = noun;
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
