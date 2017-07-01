package com.useless;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ComplexExtractor1 {

	public static void main(String[] args) throws IOException {
		PrintWriter out;
		String rules;

		rules = "com/data/regexner1.txt";

		out = new PrintWriter(System.out);
		CoreMapExpressionExtractor<MatchedExpression> extractor = CoreMapExpressionExtractor
				.createExtractorFromFiles(TokenSequencePattern.getNewEnv(), rules);

		//Properties props = new Properties();
		//props.put("annotators", "tokenize,ssplit,pos,lemma,ner");
	  //   props.setProperty("customAnnotatorClass.tokensregexdemo", "edu.stanford.nlp.pipeline.TokensRegexAnnotator");
		//props.setProperty("tokensregexdemo.rules", rules);

		StanfordCoreNLP pipeline = new StanfordCoreNLP();
		Annotation annotation = new Annotation(
				IOUtils.slurpFileNoExceptions("C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt"));

		pipeline.annotate(annotation);
		
		List<CoreLabel> tokens= annotation.get(CoreAnnotations.TokensAnnotation.class);

		  

		// out.println("The top level annotation");
		// out.println(annotation.toShorterString());
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		
		/*
		for (CoreMap sentence : sentences) {
			      // NOTE: Depending on what tokensregex rules are specified, there are other annotations
			      //       that are of interest other than just the tokens and what we print out here
			      for (CoreLabel token:sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			        // Print out words, lemma, ne, and normalized ne
			        String word = token.get(CoreAnnotations.TextAnnotation.class);
			        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
			        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			        String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
			        String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
			        out.println("token: " + "word="+word + ",  ne=" + ne);
			      }
		   }
		
		
		*/
		
		   
		   for (CoreMap sentence : sentences) {
			List<MatchedExpression> matchedExpressions = extractor.extractExpressions(sentence);
			for (MatchedExpression matched : matchedExpressions) {
				// Print out matched text and value
			//	 out.println("matched: " + matched.getText() + " with value "
			//	 + matched.getValue());
				// Print out token information
				CoreMap cm = matched.getAnnotation();
				for (CoreLabel token : cm.get(CoreAnnotations.TokensAnnotation.class)) {
					String word = token.get(CoreAnnotations.TextAnnotation.class);
					// String lemma =
					// token.get(CoreAnnotations.LemmaAnnotation.class);
					// String pos =
					// token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
					String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

					out.println("matched token: " + "word=" + word + " , ne=" + ne);
				}
			}
		}
		out.flush();
	}

}
