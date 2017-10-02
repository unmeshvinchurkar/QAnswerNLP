package com.nlp;

public class Concept {

	private AbstractWrapper subject;
	private AbstractWrapper verb;
	private AbstractWrapper object;

	public Concept(AbstractWrapper subject, AbstractWrapper verb) {
		this.subject = subject;
		this.verb = verb;
	}

	public Concept(AbstractWrapper subject, AbstractWrapper verb, AbstractWrapper object) {
		this.subject = subject;
		this.verb = verb;
		this.object = object;
	}

	public AbstractWrapper getSubject() {
		return subject;
	}

	public void setSubject(AbstractWrapper subject) {
		this.subject = subject;
	}

	public AbstractWrapper getVerb() {
		return verb;
	}

	public void setVerb(AbstractWrapper verb) {
		this.verb = verb;
	}

	public AbstractWrapper getObject() {
		return object;
	}

	public void setObject(AbstractWrapper object) {
		this.object = object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((toString() == null) ? 0 : toString().hashCode());
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
		Concept other = (Concept) obj;
		if (toString() == null) {
			if (other.toString() != null)
				return false;
		} else if (!toString().equals(other.toString()))
			return false;
		return true;
	}

	@Override
	public String toString() {

		String str = "";

		if (subject != null || subject.getWordString().length() > 0) {
			str = subject.getWordString();
		}

		if (verb != null || verb.getWordString().length() > 0) {
			
			if (str.length() > 0) {
				str = "-" + verb.getWordString();
			} else {
				str = verb.getWordString();
			}
		}

		if (object != null || object.getWordString().length() > 0) {
			str = "-" + object.getWordString();
		}

		return str;
	}

}
