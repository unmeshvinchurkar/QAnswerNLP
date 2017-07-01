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
import org.apache.lucene.wordnet.SynonymMap;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ComplexExtractor {

	public static void main(String[] args) throws IOException {
		PrintWriter out;
		String rules;

		rules = "data/regexner1.txt";

		out = new PrintWriter(System.out);

		CoreMapExpressionExtractor<MatchedExpression> extractor = CoreMapExpressionExtractor
				.createExtractorFromFiles(TokenSequencePattern.getNewEnv(), rules);

		Properties props = new Properties();
		props.put("annotators",
				"tokenize,ssplit,pos,lemma,ner, tokensregexdemo,   parse, dcoref,  natlog, mention, coref, openie, dcoref");
		props.setProperty("customAnnotatorClass.tokensregexdemo", "edu.stanford.nlp.pipeline.TokensRegexAnnotator");
		props.setProperty("tokensregexdemo.rules", rules);
		// props.setProperty("tokenize.whitespace", "true");
		// props.setProperty("ssplit.eolonly", "true");
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
					// NOTE: Depending on what tokensregex rules are specified,
					// there
					// are other annotations
					// that are of interest other than just the tokens and what
					// we print
					// out here

					List<MatchedExpression> exps = extractor.extractExpressions(sentence);

					boolean flag = false;

					if (exps != null && exps.size() > 0) {
						flag = true;
					}

					Map<String, String> wordNerMap = new HashMap<>();
					Map<String, CoreLabel> tokenMap = new HashMap<>();

					for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
						// Print out words, lemma, ne, and normalized ne

						String word = token.get(CoreAnnotations.TextAnnotation.class);
						String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
						String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
						String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
						String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
						// !ne.equals("MISC") &&
						if (!ne.equals("O") && !ne.equals("NUMBER") && !ne.equals("ORGANIZATION")) {
							wordNerMap.put(word.trim(), ne);
							tokenMap.put(word.trim(), token);
						}

						// out.println("token: " + "word=" + word + ", ne=" +
						// ne);

					}
					// out.println("//////////////////////////////////////////////////////////");

					// out.flush();
					// Tree tree =
					// sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
					// tree.pennPrint(out);

					// out.println(tree.toString());

					// out.println("The first sentence basic dependencies
					// are:");
					// out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
					// out.println("//////////////////////////////////////////////////////////");
					//

					// out.println("The first sentence collapsed, CC‐processed
					// dependencies are:");
					// SemanticGraph graph =
					// sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
					// out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

					// SemanticGraphCoreAnnotations.

					if (true) {

						Collection<RelationTriple> triples = sentence
								.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
						// Print the triples

						if (triples != null)
							for (RelationTriple triple : triples) {
								// System.out.println(triple.confidence + "\t" +
								// triple.subjectLemmaGloss() + "\t" +
								// triple.relationLemmaGloss() + "\t" +
								// triple.objectLemmaGloss());

								// out.println("//////////////////////////////////////////////////////////");
								// out.flush();

								// System.out.println();

								try {

									// boolean sUse =
									// canUseSubjectObj(triple.subjectLemmaGloss(),
									// wordNerMap) ;
									// boolean oUse =
									// canUseSubjectObj(triple.objectLemmaGloss(),
									// wordNerMap) ;
									// boolean vUse =
									// canUseVerb(triple.objectLemmaGloss()) ;

									String sub = parseSubjectObj(triple.subjectLemmaGloss(), wordNerMap);
									String verb = parseVerb(triple.relationLemmaGloss());
									String object = parseObj(triple.objectLemmaGloss(), wordNerMap);

									if (!sub.trim().equals("") && !verb.trim().equals("") && !object.trim().equals("")
											&& !object.trim().equals(sub.trim())) {
										System.out.println(sub + " __________  " + verb + " __________ " + object);

									}

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

					// Alternately, to only run e.g., the clause splitter:
					// List<SentenceFragment> clauses = new
					// OpenIE(props).clausesInSentence(sentence);
					// for (SentenceFragment clause : clauses) {
					// System.out.println(clause.parseTree.toString(SemanticGraph.OutputFormat.LIST));
					// }
					//

					/*
					 * Triplet result; ExtractionService extractorS = new
					 * ExtractionService(); result =
					 * extractorS.extractTriplet(tree); if(null != result) {
					 * out.println( result.toString()); }
					 * 
					 */

				}
			}
		}

		/*
		 * 
		 * for (CoreMap sentence : sentences) { List<MatchedExpression>
		 * matchedExpressions = extractor.extractExpressions(sentence); for
		 * (MatchedExpression matched : matchedExpressions) { // Print out
		 * matched text and value
		 * 
		 * out.println("______________________________________________________")
		 * ; out.println("matched: " + matched.getText() + " with value " +
		 * matched.getValue());
		 * 
		 * out.println("______________________________________________________")
		 * ; // Print out token information CoreMap cm =
		 * matched.getAnnotation(); for (CoreLabel token :
		 * cm.get(CoreAnnotations.TokensAnnotation.class)) { String word =
		 * token.get(CoreAnnotations.TextAnnotation.class); // String lemma = //
		 * token.get(CoreAnnotations.LemmaAnnotation.class); // String pos = //
		 * token.get(CoreAnnotations.PartOfSpeechAnnotation.class); String ne =
		 * token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
		 * 
		 * out.println("matched token: " + "word=" + word + " , ne=" + ne); } }
		 * out.println(
		 * "//////////////////////////////////////////////////////////");
		 * 
		 * Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		 * 
		 * tree.pennPrint(out);
		 * 
		 * out.println(
		 * "//////////////////////////////////////////////////////////");
		 * 
		 * }
		 */

		out.flush();
	}

	public static boolean canUseSubjectObj(String sub, Map<String, String> wordNerMap) {

		StringBuffer sb = new StringBuffer();
		try {
			sub = removeStopWords(sub);
		} catch (Exception e) {
		}

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				if (wordNerMap.containsKey(tokens[i].trim())) {
					sb.append(tokens[i] + " ");
				}
			}
		}

		if (sb.toString().trim().equals("")) {

			return false;
		}

		return true;
	}

	public static String parseSubjectObj(String sub, Map<String, String> wordNerMap) {

		StringBuffer sb = new StringBuffer();

		try {
			sub = removeStopWords(sub);
		} catch (Exception e) {
		}

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				if (wordNerMap.containsKey(tokens[i])) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
	}

	public static String parseObj(String sub, Map<String, String> wordNerMap) {

		StringBuffer sb = new StringBuffer();

		try {
			sub = removeStopWords(sub);
		} catch (Exception e) {
		}

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				if (wordNerMap.containsKey(tokens[i])) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
	}

	public static boolean canUseVerb(String verbStr) {

		try {
			verbStr = removeStopWords(verbStr);
		} catch (Exception e) {
		}

		String tokens[] = verbStr.split(" ");

		if (tokens == null || tokens.length > 3 || tokens.length == 0) {
			return false;
		}

		return true;
	}

	public static String parseVerb(String verbStr) {

		try {
			verbStr = removeStopWords(verbStr);
		} catch (Exception e) {
		}

		String tokens[] = verbStr.split(" ");

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

		// CharArraySet stopSet = (CharArraySet)
		// EnglishAnalyzer.getDefaultStopSet();

		// CharArraySet stopSet = CharArraySet.copy(Version.LUCENE_34,
		// StandardAnalyzer.STOP_WORDS_SET);

		List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "but", "if", "into", "no", "or", "such",
				"the", "their", "then", "there", "they", "was", "will", "with");

		CharArraySet stopSet = new CharArraySet(stopWords, true);

		stopSet.add("you");
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
		stopSet.add("it");
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

	public static void main1(String args[]) {
		String[] words = new String[] { "get", "use", "represent", "call", "be" };
		SynonymMap map = null;
		try {
			map = new SynonymMap(new FileInputStream(
					"C:\\Users\\unmeshvinchurkar\\Desktop\\watson\\New folder\\standfordProj\\src\\com\\data\\wn_s.pl"));

			// new File("com/data/wn_s.pl").exists()
		} catch (Exception e) {

		}
		for (int i = 0; i < words.length; i++) {
			String[] synonyms = map.getSynonyms(words[i]);
			System.out.println(words[i] + ":" + java.util.Arrays.asList(synonyms).toString());
		}

	}
}
