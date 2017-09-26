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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.search.lucene.NdLuceneBooleanQuery;
import com.search.lucene.NdLuceneIndexManager;
import com.search.lucene.NdLucenePrefixQuery;
import com.search.lucene.NdLuceneStandardQuery;

/**
 * This factory class is used to create object of NdIndexmanger according to
 * type of search library.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdSearchFactory {

	private static Map indexManagerCache = new HashMap();

	/**
	 * Returns object of index manager
	 * 
	 * @param searchProvider
	 * @param nameOrPath
	 * @return
	 */
	public static NdIndexManager getIndexManager(String searchProvider, String nameOrPath) {
		return getIndexManager(searchProvider, Locale.US, nameOrPath);
	}

	public static NdIndexManager getIndexManager(String searchProvider, Locale locale, String nameOrPath) {

		if (indexManagerCache.get(nameOrPath) == null) {

			synchronized (NdSearchFactory.class) {
				if (indexManagerCache.get(nameOrPath) == null) {

					if (searchProvider.equalsIgnoreCase(NdSearchConstants.LUCENE_DISK_INDEX)) {
						indexManagerCache.put(nameOrPath, new NdLuceneIndexManager(locale, nameOrPath, false));
					} else if (searchProvider.equalsIgnoreCase(NdSearchConstants.LUCENE_IN_MEMORY_INDEX)) {
						indexManagerCache.put(nameOrPath, new NdLuceneIndexManager(locale, nameOrPath, true));
					}
				}
			}
		}

		return (NdIndexManager) indexManagerCache.get(nameOrPath);
	}
	

	public static NdIndexQuery createPrefixQuery(String searchProvider, String field, String prefix) {		
		return new NdLuceneStandardQuery(field+":\"" + prefix+"\"");
		//return new NdLucenePrefixQuery(field, prefix);
	}

	public static NdIndexQuery createBooleanQuery(String searchProvider, NdIndexQuery q1, NdIndexQuery q2,
			String operator) {
		return new NdLuceneBooleanQuery(q1, q2, operator);
	}
	
	public static NdIndexQuery createBooleanQuery(String searchProvider, List queryList,
			String operator) {
		return new NdLuceneBooleanQuery(queryList, operator);
	}

	public static NdIndexQuery getVersioningRepoFilter(String searchProvider, String owner) {
		return createStandardQuery(searchProvider, getDocFilterQueryStr(searchProvider, owner));
	}

	public static NdIndexQuery createStandardQuery(String searchProvider, String q) {
		return new NdLuceneStandardQuery(q);
	}

	public static String getDocFilterQueryStr(String searchProvider, String owner) {

		return "( owner: " + owner + "  ) OR (- owner:" + owner + "  AND isNew:false ) ".toLowerCase();

	}

	/**
	 * Returns lucene analyzer according to locale.
	 * 
	 * @param locale
	 * @return
	 */
	public static Analyzer getAnalyzer(Locale locale) {

		Analyzer analyzer = null;

//		  if (locale.equals(Locale.FRANCE) || locale.equals(Locale.FRENCH)) {
//			return new FrenchAnalyzer();
//		} else if (locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY)) {
//			return new GermanAnalyzer();
//		} else if (locale.equals(Locale.ITALIAN) || locale.equals(Locale.ITALY)) {
//			return new SpanishAnalyzer();
//		} else if (locale.getCountry().equalsIgnoreCase("Russia") || locale.getLanguage().equalsIgnoreCase("Russian")) {
//			return new RussianAnalyzer();
//		} else if (locale.getLanguage().equalsIgnoreCase("Danish")) {
//			return new DanishAnalyzer();
//		}

		return new StandardAnalyzer();

	}

}
