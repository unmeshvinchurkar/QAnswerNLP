package com.nlp;

import java.util.Map;


import edu.stanford.nlp.ling.CoreLabel;

public class SubjectWrapper extends AbstractWrapper {

	public SubjectWrapper(Map<String, CoreLabel> tokenMap, String subStr) {
		super(tokenMap, subStr);
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
