package com.experiment;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.CoreMap;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * @author unmesh vinchurkar
 */
public class TextSummarizer {

	private static final StanfordCoreNLP pipeline;

	static {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
	}

	private final Counter<String> dfCounter;
	private final int numDocuments;

	public TextSummarizer() {
		Counter<String> dfCounter = new ClassicCounter<>();
		dfCounter.setDefaultReturnValue(1);

		this.dfCounter = dfCounter;
		this.numDocuments = 1;
	}

	private static Counter<String> getTermFrequencies(List<CoreMap> sentences) {
		Counter<String> ret = new ClassicCounter<String>();

		for (CoreMap sentence : sentences)
			for (CoreLabel cl : sentence.get(CoreAnnotations.TokensAnnotation.class))
				ret.incrementCount(cl.get(CoreAnnotations.TextAnnotation.class));

		return ret;
	}

	private class SentenceComparator implements Comparator<CoreMap> {
		private final Counter<String> termFrequencies;

		public SentenceComparator(Counter<String> termFrequencies) {
			this.termFrequencies = termFrequencies;
		}

		@Override
		public int compare(CoreMap o1, CoreMap o2) {
			return (int) Math.round(score(o2) - score(o1));
		}

		/**
		 * Compute sentence score (higher is better).
		 */
		private double score(CoreMap sentence) {
			double tfidf = tfIDFWeights(sentence);

			// Weight by position of sentence in document
			int index = sentence.get(CoreAnnotations.SentenceIndexAnnotation.class);
			double indexWeight = 5.0 - (index * 1.0) / 30.0; // 5.0 / index;

			return indexWeight * tfidf * 100.0;
		}

		private double tfIDFWeights(CoreMap sentence) {
			double total = 0;
			for (CoreLabel cl : sentence.get(CoreAnnotations.TokensAnnotation.class))
				if (cl.get(CoreAnnotations.PartOfSpeechAnnotation.class).toLowerCase().startsWith("n"))
					total += tfIDFWeight(cl.get(CoreAnnotations.TextAnnotation.class));

			return total;
		}

		private double tfIDFWeight(String word) {
			if (dfCounter.getCount(word) == 0)
				return 0;

			double tf = 1 + Math.log(termFrequencies.getCount(word));
			double idf = Math.log(numDocuments / (1 + dfCounter.getCount(word)));
			// return tf * idf;

			return tf;
		}
	}

	private List<CoreMap> rankSentences(List<CoreMap> sentences, Counter<String> tfs) {
		Collections.sort(sentences, new SentenceComparator(tfs));
		return sentences;
	}

	public String summarize(String document, int numSentences) {
		Annotation annotation = pipeline.process(document);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		Counter<String> tfs = getTermFrequencies(sentences);
		sentences = rankSentences(sentences, tfs);

		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < numSentences; i++) {
			ret.append(sentences.get(i));
			ret.append("    ");
		}

		return ret.toString();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String filename = "C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt";
		String content = IOUtils.slurpFile(filename);

		TextSummarizer summarizer = new TextSummarizer();
		String result = summarizer.summarize(content, 6);
		System.out.println(result);
	}

}