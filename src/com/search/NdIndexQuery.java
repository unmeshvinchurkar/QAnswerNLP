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
 * This is a Query interface.
 * 
 * @author UnmeshVinchurkar
 *
 */
public interface NdIndexQuery {
	
	public static final String OR ="OR";
	public static final String AND ="AND";
	
	/**
	 * Returns native query object(as per lib used)
	 * @return
	 */
	public Object getNativeQuery() ;

}
