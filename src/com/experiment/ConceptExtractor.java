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

public class ConceptExtractor {

	public static void main(String[] args) throws IOException {
		
		
		PrintWriter out;
		String rules;

		rules = "data/regexner1.txt";

		out = new PrintWriter(System.out);

		Env env =	TokenSequencePattern.getNewEnv();
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

					for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
						// Print out words, lemma, ne, and normalized ne

						String word = token.get(CoreAnnotations.TextAnnotation.class);
						String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
						String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
						String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
						String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
						// !ne.equals("MISC") &&
					//	if (!ne.equals("O") && !ne.equals("NUMBER") && !ne.equals("ORGANIZATION")) {
							tokenMap.put(word.trim().toLowerCase(), token);
					//	}

					}

					if (true) {

						Collection<RelationTriple> triples = sentence
								.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
						// Print the triples

						if (triples != null)
							for (RelationTriple triple : triples) {

								try {

									String sub = parseSubject(triple.subjectLemmaGloss(), tokenMap);
									String verb = parseVerb(triple.relationLemmaGloss(), tokenMap);
									String object = parseObj(triple.objectLemmaGloss(), tokenMap);
									
									// (sub.trim().equals("") || sub.trim().equals("you")) &&

									if ( !verb.trim().equals("")
											&& !object.trim().equals("")) {
									 System.out.println(sub + " __________  " + verb + " __________ " + object);

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

					}
				}
			}
		}

		out.flush();
	}

	public static String parseSubject(String sub, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();

		try {
			sub = removeStopWords(sub);
		} catch (Exception e) {
		}

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {

				String ner = getNER(tokens[i].toLowerCase(), tokenMap);

				if (isPermitter(ner)) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
	}

	public static String parseObj(String sub, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();

		sub = sub.replaceAll("(^|\\b)(in|you|to)(\\b|$)", "");

		try {
			sub = removeStopWords(sub);
		} catch (Exception e) {
		}

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				String ner = getNER(tokens[i].toLowerCase(), tokenMap);
				if (isPermitter(ner)) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
	}

	public static String parseVerb(String verbStr, Map<String, CoreLabel> tokenMap) {

		StringBuffer sb = new StringBuffer();

		try {
			verbStr = removeStopWords(verbStr);
		} catch (Exception e) {
		}

		String tokens[] = verbStr.split(" ");
		int count = 0;

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				String ner = getNER(tokens[i].toLowerCase(), tokenMap);

				if (!isPermitter(ner)) {
					if (!isNoun(getPOS(tokens[i], tokenMap))) {
						sb.append(tokens[i].trim() + " ");
						count++;
					}
				}
			}
		}

		verbStr = sb.toString();

		if (count == 1) {
			verbStr = verbStr.replaceAll("(^|\\b)(of|to)($|\\b)", "");
		} else if (count > 1) {
			verbStr = verbStr.replaceAll("(^|\\b)(have|in|on|to|of|for)(\\b|$)", "");
		}

		tokens = verbStr.split(" ");

		if (tokens == null || tokens.length == 0) {
			return "";
		} else if (tokens.length == 1) {
			return tokens[0];
		} else if (tokens.length == 2) {
			return tokens[0] + " " + tokens[1];
		}
		return tokens[1] + " " + tokens[2];
	}

	public static String removeStopWords(String textFile) throws Exception {

		if (textFile == null)
			return "";

		List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "but", "if", "into", "no", "or", "such",
				"the", "their", "then", "there", "they", "was", "will", "with");

		CharArraySet stopSet = new CharArraySet(stopWords, true);

		// stopSet.add("you");
		stopSet.add("over");
		stopSet.add("your");
		stopSet.add("see");
		stopSet.add("check");
		stopSet.add("therefore");
		stopSet.add("space");
		stopSet.add("none");
		stopSet.add("only");
		stopSet.add("create");
		stopSet.add("may");
		stopSet.add("more");
		stopSet.add("plus");
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
		stopSet.add("false");
		stopSet.add("attempt");
		stopSet.add("first");
		stopSet.add("add");
		stopSet.add("none");
		stopSet.add("also");
		stopSet.add("proior");
		stopSet.add("how");
		stopSet.add("least");
		stopSet.add("from");
		stopSet.add("continue");
		stopSet.add("example");
		stopSet.add("append");
		stopSet.add("copy");
		stopSet.add("way");
		stopSet.add("astrix");
		stopSet.add("contact");
		stopSet.add("expect");
		stopSet.add("still");
		stopSet.add("relate");
		stopSet.add("always");
		stopSet.add("user");
		stopSet.add("can");
		// stopSet.add("it");
		stopSet.add("need");
		stopSet.add("b");
		stopSet.add("be");
		stopSet.add("select");
		stopSet.add("could");
		stopSet.add("notes");
		stopSet.add("should");
		stopSet.add("when");
		stopSet.add("tip");
		stopSet.add("know");
		stopSet.remove("note");
		stopSet.add("customer");
		stopSet.add("about");
		stopSet.add("assess");
		stopSet.add("opinion");
		stopSet.add("must");
		stopSet.add("behave");
		stopSet.add("also");
		stopSet.add("want");
		stopSet.add("one");
		stopSet.add("following");
		stopSet.add("however");
		stopSet.add("just");
		stopSet.add("tutorial");
		stopSet.add("sure");
		stopSet.add("select");
		stopSet.add("upon");
		stopSet.add("already");
		stopSet.add("some");
		stopSet.add("card");
		stopSet.add("credit");
		stopSet.add("tutorial");
		stopSet.add("tutorials");
		stopSet.add("confidential");
		stopSet.add("simple");
		stopSet.add("erase");
		stopSet.add("must");

		stopSet.remove("by");
		stopSet.remove("in");
		stopSet.remove("with");
		stopSet.remove("not");
		stopSet.remove("to");
		stopSet.remove("with");
		stopSet.remove("it");
		stopSet.remove("its");
		stopSet.remove("this");
		stopSet.remove("that");
		stopSet.remove("is");
		stopSet.remove("at");
		stopSet.remove("on");
		stopSet.remove("try");
		stopSet.remove("be");
		stopSet.remove("of");

		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;

		// Set stopWords = EnglishAnalyzer.getDefaultStopSet();
		TokenStream tokenStream = new ClassicTokenizer(factory);
		((ClassicTokenizer) tokenStream).setReader(new StringReader(textFile.trim()));

		tokenStream = new StopFilter(tokenStream, stopSet);
		StringBuilder sb = new StringBuilder();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			String term = charTermAttribute.toString();
			sb.append(term + " ");
		}
		return sb.toString().trim();
	}

	private static boolean isPermitter(String ner) {
		// !ne.equals("MISC") &&
		if (!ner.equals("O") && !ner.equals("NUMBER") && !ner.equals("ORGANIZATION")) {
			return true;
		}
		return false;
	}

	private static String getPOS(String word, Map<String, CoreLabel> tokenMap) {
		if (word == null) {
			return "";
		}
		CoreLabel token = tokenMap.get(word.trim());
		String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		return pos;
	}

	private static String getNER(String word, Map<String, CoreLabel> tokenMap) {
		if (word == null) {
			return "";
		}
		CoreLabel token = tokenMap.get(word.trim());
		String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
		
		//System.out.println(word + " : "+ner);
		return ner;
	}

	private static boolean isNoun(String pos) {
		return pos.equalsIgnoreCase("NN") || pos.equalsIgnoreCase("NNP") || pos.equalsIgnoreCase("NNPS")
				|| pos.equalsIgnoreCase("NNS");
	}

}
