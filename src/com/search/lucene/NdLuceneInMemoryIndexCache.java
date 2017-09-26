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

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.store.RAMDirectory;

/**
 * This is a cache to cache lucene RAMDirectory objects.
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneInMemoryIndexCache {

	private static Map _indexCache = new HashMap();

	public static void addIndexMemory(String indexName, RAMDirectory indexMemory) {
		_indexCache.put(indexName, indexMemory);
	}

	public static RAMDirectory getIndexMemory(String indexName) {
		return (RAMDirectory) _indexCache.get(indexName);
	}

	public static void deleteIndexMemory(String indexName) {
		if (exists(indexName)) {
			RAMDirectory dir = (RAMDirectory) _indexCache.remove(indexName);
		}
	}

	public static boolean exists(String indexName) {
		return _indexCache.get(indexName) != null;
	}

}
