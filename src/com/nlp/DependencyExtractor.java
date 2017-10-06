package com.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;

public class DependencyExtractor {
	public static final String SUBJECT_PASSIVE = "nsubjpass";
	public static final String VERB_PASSIVE = "auxpass";
	public static final String SUBJECT = "nsubj";
	public static final String INDIRECT_SUBJECT = "acl";
	public static final String OBJECT = "dobj";
	public static final String IN = "prep_in";
	public static final String OF = "prep_of";
	public static final String VERB_VERB_PHRASE = "xcomp";
	public static final String TIME_MOD = "tmod";
	public static final String ADJ_NOUN = "amod";
	public static final String NOUN_IN_NOUN = "nmod:in"; // recorded in India
	public static final String NOUN_OF_NOUN = "nmod:of";
	public static final String VB_FOR_NOUN = "nmod:for";
	//public static final String NOUN_VERB = "acl";
	public static final String COMPOUND_WORDS = "compound";

	public static final String VERB_COMPLEMENT = "xcomp";

	private Map<String, CoreLabel> tokenMap = null;

	private Set<Concept> concepts = new LinkedHashSet<>();

	// "nsubj"
	private Map<String, Set<String>> verb_Subject = new HashMap<>();
	
	//nsubjpass
	private Map<String, Set<String>> verb_SubjectPassive = new HashMap<>();
	
	//auxpass
	private Map<String, String> verb_passive = new HashMap<>();
	
	//auxpass with case
	private Map<String, String> verb_passiveCase = new HashMap<>();

	// "dobj"
	private Map<String, Set<String>> verb_Object = new HashMap<>();

	// "nmod:in"
	private Map<String, Set<String>> noun_in_noun_map = new HashMap<>();

	// "nmod:of"
	private Map<String, Set<String>> noun_of_noun_map = new HashMap<>();

	// "amod"
	private Map<String, Set<String>> adj_noun_map = new HashMap<>();

	// "xcomp"
	private Map<String, Set<String>> verb_verb_map = new HashMap<>();

	// acl acl:relcl
	private Map<String, Set<String>> verb_noun_map = new HashMap<>();

	// xcomp
	private Map<String, Set<String>> verb_comp_map = new HashMap<>();

	private Set<String> compoundWords = new HashSet<>();

	private CorefStore coRefStore = null;

	public DependencyExtractor(Annotation document) {

		coRefStore = buildCorefs(document);
	}

	public Set<String> getCompoundWords() {
		return compoundWords;
	}

	public Set<Concept> getConcepts() {
		return concepts;
	}

	public Set<Concept> extractDependencies(Map<String, CoreLabel> tokenMap,String collapsedDeps, int sentenceNo) {
		
		this.tokenMap = tokenMap;
		
		System.out.println(collapsedDeps);
		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(collapsedDeps));
			
			

