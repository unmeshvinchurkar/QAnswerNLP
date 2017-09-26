//
// $Id$
//

// This material is the confidential, unpublished property of 
// Fair Isaac Corporation. Receipt or possession of this material does not 
// convey rights to divulge, reproduce, use, or allow others to use it 
// without the specific written authorization of Fair Isaac Corporation and 
// use must conform strictly to the license agreement.
//
// Copyright (c) 2008-2017 Fair Isaac Corporation. All Rights Reserved.
//
package com.search.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.search.NdIndexQuery;
import com.search.NdIndexReader;
import com.search.NdSearchConstants;
import com.search.NdSearchUtils;
/**
 * Implements lucene index reader.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneIndexReader implements NdIndexReader {

	private Directory _directory = null;
	private IndexReader _indexReader = null;
	private IndexSearcher _indexSearcher = null;
	private Analyzer _analyzer = null;
	private StandardQueryParser _queryParser = null;

	public NdLuceneIndexReader(IndexReader indexReader, Directory directory, Analyzer analyzer) {
		this._analyzer = analyzer;

		if (this._analyzer == null) {
			analyzer = new StandardAnalyzer();
		}

		this._indexReader = indexReader;
		this._indexSearcher = new IndexSearcher(indexReader);
		this._queryParser = new StandardQueryParser(analyzer);
		this._directory = directory;

		_queryParser.setDefaultOperator(Operator.OR);
		_queryParser.setAllowLeadingWildcard(true);
	}

	public Map fetchDocument(String fieldName, String value) throws IOException {

		Map json = null;
		Query query = null;

		query = new TermQuery(new Term(fieldName.trim().toLowerCase(), value.trim()));

		TopDocs topdocs = _indexSearcher.search(query, 1);

		ScoreDoc docs[] = topdocs.scoreDocs;

		if (docs != null && docs.length > 0) {

			json = new HashMap();

			Document doc = _indexSearcher.doc(docs[0].doc);
			List fields = doc.getFields();
			for (int i = 0; i < fields.size(); i++) {
				IndexableField field = (IndexableField) fields.get(i);

				json.put(field.name(), field.stringValue());

			}
		}

		return json;

	}

	@Override
	public List runQuery(NdIndexQuery iquery) throws IOException {

		List resultDocs = new ArrayList();

		Query query = (Query) iquery.getNativeQuery();
		TopDocs hits = _indexSearcher.search(query, 20000);
		resultDocs = _prepareResult(hits);

		return resultDocs;
	}

	@Override
	public List runQueryString(String queryString) throws IOException {

		Query query = null;
		List resultDocs = new ArrayList();

		try {

			queryString = queryString.trim();

			if ((queryString.length() < 6 || !queryString.contains(" ")) && !queryString.endsWith("*")) {
				if (!queryString.endsWith(")") && !queryString.endsWith("~")) {
					// && !queryString.endsWith("\"")
					queryString = queryString + "*";
				}
			}

			try {
				MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
						new String[] { NdSearchConstants.DISPLAY_NAME, NdSearchConstants.CONTENT, NdSearchConstants.PATH },
						_analyzer);
				query = queryParser.parse(queryString.toLowerCase());
			} catch (Exception e) {
				System.out.println(NdSearchUtils.convertTraceToStr(e));
			}

			if (query == null) {
				query = this._queryParser.parse(queryString.toLowerCase(), NdSearchConstants.CONTENT);
			}

		} catch (Exception e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
			return new ArrayList();
		}
		
		
		System.out.println(query.toString());		
		
		TopDocs hits = _indexSearcher.search(query, 200);
		resultDocs = _prepareResult(hits);

		return resultDocs;
	}

	public List _prepareResult(TopDocs hits) throws IOException {

		List resultDocs = new ArrayList();

		if (hits != null && hits.scoreDocs != null) {

			for (ScoreDoc sd : hits.scoreDocs) {
				Document doc = _indexSearcher.doc(sd.doc);
				Map docMap = new HashMap();

				List fields = doc.getFields();

				docMap.put("score", String.valueOf(sd.score));
				docMap.put("id", sd.doc);

				for (int i = 0; i < fields.size(); i++) {
					IndexableField field = (IndexableField) fields.get(i);
					docMap.put(field.name(), field.stringValue());
				}
				resultDocs.add(docMap);
			}
		}
		return resultDocs;
	}

	@Override
	public void close() {
		try {

			_indexReader.close();
		} catch (IOException e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
		}

		try {
			if (!(_directory instanceof RAMDirectory)) {
				_directory.close();
			}
		} catch (IOException e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
		}

	}

}
