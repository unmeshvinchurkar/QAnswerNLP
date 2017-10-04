package com.coref;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;


public class Detokenizer {

    public String detokenize(List<String> tokens) {
        //Define list of punctuation characters that should NOT have spaces before or after
        List<String> noSpaceBefore = new LinkedList<String>(Arrays.asList(",", ".",";", ":", ")", "}", "]", "'", "'s", "n't"));
        List<String> noSpaceAfter = new LinkedList<String>(Arrays.asList("(", "[","{", "\"",""));

        StringBuilder sentence = new StringBuilder();

        tokens.add(0, "");  //Add an empty token at the beginning because loop checks as position-1 and "" is in noSpaceAfter
        for (int i = 1; i < tokens.size(); i++) {
            if (noSpaceBefore.contains(tokens.get(i))
                    || noSpaceAfter.contains(tokens.get(i - 1))) {
                sentence.append(tokens.get(i));
            } else {
                sentence.append(" " + tokens.get(i));
            }

            // Assumption that opening double quotes are always followed by matching closing double quotes
            // This block switches the " to the other set after each occurrence
            // ie The first double quotes should have no space after, then the 2nd double quotes should have no space before
            if ("\"".equals(tokens.get(i - 1))) {
                if (noSpaceAfter.contains("\"")) {
                    noSpaceAfter.remove("\"");
                    noSpaceBefore.add("\"");
                } else {
                    noSpaceAfter.add("\"");
                    noSpaceBefore.remove("\"");
                }
            }
        }
        return sentence.toString();
    }
}