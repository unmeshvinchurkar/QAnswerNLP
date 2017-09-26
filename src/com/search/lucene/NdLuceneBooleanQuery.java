package com.search.lucene;

import java.util.List;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.search.NdIndexQuery;

/**
 * This class creates a boolean query by combining multiple queries.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneBooleanQuery implements NdIndexQuery {

	private Query query = null;

	public NdLuceneBooleanQuery(NdIndexQuery query1, NdIndexQuery query2, String op) {

		Occur ocr = Occur.MUST;
		if (op.equalsIgnoreCase(NdIndexQuery.OR)) {
			ocr = Occur.SHOULD;
		}

		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add((Query) query1.getNativeQuery(), ocr);
		builder.add((Query) query2.getNativeQuery(), ocr);

		query = builder.build();

	}

	public NdLuceneBooleanQuery(List queryList, String op) {

		Occur ocr = Occur.MUST;
		if (op.equalsIgnoreCase(NdIndexQuery.OR)) {
			ocr = Occur.SHOULD;
		}

		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for (int i = 0; i < queryList.size(); i++) {

			NdIndexQuery q = (NdIndexQuery) queryList.get(i);
			builder.add((Query) q.getNativeQuery(), ocr);
		}

		query = builder.build();

	}

	public Object getNativeQuery() {
		return query;
	}

}
