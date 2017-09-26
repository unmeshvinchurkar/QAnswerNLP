package com.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class Main2 {

	public static void main1(String[] args) throws NdIndexLockedException {

		String indexDir = "C:\\Users\\unmeshvinchurkar\\Desktop\\Index3";
		String queryStr = "National";

		NdIndexManager indexmanager = NdSearchFactory.getIndexManager(NdSearchConstants.LUCENE_DISK_INDEX, indexDir);

		String value = "/projToPubish/Business Object Models/Java/Imported Java BOMs/Claims Processing/PPOPercentRefund_DT2";
		// value = value.replace("/", "\\/");

		try {
			NdIndexSearcher searcher = indexmanager.getIndexSearcher();
			// searcher.fetchDocument("id", value);
			indexmanager.getIndexWriter();

			searcher = indexmanager.getIndexSearcher();
			searcher.fetchDocument("id", value);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		NdIndexReader reader = null;
		List results = new ArrayList();

		try {
			reader = indexmanager.getIndexReader();
			results = reader.runQueryString(queryStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}

	}

	private static String quote(String str) {
		String result = null;
		for (String s : str.split("\\||!")) {
			s = Pattern.quote(s);
			if (result == null) {
				result = s;
			} else {
				result += "|";
				result += s;
			}
		}
		if (result == null)
			return "";
		return result;
	}

	public static void main4(String[] args) {

		Pattern jar = Pattern.compile(
				quote("xerces2.jar!hk2-locator-2.4.0-b31.jar!jersey-container-servlet-2.22.1.jar"),
				Pattern.CASE_INSENSITIVE);

		jar.pattern();
	}

	public static void main(String[] args) throws IOException, NdIndexLockedException {

		System.out.print(Character.isLetter('+'));
	}

	public static void main5(String[] args) throws IOException, NdIndexLockedException {

		// String indexDir = NdSearchConstants.INDEX_DIR;

		String indexDir = "C:\\Users\\unmeshvinchurkar\\Desktop\\Index4";

		// String BLAZE_PROJ_PATH =
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\DMP_DATA\\workspace";

		// String BLAZE_PROJ_PATH =
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\LucenePerf\\bigRepo\\HSourceWS";

		// String BLAZE_PROJ_PATH =
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\LucenePerf\\rgaWS73";

		String BLAZE_PROJ_PATH = "C:\\Users\\unmeshvinchurkar\\Desktop\\Bonnie\\workspace";

		// String BLAZE_PROJ_PATH =\
		// "C:\\Users\\unmeshvinchurkar\\advisor\\examples\\examples\\repositories\\ExamplesRepository\\Metaphors
		// and Templates\\Providers\\Basic Providers";

		// String BLAZE_PROJ_PATH =
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\pvtws1";

		// String BLAZE_PROJ_PATH =
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\pvtws1\\projToPubish";

		// String BLAZE_PROJ_PATH =
		// "C:\\Users\\unmeshvinchurkar\\advisor\\builder\\..\\examples\\examples\\repositories\\ExamplesRepository";

		// NdIndexQuery q1 =
		// NdSearchFactory.createPrefixQuery(NdSearchFactory.LUCENE_SEARCH,
		// "path",
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\LucenePerf\\rgaCM73\\BL\\Rx");
		// NdIndexQuery q2 =
		// NdSearchFactory.createPrefixQuery(NdSearchFactory.LUCENE_SEARCH,
		// "path",
		// "C:\\Users\\unmeshvinchurkar\\Desktop\\LucenePerf\\rgaCM73\\TST\\RxTst");
		// NdIndexQuery q3
		// =NdSearchFactory.createBooleanQuery(NdSearchFactory.LUCENE_SEARCH,
		// q1, q2, NdIndexQuery.OR);

		NdIndexManager indexmanager = NdSearchFactory.getIndexManager(NdSearchConstants.LUCENE_DISK_INDEX, indexDir);
		try {
			indexmanager.createIndex(indexDir);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		NdIndexWriter w = indexmanager.getIndexWriter();

		indexmanager.deleteIndex(indexDir, w);

		try {
			w.close();
		} catch (Exception e3) {
			e3.printStackTrace();
		}

		try {
			int numOfDocs = indexmanager.getTotalNumOfDocs(indexDir);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		long startTime = System.currentTimeMillis();

		try {
			indexmanager.createIndex(indexDir);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// NdIFileLoader loader =
		// NdInstanceFileLoaderFactory.getFileLoader(NdSearchConstants.REPOSITORY_TYPE_FILE,
		// BLAZE_PROJ_PATH);
		//
		// NdInstanceFileParser r = new NdInstanceFileParser(loader,
		// BLAZE_PROJ_PATH);

		// InstanceFileLoader loader = new InstanceFileLoader(BLAZE_PROJ_PATH);
		// InstanceFileContentParser r = new InstanceFileContentParser(loader,
		// BLAZE_PROJ_PATH);
		// r.parseInstanceFiles();

		List<Map> jsonDocs = null;// r.getJsonDocs();

		try {

			NdIndexWriter writer = null;
			try {
				writer = indexmanager.getIndexWriter();
			} catch (NdIndexLockedException e1) {
				e1.printStackTrace();
			}
			int count = 0;

			for (Map obj : jsonDocs) {
				writer.addDocument(obj);
				count++;

			}

			try {
				writer.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				writer.close();
			}

			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// // e.printStackTrace();
			// }

			// writer = indexmanager.getIndexWriter();
			// writer = indexmanager.getIndexWriter();
			// writer = indexmanager.getIndexWriter(indexDir);
			// writer = indexmanager.getIndexWriter(indexDir);

			long endTime = System.currentTimeMillis();

			System.out.println("Total documents: " + count);
			System.out.println("Total Time Taken: " + (endTime - startTime) * 1.0 / (1000.0));

			startTime = System.currentTimeMillis();

			Query query = null;
			// value = value.replace("/", "\\/");
			String value = "/projToPubish/Business Object Models/Java/Imported Java BOMs/Claims Processing/PPOPercentRefund_DT2";
			// value = value.replace("/", "\\/");

			// query = new TermQuery(new Term(fieldName, "\""+value+"\""));

			query = new TermQuery(new Term("path", value));

			NdIndexSearcher searcher = indexmanager.getIndexSearcher();
			searcher.fetchDocument("id", value);

			List results = searcher.runQueryString("a*", null);

			// IIndexReader reader = indexmanager.getIndexReader(indexDir);
			// List results = reader.runQueryString("a*");
			// System.out.println(re);

			endTime = System.currentTimeMillis();

			System.out.println("Query Time milis: " + (endTime - startTime));
			System.out.println("Query Time: " + (endTime - startTime) * 1.0 / (1000.0));

			System.out.println("Results length: " + (results == null ? 0 : results.size()));

			searcher.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(0);

	}

}
