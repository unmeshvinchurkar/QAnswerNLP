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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StemmerUtil;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.wordnet.SynonymMap;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class QTargetExtractor {

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
		props.put("openie.resolve_coref", "true");
		props.put("openie.ignoreaffinity", "false");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		

		String line = null;

		Annotation annotation = new Annotation("how it can create rules maintenance application ");

		try {
			pipeline.annotate(annotation);
		} catch (Exception e1) {
		}

		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			
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
				
				
				
				
				System.out.println("1. "+ Morphology.stemStatic(word,pos ));
				System.out.println("2. "+Morphology.stemStatic(word,ne ));
				System.out.println("3. "+token.get(CoreAnnotations.EntityRuleAnnotation.class));
				System.out.println("4. "+token.get(CoreAnnotations.AnswerObjectAnnotation.class));
				System.out.println("5. "+token.get(CoreAnnotations.BagOfWordsAnnotation.class));
				System.out.println("6. "+token.get(CoreAnnotations.CategoryAnnotation.class));
				System.out.println("7. "+token.get(CoreAnnotations.CategoryAnnotation.class));
				System.out.println("8 "+token.get(CoreAnnotations.DoAnnotation.class));
				System.out.println("9 "+token.get(CoreAnnotations.StemAnnotation.class));
				System.out.println("10 "+token.get(CoreAnnotations.WordSenseAnnotation.class));
				System.out.println("11 "+token.get(CoreAnnotations.VerbSenseAnnotation.class));
				System.out.println("12 "+token.get(CoreAnnotations.TopicAnnotation.class));
				
				
				
				
				System.out.println("lemma: "+lemma + "  word="+word + ",  ne=" + ne+ ",  pos=" + pos+ ",  norm=" + normalized);
				// !ne.equals("MISC") &&
			//	if (!ne.equals("O") && !ne.equals("NUMBER") && !ne.equals("ORGANIZATION")) {
					wordNerMap.put(word.trim().toLowerCase(), ne);
					tokenMap.put(word.trim().toLowerCase(), token);
			//	}

			}

			SemanticGraph graph = sentence
					.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
						
			
			System.out.println("DEPENDENCIES: "+graph.toList());
			//System.out.println("DEPENDENCIES SIZE: "+graph.size());
			List<SemanticGraphEdge> edge_set1 = graph.edgeListSorted();
			int j=0;

			for(SemanticGraphEdge edge : edge_set1){
			    j++;
			  //  System.out.println("------EDGE DEPENDENCY: "+j);
			    Iterator<SemanticGraphEdge> it = edge_set1.iterator();
			    IndexedWord dep = edge.getDependent();
			    String dependent = dep.word();
			    int dependent_index = dep.index();
			    IndexedWord gov = edge.getGovernor();
			    String governor = gov.word();
			    int governor_index = gov.index();
			    GrammaticalRelation relation = edge.getRelation();
			  //  System.out.println("No:"+j+" Relation: "+relation.toString()+" Dependent: "+dependent.toString()+" Governor: "+governor.toString());
			    System.out.println(" Relation: "+relation.toString()+" Governor: "+governor.toString()+" Dependent: "+dependent.toString());
				
			
			
			}
			
			
		
		SemanticGraph dependencies2 = sentence.get(BasicDependenciesAnnotation.class);
		
	//	dependencies2.prettyPrint();
		
		
		Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		
	//	tree.pennPrint();
			

			if (true) {

				Collection<RelationTriple> triples = sentence
						.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
				// Print the triples

				if (triples != null)
					for (RelationTriple triple : triples) {
						 System.out.println(triple.confidence + "\t" +
						 triple.subjectLemmaGloss() + "\t" +
						 triple.relationLemmaGloss() + "\t" +
						 triple.objectLemmaGloss());

						

						 System.out.println();

						try {

							String sub = parseSubjectObj(triple.subjectLemmaGloss(), wordNerMap);
							String verb = parseVerb(triple.relationLemmaGloss());
							String object = parseObj(triple.objectLemmaGloss(), wordNerMap);

//							if (!sub.trim().equals("") && !verb.trim().equals("") && !object.trim().equals("")
//									&& !object.trim().equals(sub.trim())) {
								System.out.println(sub + " __________  " + verb + " __________ " + object);

						//	}

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

		out.flush();
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
				if (wordNerMap.containsKey(tokens[i].toLowerCase())) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
	}

	public static String parseObj(String sub, Map<String, String> wordNerMap) {

		StringBuffer sb = new StringBuffer();
		int ii=0;

		try {
			sub = removeStopWords(sub);
		} catch (Exception e) {
		}

		String tokens[] = sub.split(" ");

		if (tokens != null) {
			for (int i = 0; i < tokens.length; i++) {
				if (wordNerMap.containsKey(tokens[i].toLowerCase())) {
					sb.append(tokens[i].trim() + " ");
				}
			}
		}

		return sb.toString();
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

		List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "but", "if", "into", "no", "or", "such",
				"the", "their", "then", "there", "they", "was", "will", "with");

		CharArraySet stopSet = new CharArraySet(stopWords, true);

		stopSet.add("he");
		stopSet.add("she");
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

}
