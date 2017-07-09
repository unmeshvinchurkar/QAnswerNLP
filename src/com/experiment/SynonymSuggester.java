package com.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.dictionary.Dictionary;

public class SynonymSuggester {

	// Related words for a given sense (do synonyms by default)
	// Probably should implement all PointerTypes
	public static ArrayList<Synset> getRelated(Synset sense, PointerType type)
			throws JWNLException, NullPointerException {
		PointerTargetNodeList relatedList;
		// Call a different function based on what type of relationship you are
		// looking for
		if (type == PointerType.HYPERNYM) {
			relatedList = PointerUtils.getInstance().getDirectHypernyms(sense);
		} else if (type == PointerType.HYPONYM) {
			relatedList = PointerUtils.getInstance().getDirectHyponyms(sense);
		} else if (type == PointerType.DERIVED) {
			relatedList = PointerUtils.getInstance().getDerived(sense);
		} else {
			relatedList = PointerUtils.getInstance().getSynonyms(sense);
		}
		// Iterate through the related list and make an ArrayList of Synsets to
		// send back
		Iterator i = relatedList.iterator();
		ArrayList a = new ArrayList();
		while (i.hasNext()) {
			PointerTargetNode related = (PointerTargetNode) i.next();
			Synset s = related.getSynset();
			a.add(s);
		}
		return a;
	}

	public static void main(String args[]) throws JWNLException, FileNotFoundException {

		File flag = new File(
				"C:\\Users\\unmeshvinchurkar\\Desktop\\watson\\New folder\\standfordProj\\data\\properties.xml");

		// flag.exists()
		JWNL.initialize(new FileInputStream(
				"C:\\Users\\unmeshvinchurkar\\Desktop\\watson\\New folder\\standfordProj\\data\\properties.xml"));
		Dictionary wordnet = Dictionary.getInstance();

		IndexWord word = wordnet.getIndexWord(POS.VERB, "update");

		PointerTargetNodeList nyms = PointerUtils.getInstance().getSynonyms(word.getSense(2));

		// nyms.

		System.out.println(">>>> " + word.getLemma());

		Synset[] senses = word.getSenses();
		for (int i = 0; i < 2; i++) {
			// System.out.println(
			// ": " + senses[i].getKey() + ": " +
			// senses[i].getWords().toString() + " : " + word.getLemma());

			System.out.println(getRelated(senses[i], PointerType.HYPERNYM));

			Word[] ws = senses[i].getWords();

			if (ws != null) {
				for (int j = 0; j < ws.length; j++) {
					System.out.println(">> " + ws[j].getLemma());
				}
			}

			System.out.println("____________________________________ ");
		}

		/**
		 * Dictionary d = Dictionary.getInstance();
		 * 
		 * IndexWord word = d.getIndexWord(POS.NOUN, "airport");
		 * 
		 * PointerTargetNodeList hypernyms =
		 * PointerUtils.getDirectHypernyms(word.getSenses().get(0));
		 * System.out.println("Direct hypernyms of \"" + word.getLemma() +
		 * "\":"); hypernyms.print();
		 */

		/**
		 * String[] words = new String[] {"generate", "create" }; SynonymMap map
		 * = null; try { map = new SynonymMap(new FileInputStream(
		 * "C:\\Users\\unmeshvinchurkar\\Desktop\\watson\\New
		 * folder\\standfordProj\\data\\wn_s.pl"));
		 * 
		 * 
		 * 
		 * // new File("com/data/wn_s.pl").exists() } catch (Exception e) {
		 * 
		 * } for (int i = 0; i < words.length; i++) { String[] synonyms =
		 * map.getSynonyms(words[i]); System.out.println(words[i] + ":" +
		 * java.util.Arrays.asList(synonyms).toString()); }
		 */
	}

}
