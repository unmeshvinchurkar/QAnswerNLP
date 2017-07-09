package com.experiment;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneQuestionClassifier {

	private Directory _directory = null;

	public LuceneQuestionClassifier() {

		// setup Lucene to use an in-memory index
		_directory = new RAMDirectory();
		Analyzer analyzer = new WhitespaceAnalyzer();
		try {
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(_directory, config);

			writer.addDocument(_createDocument("EXPLAINATION",
					"where who procedure explain process describe can create update delete do how to"));
			writer.addDocument(
					_createDocument("YES-NO", "any possible to equals equal greater smaller lesser better than "));
			writer.addDocument(_createDocument("NUMERIC", "are how many number total sum calculate no. min max minimum maximum average"));
			writer.addDocument(_createDocument("LIST", "which get list name names all type types of kind kinds from"));
			writer.close();

		} catch (IOException e) {
			new RuntimeException("couldn't create lucene index");
		}

	}

	public String classify(String sentence) {

		Document doc = null;
		try {
			IndexReader reader = DirectoryReader.open(_directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new WhitespaceAnalyzer();

			StandardQueryParser queryParser = new StandardQueryParser(analyzer);
			Query query = null;

			query = queryParser.parse(sentence, "contents");

			TopDocs hits = searcher.search(query, 1);
			ScoreDoc scoreDoc = hits.scoreDocs[0];
			doc = searcher.doc(scoreDoc.doc);
		} catch (IOException | QueryNodeException e) {
			new RuntimeException("couldn't search lucene index");
		}

		return doc.get("id");
	}

	private Document _createDocument(String id, String content) {
		Document doc = new Document();
		new TextField("contents", content, Store.YES);
		doc.add(new TextField("id", id, Store.YES));
		doc.add(new TextField("contents", content, Store.YES));
		return doc;
	}

	public static void main(String[] args) throws Exception {
		LuceneQuestionClassifier cl = new LuceneQuestionClassifier();
		System.out.println(cl.classify("how to do it "));
	}

}