			String line = null;
			while ((line = bufReader.readLine()) != null) {

				if (line.startsWith(VERB_COMPLEMENT)) {
					String verb_compVerb[] = getPair(line);
					
					Set<String> set = verb_comp_map.get(verb_compVerb[0]);
					
					if (set == null) {
						set = new HashSet<>();
						verb_comp_map.put(verb_compVerb[0], set);
					}
					
					set.add(verb_compVerb[1]);
				}
				
				else if (line.startsWith(SUBJECT_PASSIVE) ){
					String verb_noun[] = getPair(line);

					Set<String> set = verb_SubjectPassive.get(verb_noun[0]);

					if (set == null) {
						set = new HashSet<>();
						verb_SubjectPassive.put(verb_noun[0], set);
					}

					set.add(coRefStore.getCoRef(String.valueOf(sentenceNo), verb_noun[1], verb_noun[1]));

				}
				
				else if (line.startsWith(VERB_PASSIVE) ){
					String verb_verb[] = getPair(line);	
					
					bufReader.mark(2);
					
					String nextLine = bufReader.readLine();

					if (nextLine.startsWith("case")) {
						String casePair[] = getPair(nextLine);
						verb_passiveCase.put(verb_verb[0], casePair[0]);

						bufReader.mark(2);
						nextLine = bufReader.readLine();

						if (!nextLine.startsWith("nmod:")) {
							bufReader.reset();
						}

					} else {
						bufReader.reset();
						verb_passive.put(verb_verb[0], verb_verb[1]);
					}
					
				}
				
				else if (line.startsWith(INDIRECT_SUBJECT)) {
					String noun_verb[] = getPair(line);

					Set<String> set = verb_Subject.get(noun_verb[1]);

					if (set == null) {
						set = new HashSet<>();
						verb_Subject.put(noun_verb[1], set);
					}

					set.add(coRefStore.getCoRef(String.valueOf(sentenceNo), noun_verb[0], noun_verb[0]));

				}

				else if ((line.startsWith(SUBJECT))) {
					String verb_noun[] = getPair(line);

					Set<String> set = verb_Subject.get(verb_noun[0]);

					if (set == null) {
						set = new HashSet<>();
						verb_Subject.put(verb_noun[0], set);
					}

					set.add(coRefStore.getCoRef(String.valueOf(sentenceNo), verb_noun[1], verb_noun[1]));

				}

//			 else if (line.startsWith(NOUN_VERB)) {
//					String verb_noun[] = getPair(line);
//
//					Set<String> set = verb_noun_map.get(verb_noun[0]);
//
//					if (set == null) {
//						set = new HashSet<>();
//						verb_noun_map.put(verb_noun[0], set);
//					}
//
//					set.add(coRefStore.getCoRef(String.valueOf(sentenceNo), verb_noun[1], verb_noun[1]));
//
//				} 			 
			 
			 else if (line.startsWith(OBJECT) || line.startsWith(VB_FOR_NOUN)) {

					String verb_noun[] = getPair(line);

					if (!isVerb(getPos(getWord(verb_noun[1])))) {

						Set<String> set = verb_Object.get(verb_noun[0]);
						if (set == null) {
							set = new HashSet<>();
							verb_Object.put(verb_noun[0], set);
						}
						set.add(coRefStore.getCoRef(String.valueOf(sentenceNo), verb_noun[1], verb_noun[1]));
					}

				} else if (line.startsWith(NOUN_IN_NOUN) || line.startsWith(IN)) {
					String noun_noun[] = getPair(line);

					Set<String> set = noun_in_noun_map.get(noun_noun[0]);
					if (set == null) {
						set = new HashSet<>();
						noun_in_noun_map
								.put(coRefStore.getCoRef(String.valueOf(sentenceNo), noun_noun[0], noun_noun[0]), set);
					}
					set.add(coRefStore.getCoRef(String.valueOf(sentenceNo), noun_noun[1], noun_noun[1]));

				}

				else if (line.startsWith(NOUN_OF_NOUN)) {
					String noun_noun[] = getPair(line);

					String noun0 = coRefStore.getCoRef(String.valueOf(sentenceNo), noun_noun[0], noun_noun[0]);
					String noun1 = coRefStore.getCoRef(String.valueOf(sentenceNo), noun_noun[1], noun_noun[1]);

					Set<String> set = noun_of_noun_map.get(noun0);
					if (set == null) {
						set = new HashSet<>();
						noun_of_noun_map.put(noun0, set);
					}
					set.add(noun1);
				}

				else if (line.startsWith(ADJ_NOUN)) {
					String adj_noun[] = getPair(line);

					String noun = coRefStore.getCoRef(String.valueOf(sentenceNo), adj_noun[1], adj_noun[1]);

					Set<String> set = adj_noun_map.get(noun);
					if (set == null) {
						set = new HashSet<>();
						adj_noun_map.put(noun, set);
					}
					set.add(adj_noun[0]);
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

						compoundWords.add(word_word[0] + " " + word_word[1]);
					} else {

						compoundWords.add(word_word[1] + " " + word_word[0]);
					}
				}

