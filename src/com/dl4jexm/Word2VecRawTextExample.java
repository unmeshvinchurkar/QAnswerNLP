package com.dl4jexm;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by agibsonccc on 10/9/14.
 *
 * Neural net that processes text into wordvectors. See below url for an
 * in-depth explanation. https://deeplearning4j.org/word2vec.html
 */
public class Word2VecRawTextExample {

	// private static Logger log =
	// LoggerFactory.getLogger(Word2VecRawTextExample.class);

	public static void main(String[] args) throws Exception {

		// Gets Path to Text file
		String filePath = "C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt";

		// new ClassPathResource("./sample.txt").getFile().getAbsolutePath();

		// log.info("Load & Vectorize Sentences....");
		// Strip white space before and after for each line
		SentenceIterator iter = new BasicLineIterator(filePath);
		// Split on white spaces in the line to get words
		TokenizerFactory t = new DefaultTokenizerFactory();

		/*
		 * CommonPreprocessor will apply the following regex to each token:
		 * [\d\.:,"'\(\)\[\]|/?!;]+ So, effectively all numbers, punctuation
		 * symbols and some special symbols are stripped off. Additionally it
		 * forces lower case for all tokens.
		 */
		t.setTokenPreProcessor(new CommonPreprocessor());

		// log.info("Building model....");
		Word2Vec vec = new Word2Vec.Builder().minWordFrequency(2).iterations(2).layerSize(10).seed(42).windowSize(2)
				.iterate(iter).tokenizerFactory(t).build();

		// log.info("Fitting Word2Vec model....");
		vec.fit();

		// log.info("Writing word vectors to text file....");

		// Write word vectors to file
		WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");

		// Prints out the closest 10 words to "day". An example on what to do
		// with these Word Vectors.
		// log.info("Closest Words:");

		Collection<String> lst = vec.wordsNearest("branch", 3);
		System.out.println("5 Words closest: " + lst);

		
	}
}