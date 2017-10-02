package com.nlp;

public class Concept {

	private String subject;
	private String verb;
	private String object;

	public Concept(String subject, String verb) {
		this.subject = subject;
		this.verb = verb;
	}

	public Concept(String subject, String verb, String object) {
		this.subject = subject;
		this.verb = verb;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
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

		if (subject != null || subject.length() > 0) {
			str = subject;
		}

		if (verb != null || verb.length() > 0) {
			
			if (str.length() > 0) {
				str = "-" + verb;
			} else {
				str = verb;
			}
		}

		if (object != null || object.length() > 0) {
			str = "-" + object;
		}

		return str;
	}

}
