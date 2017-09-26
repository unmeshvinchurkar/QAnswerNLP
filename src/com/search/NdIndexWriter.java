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
import java.util.Map;

/**
 * This interface is used to add documents to a search index.
 * 
 * @author UnmeshVinchurkar
 *
 */
public interface NdIndexWriter {
	/**
	 * Add document to an index.
	 * 
	 * @param json
	 * @throws IOException
	 */
	public void addDocument(Map json) throws IOException;
	
	public void updateDocument(String field, String value, Map json) throws IOException;
	
	public void deleteDocument(String field, String value) throws IOException;

	/**
	 * Closes the index writer.
	 */
	public void close();

	/**
	 * Commit, so that changes are visible to index searcher and index readers.
	 */
	public void commit();
	
	public void deleteAll() throws IOException;

	/**
	 * Flush the changes to index directory.
	 */
	public void flush();

}
