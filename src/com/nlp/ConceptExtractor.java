package com.nlp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.AttributeFactory;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ConceptExtractor {

	private StanfordCoreNLP pipeline = null;

	public ConceptExtractor() {

		PrintWriter out;
		String rules;

		rules = "data/regexner1.txt";

		out = new PrintWriter(System.out);

		Properties props = new Properties();
		props.put("annotators",
				"tokenize,ssplit,pos,lemma,ner, tokensregexdemo,   parse, dcoref,  natlog, mention, coref, openie, dcoref");
		props.setProperty("customAnnotatorClass.tokensregexdemo", "edu.stanford.nlp.pipeline.TokensRegexAnnotator");
		props.setProperty("tokensregexdemo.rules", rules);
		props.put("openie.resolve_coref", "true");
		props.put("openie.ignoreaffinity", "false");

		pipeline = new StanfordCoreNLP(props);

	}

	public void extract(String lines) {

		Annotation annotation = new Annotation(lines + ".");

		try {
			pipeline.annotate(annotation);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {

			Map<String, CoreLabel> tokenMap = new HashMap<>();

			StringBuffer sTxt = new StringBuffer(200);

			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				// Print out words, lemma, ne, and normalized ne

				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
				String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);

				tokenMap.put(word.trim().toLowerCase(), token);

				sTxt.append(word);
				sTxt.append(" ");

			}

			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples

			System.out.println(sTxt.toString());

			if (triples != null)
				for (RelationTriple triple : triples) {

					try {

						String sub = parseSubject(triple.subjectGloss(), tokenMap).getWordString();
						String verb = parseVerb(triple.relationGloss(), triple.relationLemmaGloss(), tokenMap).getWordString();
						String object = parseObj(triple.objectGloss(), tokenMap).getWordString();

						// System.out.println(sTxt.toString());
						if (isValid(sub, verb, object)) {
							System.out.println(sub + " __________  " + verb + " __________ " + object);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			System.out.println("_________________________________________________________-----------");

		}

	}

	private static boolean isValid(String sub, String verb, String obj) {

		sub = sub.trim().toLowerCase();
		verb = verb.trim().toLowerCase();
		obj = obj.trim().toLowerCase();

		if (sub.equals("") && obj.equals("")) {
			return false;
		} else if (verb.equals("")) {
			return false;
		} else if (obj.equals(sub)) {
			return false;
		} else if (verb.equals(obj)) {
			return false;
		} else if (sub.equals(obj)) {
			return false;
		}
		return true;
	}

	public static SubjectWrapper parseSubject(String sub, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();
		sub = removeStopWords(sub);

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {

				String ner = getNER(tokens[i].toLowerCase(), "", tokenMap);
				String pos = getPOS(tokens[i].toLowerCase(), "", tokenMap);

				if (isPermitted(ner) && isNoun(pos)) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return new SubjectWrapper(tokenMap, sb.toString());
	}

	public static ObjectWrapper parseObj(String sub, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();

		sub = sub.replaceAll("(^|\\b)(in|you|to)(\\b|$)", "");

		sub = removeStopWords(sub);
		int ii = 0;

		String tokens[] = sub.split(" ");
		
		//ObjectWrapper
		boolean isNoun = true;

		try {
			if (tokens != null) {
				for (int i = 0; i < tokens.length; i++) {
					String ner = getNER(tokens[i].toLowerCase(), "", tokenMap);
					String pos = getPOS(tokens[i].toLowerCase(), "", tokenMap);

					if (isPermitted(ner) && (isNoun(pos) || isVerb(pos))) {
						
						if(isVerb(pos)){
							isNoun = false;
						}
						
						sb.append(tokens[i].trim() + " ");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (sb.toString().trim().length() == 0) {
			int z = 0;
		}

		return new ObjectWrapper(tokenMap, isNoun, sb.toString());
	}

	public static VerbWrapper parseVerb(String verbStr, String lemma, Map<String, CoreLabel> tokenMap) throws Exception {

		StringBuffer sb = new StringBuffer();

		String tokens[] = verbStr.split(" ");
		int count = 0;
		boolean isVerb = true;
		
		//VerbWrapper

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				String ner = getNER(tokens[i].toLowerCase(), lemma, tokenMap);
				String pos = getPOS(tokens[i], lemma, tokenMap);

				if (isPermitted(ner)) {
					if (isVerb(pos) || isIN(pos)) {
						
						if(isIN(pos)){
							isVerb =  false;
						}
						
						sb.append(tokens[i].trim() + " ");
						count++;
					}
				}
			}
		}

		verbStr = sb.toString();

		String tokenss[] = verbStr.split(" ");
		String returnStr = "";

		if ((tokenss == null || tokenss.length == 0) && tokens.length >= 1) {
			returnStr =  tokenss[0];
		} else if ((tokenss == null || tokens.length == 0)) {
			returnStr =  "";
		} else if (tokenss.length == 1) {
			returnStr = tokenss[0];
		} else {
			String str = removeStopWords(verbStr);
			// return tokenss[0] ;

			if (str.trim().equals("")) {

				str = str;
			}

			returnStr = str;
		}
		
		return new VerbWrapper(tokenMap, isVerb, returnStr);
		
	}

	public static String removeStopWords(String textFile) {

		if (textFile == null)
			return "";

		List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "but", "if", "or", "such", "the", "their",
				"then", "there", "they", "was", "will", "through");

		CharArraySet stopSet = new CharArraySet(stopWords, true);

		stopSet.add("is");
		stopSet.add("was");
		stopSet.add("were");
		stopSet.add("its");
		stopSet.add("your");
		stopSet.add("therefore");
		stopSet.add("none");
		stopSet.add("only");
		stopSet.add("may");
		stopSet.add("more");
		stopSet.add("1");
		stopSet.add("2");
		stopSet.add("3");
		stopSet.add("4");
		stopSet.add("5");
		stopSet.add("6");
		stopSet.add("7");
		stopSet.add("8");
		stopSet.add("9");
		stopSet.add("0");
		stopSet.add("so");
		stopSet.add("also");
		stopSet.add("how");
		stopSet.add("least");
		stopSet.add("example");
		stopSet.add("astrix");
		stopSet.add("still");
		stopSet.add("always");
		stopSet.add("user");
		stopSet.add("it");
		stopSet.add("notes");
		stopSet.add("should");
		stopSet.add("when");
		stopSet.add("about");
		stopSet.add("opinion");
		stopSet.add("also");
		stopSet.add("following");
		stopSet.add("however");
		stopSet.add("just");
		stopSet.add("sure");
		stopSet.add("already");
		stopSet.remove("it");
		stopSet.remove("its");
		stopSet.remove("this");
		stopSet.remove("that");

		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;

		// Set stopWords = EnglishAnalyzer.getDefaultStopSet();
		TokenStream tokenStream = new ClassicTokenizer(factory);
		((ClassicTokenizer) tokenStream).setReader(new StringReader(textFile.trim()));

		tokenStream = new StopFilter(tokenStream, stopSet);
		StringBuilder sb = new StringBuilder();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();

			while (tokenStream.incrementToken()) {
				String term = charTermAttribute.toString();
				sb.append(term + " ");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().trim();
	}

	private static boolean isPermitted(String ner) {
		// !ne.equals("MISC") &&
		// !ner.equals("O") &&
		// && !ner.equals("ORGANIZATION")
		if (!ner.equals("NUMBER")) {
			return true;
		}
		return false;
	}

	private static String getPOS(String word, String lemma, Map<String, CoreLabel> tokenMap) {
		if (word == null) {
			return "";
		}
		CoreLabel token = tokenMap.get(word.trim().toLowerCase());

		if (token == null) {
			return "";
		}

		String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		return pos;
	}

	private static String getNER(String word, String lemma, Map<String, CoreLabel> tokenMap) {
		if (word == null) {
			return "";
		}
		CoreLabel token = tokenMap.get(word.trim().toLowerCase());

		if (token == null) {
			return "";
		}

		String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
		return ner;
	}

	private static boolean isVerb(String wordType) {
		return wordType.equalsIgnoreCase("VB") || wordType.equalsIgnoreCase("VBD") || wordType.equalsIgnoreCase("VBG")
				|| wordType.equalsIgnoreCase("VBN") || wordType.equalsIgnoreCase("VBP")
				|| wordType.equalsIgnoreCase("VBZ");
	}

	private static boolean isIN(String wordType) {
		return wordType.equalsIgnoreCase("IN");
	}

	private static boolean isAdjective(String wordType) {
		return wordType.equalsIgnoreCase("JJ") || wordType.equalsIgnoreCase("JJR") || wordType.equalsIgnoreCase("JJS");
	}

	private static boolean isNoun(String pos) {
		return pos.equalsIgnoreCase("NN") || pos.equalsIgnoreCase("NNP") || pos.equalsIgnoreCase("NNPS")
				|| pos.equalsIgnoreCase("NNS") || pos.equalsIgnoreCase("PRP");
	}

}
