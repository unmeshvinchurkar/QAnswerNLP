package com.nlp;

import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;

public class VerbWrapper extends AbstractWrapper {

	private boolean verb = false;

	public VerbWrapper(Map<String, CoreLabel> tokenMap, boolean verb, String verbStr) {
		super(tokenMap, verbStr);
		this.verb = verb;
	}

	public boolean isVerb() {
		return verb;
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
