
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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

import com.search.NdIndexQuery;

public class NdLucenePrefixQuery implements NdIndexQuery {

	private Query _query = null;

	public NdLucenePrefixQuery(String property, String preFix) {
		Term term = new Term(property, preFix);
		_query = new PrefixQuery(term);
	}

	public Object getNativeQuery() {
		return _query;
	}
}
