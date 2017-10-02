package com.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.NodePattern;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ConceptExtractor2 {

	public static void main(String[] args) throws IOException {

		PrintWriter out;
		String rules;

		rules = "data/regexner1.txt";

		out = new PrintWriter(System.out);

		Env env = TokenSequencePattern.getNewEnv();
		env.setDefaultStringMatchFlags(NodePattern.CASE_INSENSITIVE);

		CoreMapExpressionExtractor<MatchedExpression> extractor = CoreMapExpressionExtractor
				.createExtractorFromFiles(env, rules);

		Properties props = new Properties();
		props.put("annotators",
				"tokenize,ssplit,pos,lemma,ner, tokensregexdemo,   parse, dcoref,  natlog, mention, coref, openie, dcoref");
		props.setProperty("customAnnotatorClass.tokensregexdemo", "edu.stanford.nlp.pipeline.TokensRegexAnnotator");
		props.setProperty("tokensregexdemo.rules", rules);
		props.put("openie.resolve_coref", "true");
		props.put("openie.ignoreaffinity", "false");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		File f = new File("C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt");

		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

		String line = null;

		StringBuffer sb = new StringBuffer();
		int i = 0;
		while ((line = bf.readLine()) != null) {

			sb.append(" " + line);
			i++;

			if (i > 50) {
				i = 0;

				Annotation annotation = new Annotation(sb.toString() + ".");
				sb.setLength(0);

				//
				// Annotation annotation = new Annotation(
				// IOUtils.slurpFileNoExceptions("C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt"));

				try {
					pipeline.annotate(annotation);
				} catch (Exception e1) {
				}

				// out.println("The top level annotation");
				// out.println(annotation.toShorterString());
				List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

				for (CoreMap sentence : sentences) {

					// List<MatchedExpression> exps =
					// extractor.extractExpressions(sentence);

					Map<String, CoreLabel> tokenMap = new HashMap<>();

					StringBuffer sTxt = new StringBuffer(200);

					for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
						// Print out words, lemma, ne, and normalized ne

						String word = token.get(CoreAnnotations.TextAnnotation.class);
						String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
						String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
						String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
						String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
						// !ne.equals("MISC") &&
						// if (!ne.equals("O") && !ne.equals("NUMBER") &&
						// !ne.equals("ORGANIZATION")) {
						tokenMap.put(word.trim().toLowerCase(), token);
						// }

						sTxt.append(word);
						sTxt.append(" ");

					}

					if (true) {

						Collection<RelationTriple> triples = sentence
								.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
						// Print the triples

						System.out.println(sTxt.toString());

						if (triples != null)
							for (RelationTriple triple : triples) {

								try {

									String sub = parseSubject(triple.subjectGloss(), tokenMap);
									String verb = parseVerb(triple.relationGloss(), triple.relationLemmaGloss(),
											tokenMap);
									String object = parseObj(triple.objectGloss(), tokenMap);

									// (sub.trim().equals("") ||
									// sub.trim().equals("you")) &&

									if (true || !verb.trim().equals("") && !object.trim().equals("")) {

										// System.out.println(sTxt.toString());
										if (isValid(sub, verb, object)) {
											System.out.println(sub + " __________  " + verb + " __________ " + object);
										}

									}

									// if (!sub.trim().equals("") &&
									// !verb.trim().equals("") &&
									// !object.trim().equals("")
									// && !object.trim().equals(sub.trim())) {
									// System.out.println(sub + " __________ " +
									// verb + " __________ " + object);
									//
									// }

									// if (sub != null && verb != null && object
									// != null && !sub.trim().equals("")
									// && !verb.trim().equals("") &&
									// !object.trim().equals("") &&
									// !sub.trim().equals(object.trim())) {
									// System.out.println(sub + " __________ " +
									// verb + " __________ " + object);
									//
									// }

									// System.out.println("///////////////////////////////////////");

								} catch (Exception e) {
								}
							}
						else {

							System.out.println("____________NO TRIPLET______________________");

						}

						System.out.println("_________________________________________________________-----------");

					}
				}
			}
		}

		out.flush();
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

	public static String parseSubject(String sub, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();
		sub = removeStopWords(sub);

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {

				String ner = getNER(tokens[i].toLowerCase(), "", tokenMap);
				String pos = getPOS(tokens[i].toLowerCase(), "", tokenMap);

				if (isPermitter(ner) && isNoun(pos)) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
	}

	public static String parseObj(String sub, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();

		sub = sub.replaceAll("(^|\\b)(in|you|to)(\\b|$)", "");

		sub = removeStopWords(sub);
		int ii = 0;

		String tokens[] = sub.split(" ");

		try {
			if (tokens != null) {
				for (int i = 0; i < tokens.length; i++) {
					String ner = getNER(tokens[i].toLowerCase(), "", tokenMap);
					String pos = getPOS(tokens[i].toLowerCase(), "", tokenMap);

					if (isPermitter(ner) && (isNoun(pos) || isVerb(pos))) {
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

		return sb.toString();
	}

	public static String parseVerb(String verbStr, String lemma, Map<String, CoreLabel> tokenMap) throws Exception {

		StringBuffer sb = new StringBuffer();

		String tokens[] = verbStr.split(" ");
		int count = 0;

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				String ner = getNER(tokens[i].toLowerCase(), lemma, tokenMap);
				String pos = getPOS(tokens[i], lemma, tokenMap);

				if (isPermitter(ner)) {
					if (isVerb(pos) || isIN(pos)) {
						sb.append(tokens[i].trim() + " ");
						count++;
					}
				}
			}
		}

		verbStr = sb.toString();

		String tokenss[] = verbStr.split(" ");

		if ((tokenss == null || tokenss.length == 0) && tokens.length >= 1) {
			return tokenss[0];
		} else if ((tokenss == null || tokens.length == 0)) {
			return "";
		} else if (tokenss.length == 1) {
			return tokenss[0];
		} else {
			String str = removeStopWords(verbStr);
			// return tokenss[0] ;

			if (str.trim().equals("")) {

				str = str;
			}

			return str;
		}
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

	private static boolean isPermitter(String ner) {
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
			if (tokenMap.get(lemma) == null) {
				return "";
			} else {
				token = tokenMap.get(lemma);
			}
		}

		String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

		// System.out.println(word + " : "+ner);
		return ner;
	}

	private static boolean isVerb(String wordType) {
		return wordType.equalsIgnoreCase("VB") || wordType.equalsIgnoreCase("VBD") || wordType.equalsIgnoreCase("VBG")
				|| wordType.equalsIgnoreCase("VBN") || wordType.equalsIgnoreCase("VBP")
				|| wordType.equalsIgnoreCase("VBZ") ;
	}
	
	private static boolean isIN(String wordType) {
		return  wordType.equalsIgnoreCase("IN");
	}

	private static boolean isAdjective(String wordType) {
		return wordType.equalsIgnoreCase("JJ") || wordType.equalsIgnoreCase("JJR") || wordType.equalsIgnoreCase("JJS");
	}

	private static boolean isNoun(String pos) {
		return pos.equalsIgnoreCase("NN") || pos.equalsIgnoreCase("NNP") || pos.equalsIgnoreCase("NNPS")
				|| pos.equalsIgnoreCase("NNS") || pos.equalsIgnoreCase("PRP");
	}

}
