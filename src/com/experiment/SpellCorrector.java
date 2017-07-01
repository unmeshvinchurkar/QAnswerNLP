package com.experiment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.event.SpellChecker;

public class SpellCorrector {

	private static String dictFile = "data/english.0";

	private SpellDictionaryHashMap _dictionary = null;
	private SpellChecker _spellChecker = null;

	public SpellCorrector() throws IOException {
		_dictionary = new SpellDictionaryHashMap(new File(dictFile));
		_spellChecker = new SpellChecker(_dictionary);
	}

	public String getSuggestion(String word) {
		List list = _spellChecker.getSuggestions(word, 1);
		if (list != null && list.size() > 0) {
			return (String) list.get(0);
		}
		return null;
	}

	public List getSuggestions(String word) {
		return _spellChecker.getSuggestions(word, 4);
	}

	public static void main(String[] args) {
		try {
			SpellCorrector s = new SpellCorrector();
			System.out.println(s.getSuggestions("apples"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
