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
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.search.NdIndexQuery;
import com.search.NdIndexSearcher;
import com.search.NdSearchConstants;
import com.search.NdSearchFactory;
import com.search.NdSearchUtils;
import com.search.NdWrappedQuery;

/**
 * Implements Lucene Index Searcher.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneIndexSearcher implements NdIndexSearcher {

	private SearcherManager _sMgr = null;
	private IndexSearcher _indexSearcher = null;
	private Analyzer _analyzer = null;
	private StandardQueryParser _queryParser = null;
	private boolean _inMemory = false;
	private String _indexType = null;
	
	public NdLuceneIndexSearcher(IndexSearcher searcher, Analyzer analyzer, SearcherManager sMgr, boolean inMemory) {
		this._sMgr = sMgr;
		this._indexSearcher = searcher;
		this._analyzer = analyzer;
		this._inMemory = inMemory;

		if (!this._inMemory) {
			_indexType = NdSearchConstants.LUCENE_DISK_INDEX;
		} else {
			_indexType = NdSearchConstants.LUCENE_IN_MEMORY_INDEX;
		}

		if (this._analyzer == null) {
			analyzer = new StandardAnalyzer();
		}

		this._queryParser = new StandardQueryParser(analyzer);
		_queryParser.setDefaultOperator(Operator.OR);
		_queryParser.setAllowLeadingWildcard(true);
	}

	public Map fetchDocument(String fieldName, String value) throws IOException {

		Map json = null;
		TermQuery query = new TermQuery(new Term(fieldName.toLowerCase(), value));
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

	private String _processQuery(String queryString) {

		StringBuffer sb = new StringBuffer(200);
		String or = /* NOI18N */"or";
		String and = /* NOI18N */"and";

		if (queryString != null) {

			sb.append(queryString.toLowerCase());

			int index = sb.indexOf(or, 0);

			while (index > 0) {

				int preWordIndex = index - 1;
				int postWordIndex = index + or.length();

				if (preWordIndex > 0 && postWordIndex < sb.length()) {

					if (!Character.isLetter(sb.charAt(preWordIndex)) && !Character.isLetter(sb.charAt(postWordIndex))) {
						sb.replace(index, index + or.length(), or.toUpperCase());
					}
				}

				index = sb.indexOf(or, index + 1);
			}

			index = sb.indexOf(and, 0);

			while (index > 0) {

				int preWordIndex = index - 1;
				int postWordIndex = index + and.length();

				if (preWordIndex > 0 && postWordIndex < sb.length()) {

					if (!Character.isLetter(sb.charAt(preWordIndex)) && !Character.isLetter(sb.charAt(postWordIndex))) {
						sb.replace(index, index + and.length(), and.toUpperCase());
					}
				}
				index = sb.indexOf(and, index + 1);
			}
		}
		return sb.toString();
	}

	public List runQueryString(String queryString) throws IOException {
		return runQueryString(queryString, null);
	}

	public List runQueryString(String queryString, NdIndexQuery filterQuery) throws IOException {

		Query query = null;
		List resultDocs = new ArrayList();

		try {
			queryString = queryString.trim();

			if ((queryString.length() < 11 || !queryString.contains(/* NOI18N */" ")) && !queryString.endsWith(/* NOI18N */"*")) {
				if (!queryString.endsWith(/* NOI18N */")") && !queryString.endsWith(/* NOI18N */"~")) {
					// && !queryString.endsWith("\"")
					queryString = queryString + /* NOI18N */"*";
				}
			}

			if (!queryString.contains(/* NOI18N */":") && !queryString.equals("*")) {

				try {
					BooleanQuery.setMaxClauseCount(100000);
					MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
							new String[] { NdSearchConstants.DISPLAY_NAME, NdSearchConstants.CONTENT }, _analyzer);
					queryParser.setAllowLeadingWildcard(true);

					// using scoring for wildcards
					queryParser.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
					query = queryParser.parse(_processQuery(queryString));
				} catch (Exception e) {					
					System.out.println(NdSearchUtils.convertTraceToStr(e));
				}
			}

			if (query == null) {
				query = this._queryParser.parse(_processQuery(queryString), NdSearchConstants.CONTENT);
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}

		if (filterQuery != null) {
			NdIndexQuery iQuery = NdSearchFactory.createBooleanQuery(_indexType, filterQuery, new NdWrappedQuery(query),
					NdIndexQuery.AND);

			query = (Query) iQuery.getNativeQuery();
		}
		
		System.out.println(query.toString());		

		TopDocs hits = _indexSearcher.search(query, 50);

		ScoreDoc docs[] = hits.scoreDocs;

		if (docs != null && docs.length > 0) {
			for (ScoreDoc sd : docs) {
				Document doc = _indexSearcher.doc(sd.doc);
				Map docMap = new HashMap();

				List fields = doc.getFields();

				docMap.put(/* NOI18N */"score", String.valueOf(sd.score));

				for (int i = 0; i < fields.size(); i++) {
					IndexableField field = (IndexableField) fields.get(i);
					docMap.put(field.name(), field.stringValue());
				}
				resultDocs.add(docMap);
			}
		}	

		return resultDocs;
	}

	public void close() {
		try {
			_sMgr.release(_indexSearcher);
		} catch (Exception e) {
			
		}
	}

}
