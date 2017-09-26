
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

/**
 * Implementation of this interface provide methods to manage search index.
 * 
 * @author UnmeshVinchurkar
 *
 */
public interface NdIndexManager {

	/**
	 * Creates index
	 * 
	 * @param name
	 *            - string
	 */
	public void createIndex(String nameOrPath) throws IOException;
	
	/**
	 * Refreshes Lucene Index
	 */
	public void refreshIndex();
	
	/**
	 * Deletes index
	 * 
	 * @param nameOrPath
	 */
	public void deleteIndex(String nameOrPath) ;

	public void deleteIndex(String nameOrPath, NdIndexWriter indexWriter) throws IOException;

	/**
	 * Checks if an index exitst
	 * 
	 * @param nameOrPath
	 * @return boolean
	 */
	public boolean exists(String nameOrPath);

	/**
	 * Return Index writer which is used to add documents to index
	 * 
	 * @return
	 * @throws IOException
	 */
	public NdIndexWriter getIndexWriter() throws IOException, NdIndexLockedException;

	/**
	 * Returns object of index searcher, which is used to run queries on the
	 * index
	 * 
	 * @return
	 * @throws IOException
	 */
	public NdIndexSearcher getIndexSearcher() throws IOException;

	/**
	 * Returns object of index reader, which is used to run queries on the index
	 * 
	 * @return
	 * @throws IOException
	 */
	public NdIndexReader getIndexReader() throws IOException;
	
	/**
	 * Returns total number of docs stored in an index
	 * @param nameOrPath
	 * @return
	 */
	public int getTotalNumOfDocs(String nameOrPath) ;

	/**
	 * Returns index name
	 * 
	 * @return string
	 */
	public String getIndexNameOrPath();

	/**
	 * If index is in-memory index then returns true.
	 * 
	 * @return
	 */
	public boolean isInMemory();

}
