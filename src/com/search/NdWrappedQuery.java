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

/**
 * It is just a simple wrapper over native query.
 * @author UnmeshVinchurkar
 *
 */
public class NdWrappedQuery implements NdIndexQuery {

	private Object query = null;

	public NdWrappedQuery(Object q) {
		query = q;

	}

	public Object getNativeQuery() {

		return query;
	}

}
