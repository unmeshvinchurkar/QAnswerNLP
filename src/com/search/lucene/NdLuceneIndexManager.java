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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

import com.search.NdIndexLockedException;
import com.search.NdIndexManager;
import com.search.NdIndexReader;
import com.search.NdIndexWriter;
import com.search.NdSearchFactory;
import com.search.NdSearchUtils;
/**
 * Implements Lucene index manager.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdLuceneIndexManager implements NdIndexManager {

	private Locale _locale = Locale.US;
	private SearcherManager _sMgr = null;
	private String _nameOrPath = null;
	private boolean _inMemory = true;
	

	public NdLuceneIndexManager(Locale locale, String nameOrPath, boolean inMemory) {

		this._nameOrPath = nameOrPath;
		this._inMemory = inMemory;

		if (locale != null) {
			this._locale = locale;
		}
	}

	public NdLuceneIndexManager(String nameOrPath, boolean inMemory) {
		this._nameOrPath = nameOrPath;
		this._inMemory = inMemory;
	}

	public boolean isInMemory() {
		return _inMemory;
	}

	public String getIndexNameOrPath() {
		return _nameOrPath;
	}

	public Directory openDirectory(String nameOrPath) throws IOException {
		return openDirectory(nameOrPath, false);
	}

	public Directory openDirectory(String nameOrPath, boolean inMemory) throws IOException {

		Directory index = null;
		
		if (inMemory) {
			index = NdLuceneInMemoryIndexCache.getIndexMemory(nameOrPath);
			
			if (index == null) {
				throw new IOException("In memory index: " + nameOrPath + " not available");
			}
		} else {
			Path path = Paths.get(nameOrPath);
			index = FSDirectory.open(path);
		}

		return index;
	}

	private void _initIndexSearcher(String nameOrPath, boolean inMemory) throws IOException {
		_sMgr = new SearcherManager(openDirectory(nameOrPath, inMemory), new SearcherFactory());
	}

	public NdLuceneIndexSearcher getIndexSearcher() throws IOException {

		if (_sMgr == null) {
			_initIndexSearcher(_nameOrPath, _inMemory);
		}

		IndexSearcher searcher = _sMgr.acquire();

		_sMgr.maybeRefresh();

		return new NdLuceneIndexSearcher(searcher, NdSearchFactory.getAnalyzer(_locale), _sMgr, _inMemory);

	}

	public void refreshIndex() {
		try {
			if (_sMgr == null) {
				_initIndexSearcher(_nameOrPath, _inMemory);
			}
			_sMgr.maybeRefresh();

		} catch (IOException e) {
		}
	}

	public NdIndexReader getIndexReader() throws IOException {

		Directory index = null;
		IndexReader reader = null;

		index = openDirectory(_nameOrPath, _inMemory);
		reader = DirectoryReader.open(index);

		return new NdLuceneIndexReader(reader, index, NdSearchFactory.getAnalyzer(_locale));
	}

	public NdIndexWriter getIndexWriter() throws IOException, NdIndexLockedException {
		Directory directory = null;
		IndexWriterConfig config = null;
		IndexWriter indexWriter = null;

		if (_inMemory || NdLuceneInMemoryIndexCache.exists(_nameOrPath)) {
			directory = NdLuceneInMemoryIndexCache.getIndexMemory(_nameOrPath);
		} else {
			
			directory = openDirectory(_nameOrPath, _inMemory);
		}
		
		Analyzer analyzer = NdSearchFactory.getAnalyzer(_locale);
		config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		try {
			indexWriter = new IndexWriter(directory, config);
		} catch (LockObtainFailedException e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
			throw new NdIndexLockedException(e);
		}
		return new NdLuceneIndexWriter(indexWriter, directory);
	}


	@Override
	public void createIndex(String nameOrPath) {

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = NdSearchFactory.getAnalyzer(_locale);

		try {

			if (!exists(nameOrPath)) {

				synchronized (this) {

					if (!exists(nameOrPath)) {

						if (_inMemory) {
							directory = new RAMDirectory();
							NdLuceneInMemoryIndexCache.addIndexMemory(nameOrPath, (RAMDirectory) directory);
						} else {
							Path path = Paths.get(nameOrPath);
							directory = FSDirectory.open(path);
						}
						IndexWriterConfig config = new IndexWriterConfig(analyzer);
						config.setCommitOnClose(true);
						config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

						indexWriter = new IndexWriter(directory, config);
						indexWriter.prepareCommit();
						indexWriter.commit();
					}
				}
			}

		} catch (IOException e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
		}
		catch (Exception e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
			throw new RuntimeException(e);
		} finally {

			try {
				if (indexWriter != null) {
					indexWriter.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				if (directory!=null && !(directory instanceof RAMDirectory)) {
					directory.close();
				}
			} catch (IOException e) {
			}

			try {
				analyzer.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void deleteIndex(String nameOrPath) {

		if (!exists(nameOrPath)) {
			return;
		}

		if (_inMemory) {
			NdLuceneInMemoryIndexCache.deleteIndexMemory(nameOrPath);

		} else {

			File indexDir = new File(nameOrPath);

			if (!indexDir.exists()) {
				return;
			}

			Directory directory = null;
			IndexWriter indexWriter = null;
			Analyzer analyzer = NdSearchFactory.getAnalyzer(_locale);

			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setCommitOnClose(true);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			Path path = Paths.get(nameOrPath);

			try {
				directory = FSDirectory.open(path);
				indexWriter = new IndexWriter(directory, config);
				indexWriter.deleteAll();
				indexWriter.prepareCommit();
				indexWriter.commit();

			} catch (IOException e) {
				System.out.println(NdSearchUtils.convertTraceToStr(e));
			} finally {
				try {
					indexWriter.close();
				} catch (IOException e) {
				}

				try {
					directory.close();
				} catch (IOException e) {
					System.out.println(NdSearchUtils.convertTraceToStr(e));
				}
			}
		}
		close();
	}

	@Override
	public void deleteIndex(String nameOrPath, NdIndexWriter indexWriter) {

		if (!exists(nameOrPath)) {
			return;
		}

		if (_inMemory) {
			NdLuceneInMemoryIndexCache.deleteIndexMemory(nameOrPath);

		} else {

			File indexDir = new File(nameOrPath);

			if (!indexDir.exists()) {
				return;
			}

			try {
				indexWriter.deleteAll();
				indexWriter.commit();

			} catch (IOException e) {
				System.out.println(NdSearchUtils.convertTraceToStr(e));
			}
		}
		close();
	}

	@Override
	public int getTotalNumOfDocs(String nameOrPath) {
		int numOfDocs = 0;
		
		System.out.println("****************** Index path: " + nameOrPath);
		System.out.println("****************** _inMemory: " + _inMemory);

		if (exists(nameOrPath)) {

			Directory directory = null;
			IndexReader reader = null;

			if (_inMemory) {
				directory = NdLuceneInMemoryIndexCache.getIndexMemory(nameOrPath);

			} else {
				File indexDir = new File(nameOrPath);

				if (!indexDir.exists()) {
					return numOfDocs;
				}

				try {
					Path path = Paths.get(nameOrPath);
					directory = FSDirectory.open(path);
				} catch (IOException e) {
					System.out.println(NdSearchUtils.convertTraceToStr(e));
					return numOfDocs;
				}
			}

			try {
				if (directory != null) {
					reader = DirectoryReader.open(directory);
					numOfDocs = reader.numDocs();
				}
			} catch (Exception e) {
				System.out.println(NdSearchUtils.convertTraceToStr(e));
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					System.out.println(NdSearchUtils.convertTraceToStr(e));
				}
				try {
					if (directory != null && !(directory instanceof RAMDirectory)) {
						directory.close();
					}
				} catch (IOException e) {
					System.out.println(NdSearchUtils.convertTraceToStr(e));
				}
			}
		}

		return numOfDocs;
	}

	@Override
	public boolean exists(String nameOrPath) {

		Directory directory = null;
		IndexReader reader = null;

		if (_inMemory) {
			if (NdLuceneInMemoryIndexCache.exists(nameOrPath)) {
				return true;
			}
			return false;
		}

		File indexDir = new File(nameOrPath);

		if (!indexDir.exists()) {
			return false;
		}

		try {
			Path path = Paths.get(nameOrPath);
			directory = FSDirectory.open(path);
			reader = DirectoryReader.open(directory);
			reader.numDocs();
		} catch (Exception e) {
			return false;
		} finally {

			try {
				reader.close();
			} catch (Exception e) {
			}
			try {
				directory.close();
			} catch (IOException e) {
			}

		}
		return true;
	}

	public void close() {
		try {
			if (_sMgr != null) {
				_sMgr.close();
			}
		} catch (IOException e) {
			System.out.println(NdSearchUtils.convertTraceToStr(e));
		} finally {
			_sMgr = null;
		}
	}

}
