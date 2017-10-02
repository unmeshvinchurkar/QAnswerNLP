package com.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;

public class DependencyExtractor {

	public static String SUBJECT = "nsubj";
	public static String OBJECT = "dobj";
	public static String IN = "prep_in";
	public static String OF = "prep_of";
	public static String VERB_VERB_PHRASE = "xcomp";
	public static String TIME_MOD = "tmod";
	public static String ADJ_NOUN = "amod";
	public static String VB_IN_NOUN = "nmod"; // recorded in India
	public static String COMPOUND_WORDS = "compound";

	private Map<String, CoreLabel> tokenMap = null;

	private Set<Concept> concepts = new LinkedHashSet<>();

	private Map<String, Set<String>> verb_Subject = new HashMap<>();
	private Map<String, Set<String>> verb_Object = new HashMap<>();

	private Map<String, Set<String>> verb_in_noun_map = new HashMap<>();
	private Map<String, Set<String>> adj_noun_map = new HashMap<>();
	private Map<String, Set<String>> verb_verb_map = new HashMap<>();

	private Map<String, Set<String>> compoundWords = new HashMap<>();

	public DependencyExtractor(Map<String, CoreLabel> tokenMap) {
		this.tokenMap = tokenMap;

	}

	public void extractDependencies(String collapsedDeps) throws IOException {

		BufferedReader bufReader = new BufferedReader(new StringReader(collapsedDeps));

		String line = null;
		while ((line = bufReader.readLine()) != null) {

			if (line.startsWith(SUBJECT)) {
				String verb_noun[] = getPair(line);

				Set<String> set = verb_Subject.get(verb_noun[0]);

				if (set == null) {
					set = new HashSet<>();
					verb_Subject.put(verb_noun[0], set);
				}

				set.add(verb_noun[1]);

			} else if (line.startsWith(OBJECT)) {

				String verb_noun[] = getPair(line);

				Set<String> set = verb_Object.get(verb_noun[0]);
				if (set == null) {
					set = new HashSet<>();
					verb_Object.put(verb_noun[0], set);
				}
				set.add(verb_noun[1]);

			} else if (line.startsWith(VB_IN_NOUN) || line.startsWith(IN)) {
				String verb_noun[] = getPair(line);

				Set<String> set = verb_in_noun_map.get(verb_noun[0]);
				if (set == null) {
					set = new HashSet<>();
					verb_in_noun_map.put(verb_noun[0], set);
				}
				set.add(verb_noun[1]);

			} else if (line.startsWith(ADJ_NOUN)) {
				String adj_noun[] = getPair(line);

				Set<String> set = adj_noun_map.get(adj_noun[0]);
				if (set == null) {
					set = new HashSet<>();
					adj_noun_map.put(adj_noun[0], set);
				}
				set.add(adj_noun[1]);
			}

			else if (line.startsWith(VERB_VERB_PHRASE)) {

				String verb_verb[] = getPair(line);

				Set<String> set = verb_verb_map.get(verb_verb[0]);
				if (set == null) {
					set = new HashSet<>();
					verb_verb_map.put(verb_verb[0], set);
				}
				set.add(verb_verb[1]);

			} else if (line.startsWith(COMPOUND_WORDS)) {
				String word_word[] = getPair(line);

				if (getIndex(word_word[0]) < getIndex(word_word[1])) {

					Set<String> set = compoundWords.get(word_word[0]);
					if (set == null) {
						set = new HashSet<>();
						compoundWords.put(word_word[0], set);
					}
					set.add(word_word[0] + " " + word_word[1]);
				} else {

					Set<String> set = compoundWords.get(word_word[1]);
					if (set == null) {
						set = new HashSet<>();
						compoundWords.put(word_word[1], set);
					}
					set.add(word_word[1] + " " + word_word[0]);
				}
			}
		}

		bufReader.close();
	}

	public void generateConcepts() {

		AbstractWrapper nul = null;

		Set<String> keys = adj_noun_map.keySet();

		for (String adj : keys) {
			Set<String> nouns = adj_noun_map.get(adj);

			for (String noun : nouns) {
				Concept concept = new Concept(nul, new VerbWrapper(tokenMap, false, getWord(adj)),
						new ObjectWrapper(tokenMap, true, getWord(noun)));
				concepts.add(concept);
			}
		}

		keys = verb_in_noun_map.keySet();

		for (String verb : keys) {
			Set<String> nouns = verb_in_noun_map.get(verb);

			for (String noun : nouns) {
				Concept concept = new Concept(nul, new VerbWrapper(tokenMap, true, getWord(verb)),
						new ObjectWrapper(tokenMap, true, getWord(noun)));
				concepts.add(concept);
			}
		}

		keys = verb_verb_map.keySet();

		for (String verb : keys) {
			Set<String> verbs = verb_verb_map.get(verb);

			for (String v : verbs) {
				Concept concept = new Concept(nul, new VerbWrapper(tokenMap, true, getWord(verb)),
						new ObjectWrapper(tokenMap, false, getWord(v)));
				concepts.add(concept);
			}
		}

		keys = verb_Subject.keySet();

		for (String verb : keys) {

			Set<String> subjs = verb_Subject.remove(verb);

			if (verb_Object.containsKey(verb)) {

				Set<String> objs = verb_Object.remove(verb);

				for (String sub : subjs) {
					for (String obj : objs) {
						Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
								new VerbWrapper(tokenMap, true, getWord(verb)),
								new ObjectWrapper(tokenMap, true, getWord(obj)));
						concepts.add(concept);
					}
				}
			} else {

				for (String sub : subjs) {
					Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
							new VerbWrapper(tokenMap, true, getWord(verb)));
					concepts.add(concept);
				}
			}
		}

		keys = verb_Object.keySet();

		for (String verb : keys) {

			Set<String> objs = verb_Object.get(verb);

			for (String obj : objs) {
				Concept concept = new Concept(nul, new VerbWrapper(tokenMap, true, getWord(verb)),
						new ObjectWrapper(tokenMap, true, getWord(obj)));
				concepts.add(concept);
			}
		}
	}

	public boolean areCompoundWords(String w1, String w2) {

		if (compoundWords.containsKey(w1)) {
			Set<String> set = compoundWords.get(w1);
			for (String w : set) {
				if (w.contains(w2)) {
					return true;
				}
			}
		}

		else if (compoundWords.containsKey(w2)) {
			Set<String> set = compoundWords.get(w2);
			for (String w : set) {
				if (w.contains(w1)) {
					return true;
				}
			}
		}
		return false;
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

	private String[] getPair(String dependency) {
		int i1 = dependency.indexOf("(");
		int i2 = dependency.indexOf(")");

		String pair[] = dependency.substring(i1 + 1, i2).split(",");

		if (pair != null) {
			int i = 0;
			for (String s : pair) {
				pair[i++] = s.trim();
			}
		}
		return pair;
	}

}
