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

package com.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Implementation of this interface run query on search index.
 * 
 * @author UnmeshVinchurkar
 *
 */
public interface NdIndexSearcher {

	/**
	 * Runs the given query strings.
	 * 
	 * @param queryString
	 * @return Returns a list of json docs
	 * @throws IOException
	 */
	public List runQueryString(String queryString) throws IOException;
	
	/**
	 * Fetches a document based on field and value.
	 * @param fieldName
	 * @param value
	 * @return JSONObject
	 * @throws IOException
	 */
	public Map fetchDocument(String fieldName, String value)  throws IOException;

	public List runQueryString(String queryString, NdIndexQuery filterQuery) throws IOException;

	/**
	 * Closes index reader.
	 */
	public void close();

}