				sentenceNo++;
			}

			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		generateConcepts();

		return concepts;
	}

	public void generateConcepts() {

		AbstractWrapper nul = null;

		Set<String> keys = adj_noun_map.keySet();

		// for (String adj : keys) {
		// Set<String> nouns = adj_noun_map.get(adj);
		//
		// for (String noun : nouns) {
		// Concept concept = new Concept(nul, new VerbWrapper(tokenMap, false,
		// getWord(adj)),
		// new ObjectWrapper(tokenMap, true, getWord(noun)));
		// concepts.add(concept);
		// }
		// }
		
		
		keys = verb_SubjectPassive.keySet();
		
		
		for (String verb : keys) {

			Set<String> subjs = verb_SubjectPassive.get(verb);

			if (verb_passive.containsKey(verb)) {

				String auxVerb = verb_passive.get(verb);

				for (String sub : subjs) {					
						Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
								new VerbWrapper(tokenMap, true, getWord(auxVerb)),
								new ObjectWrapper(tokenMap, false, getWord(verb)));
						concepts.add(concept);
					}				
			}
			else if(verb_passiveCase.containsKey(verb)){
				String noun = verb_passiveCase.get(verb);
				
				for (String sub : subjs) {					
					Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
							new VerbWrapper(tokenMap, true, getWord(verb)),
							new ObjectWrapper(tokenMap, false, getWord(noun)));
					concepts.add(concept);
				}				
			}
		}

		keys = noun_in_noun_map.keySet();

		for (String noun : keys) {
			Set<String> nouns = noun_in_noun_map.get(noun);

			for (String noun1 : nouns) {
				Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(noun)),
						new VerbWrapper(tokenMap, true, getWord("in")),
						new ObjectWrapper(tokenMap, true, getWord(noun1)));
				concepts.add(concept);
			}
		}

		keys = verb_noun_map.keySet();

		for (String noun : keys) {
			Set<String> verbs = verb_noun_map.get(noun);

			for (String verb : verbs) {
				Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(noun)),
						new VerbWrapper(tokenMap, true, getWord(verb)), nul);
				concepts.add(concept);
			}
		}

		keys = noun_of_noun_map.keySet();

		for (String noun : keys) {
			Set<String> nouns = noun_of_noun_map.get(noun);

			for (String noun1 : nouns) {
				Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(noun)),
						new VerbWrapper(tokenMap, true, "of"), new ObjectWrapper(tokenMap, true, getWord(noun1)));
				concepts.add(concept);
			}
		}

		// keys = verb_verb_map.keySet();
		//
		// for (String verb : keys) {
		// Set<String> verbs = verb_verb_map.get(verb);
		//
		// for (String v : verbs) {
		// Concept concept = new Concept(nul, new VerbWrapper(tokenMap, true,
		// getWord(verb)),
		// new ObjectWrapper(tokenMap, false, getWord(v)));
		// concepts.add(concept);
		// }
		// }

		Set<String> toBeRemovedObj = new HashSet<>();

		keys = verb_Subject.keySet();

		for (String verb : keys) {

			Set<String> subjs = verb_Subject.get(verb);

			if (verb_Object.containsKey(verb)) {

				Set<String> objs = verb_Object.get(verb);
				toBeRemovedObj.add(verb);

				for (String sub : subjs) {
					for (String obj : objs) {
						Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
								new VerbWrapper(tokenMap, true, getWord(verb)),
								new ObjectWrapper(tokenMap, true, getWord(obj)));
						concepts.add(concept);
					}
				}
			} else if (verb_comp_map.containsKey(verb)) {

				// If verb complement exists then use it

				Set<String> compVerbSet = verb_comp_map.get(verb);
				
				boolean keyFound = false;
				for(String compVerb: compVerbSet){

				if (verb_Object.containsKey(compVerb)) {
					
					keyFound = true;

					Set<String> objs = verb_Object.get(compVerb);
					toBeRemovedObj.add(compVerb);
					toBeRemovedObj.add(verb);

					for (String sub : subjs) {
						for (String obj : objs) {
							Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
									new VerbWrapper(tokenMap, true, getWord(compVerb)),
									new ObjectWrapper(tokenMap, true, getWord(obj)));
							concepts.add(concept);
						}
					}
				}
				}

				if (!keyFound) {
					for (String sub : subjs) {
						Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
								new VerbWrapper(tokenMap, true, getWord(verb)));
						concepts.add(concept);
					}
				}			
			}

			else {

				for (String sub : subjs) {
					Concept concept = new Concept(new SubjectWrapper(tokenMap, getWord(sub)),
							new VerbWrapper(tokenMap, true, getWord(verb)));
					concepts.add(concept);
				}
			}
		}

		// Remove used verbs
		for (String k : toBeRemovedObj) {
			verb_Object.remove(k);
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

		for (String compoundWord : compoundWords) {
			if (compoundWord.contains(w1) && compoundWord.contains(w2)) {
				return true;
			}
		}
		return false;
	}

	public Set<String> lookUpCompoundWords(String word) {
		Set<String> words = new HashSet<>();

		for (String compoundWord : compoundWords) {
			if (compoundWord.contains(word)) {
				words.add(compoundWord);
			}
		}

		return words;
	}

	public boolean isCompoundWord(String word) {

		for (String compoundWord : compoundWords) {
			if (compoundWord.contains(word)) {
				return true;
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

	private CorefStore buildCorefs(Annotation document) {

		CorefStore store = new CorefStore();

		Map<Integer, CorefChain> coref = document.get(CorefChainAnnotation.class);

		for (Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
			CorefChain c = entry.getValue();

			// this is because it prints out a lot of self references which
			// aren't that useful
			if (c.getMentionsInTextualOrder().size() <= 1) {
				continue;
			}

			CorefMention cm = c.getRepresentativeMention();
			String clust = "";
			List<CoreLabel> tks = document.get(SentencesAnnotation.class).get(cm.sentNum - 1)
					.get(TokensAnnotation.class);
			for (int i = cm.startIndex - 1; i < cm.endIndex - 1; i++) {
				clust += tks.get(i).get(TextAnnotation.class) + " ";
			}
			clust = clust.trim();
			// System.out.println("representative mention: \"" + clust + "\" is
			// mentioned by:");

			store.addRepresentative(String.valueOf(cm.sentNum - 1), clust);

			for (CorefMention m : c.getMentionsInTextualOrder()) {
				String clust2 = "";
				tks = document.get(SentencesAnnotation.class).get(m.sentNum - 1).get(TokensAnnotation.class);
				for (int i = m.startIndex - 1; i < m.endIndex - 1; i++) {
					clust2 += tks.get(i).get(TextAnnotation.class) + " ";
				}
				clust2 = clust2.trim();
				// don't need the self mention
				if (clust.equals(clust2)) {
					continue;
				}

				store.addCoRef(String.valueOf(m.sentNum - 1), clust2, clust);

				// System.out.println("\t" + clust2);
			}
		}

		return store;
	}

	public String getPos(String word) {

		CoreLabel token = tokenMap.get(word.trim().toLowerCase());

		if (token == null) {
			return "";
		}

		return token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
	}

	private boolean isNoun(String pos) {
		return pos.equalsIgnoreCase("NN") || pos.equalsIgnoreCase("NNP") || pos.equalsIgnoreCase("NNPS")
				|| pos.equalsIgnoreCase("NNS") || pos.equalsIgnoreCase("PRP");
	}

	private boolean isVerb(String wordType) {
		return wordType.equalsIgnoreCase("VB") || wordType.equalsIgnoreCase("VBD") || wordType.equalsIgnoreCase("VBG")
				|| wordType.equalsIgnoreCase("VBN") || wordType.equalsIgnoreCase("VBP")
				|| wordType.equalsIgnoreCase("VBZ");
	}

}
