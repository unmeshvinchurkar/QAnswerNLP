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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.search.NdIndexWriter;
import com.search.NdSearchConstants;

/**
 * Implements Lucene index writer.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneIndexWriter implements NdIndexWriter {

	private IndexWriter _indexWriter = null;
	private Directory _directory = null;

	public NdLuceneIndexWriter(IndexWriter indexWriter, Directory directory) {

		this._indexWriter = indexWriter;
		this._directory = directory;
	}

	public void deleteDocument(String field, String value) throws IOException {
		_indexWriter.deleteDocuments(new Term(field, value));
	}

	public void updateDocument(String field, String value, Map json) throws IOException {
		Document doc = _convertMapToDocument(json);
		_indexWriter.updateDocument(new Term(field, value), doc);
	}

	@Override
	public void addDocument(Map json) throws IOException {

		Document doc = _convertMapToDocument(json);
		_indexWriter.addDocument(doc);
	}

	private Document _convertMapToDocument(Map json) {

		Document doc = new Document();
		Iterator iter = json.keySet().iterator();

		while (iter.hasNext()) {
			String key = ((String) iter.next()).toLowerCase();
			String value = String.valueOf(json.get(key));
			Field field = null;

			if (key.equals(NdSearchConstants.ID)) {
				field = new StringField(key.toLowerCase(), value, Store.YES);
			} else {
				if (NdSearchConstants.STORED_FIELDS.contains(key)) {
					field = new TextField(key.toLowerCase(), value, Store.YES);
				} else {
					field = new TextField(key.toLowerCase(), value, Store.NO);
				}
			}

			if (key.equals(NdSearchConstants.DISPLAY_NAME.toLowerCase()) || key.equals(NdSearchConstants.NAME.toLowerCase())) {
				field.setBoost(20.0f);
			}

			doc.add(field);
		}

		return doc;
	}

	public void deleteAll() throws IOException {

		_indexWriter.deleteAll();

	}

	@Override
	public void close() {
		try {

			_indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (!(_directory instanceof RAMDirectory)) {
				_directory.close();
			}
		} catch (IOException e) {
			
		}

	}

	public void flush() {
		try {
			_indexWriter.flush();
		} catch (IOException e) {
			
		}
	}

	@Override
	public void commit() {
		try {
			_indexWriter.prepareCommit();
			_indexWriter.commit();
		} catch (IOException e) {			
		}

	}
}
