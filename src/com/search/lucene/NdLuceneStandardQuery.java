//
//$Id$
//

//This material is the confidential, unpublished property of 
//Fair Isaac Corporation. Receipt or possession of this material does not 
//convey rights to divulge, reproduce, use, or allow others to use it 
//without the specific written authorization of Fair Isaac Corporation and 
//use must conform strictly to the license agreement.
//
//Copyright (c) 2008-2017 Fair Isaac Corporation. All Rights Reserved.
//


package com.search.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.Operator;
import org.apache.lucene.search.Query;

import com.search.NdIndexQuery;
import com.search.NdSearchConstants;

/**
 * This is a standard query implementation.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneStandardQuery implements NdIndexQuery {

	private static Analyzer _analyzer = new StandardAnalyzer();
	private static StandardQueryParser _queryParser = null;
	private Query _query = null;

	static {
		_queryParser = new StandardQueryParser(_analyzer);
		_queryParser.setDefaultOperator(Operator.OR);
		_queryParser.setAllowLeadingWildcard(true);
	}

	public NdLuceneStandardQuery(String queryString) {

		try {
			_query = _queryParser.parse(queryString.toLowerCase(), NdSearchConstants.CONTENT);
		} catch (QueryNodeException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getNativeQuery() {
		return _query;
	}

}
