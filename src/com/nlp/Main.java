package com.nlp;

import java.io.IOException;

import com.reader.FileReader;
import com.reader.TextFileReader;

public class Main {

	public static void main(String[] args) throws IOException {

		FileReader reader = new TextFileReader("C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt");

		String str = reader.getNextLines();

		ConceptExtractor extractor = new ConceptExtractor();

		while (str.length() > 0) {
			extractor.extract(str);
			str = reader.getNextLines();
		}

	}

}
