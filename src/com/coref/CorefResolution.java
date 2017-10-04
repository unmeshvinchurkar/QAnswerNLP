package com.coref;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class CorefResolution {
    public static String corefResolute(String text, List<String> tokenToReplace) {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);

        Map<Integer, CorefChain> corefs =  doc.get(CorefChainAnnotation.class);
        System.out.println(corefs);
        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> resolved = new ArrayList<String>();

        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            for (CoreLabel token : tokens) {

                Integer corefClustId = token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
                //token.get(Coref)

                if (corefClustId == null) {
                    System.out.println("NULL NULL NULL\n");
                    resolved.add(token.word());
                    continue;
                }
                else {
                    System.out.println("Exist Exist Exist\n");
                }

                System.out.println("coreClustId is "+corefClustId.toString()+"\n");
                CorefChain chain = corefs.get(corefClustId);

                if (chain == null || chain.getMentionsInTextualOrder().size() == 1) {
                    resolved.add(token.word());
                } else {
                    int sentINdx = chain.getRepresentativeMention().sentNum - 1;
                    CoreMap corefSentence = sentences.get(sentINdx);
                    List<CoreLabel> corefSentenceTokens = corefSentence.get(CoreAnnotations.TokensAnnotation.class);

                    CorefChain.CorefMention reprMent = chain.getRepresentativeMention();

                    if (tokenToReplace.contains(token.word())) {
                        for (int i = reprMent.startIndex; i < reprMent.endIndex; i++) {
                            CoreLabel matchedLabel = corefSentenceTokens.get(i - 1);
                            resolved.add(matchedLabel.word());
                        }
                    } else {
                        resolved.add(token.word());
                    }
                }
            }
        }

        Detokenizer detokenizer = new Detokenizer();
        String resolvedStr = detokenizer.detokenize(resolved);

        return resolvedStr;
    }
    
    public static void main(String args[]){
    	
    	 List<String> tokenToReplace = Arrays.asList("He", "he", "She", "she", "It", "it", "They", "they"); //!!!
         String resolvedLine = CorefResolution.corefResolute("Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.", tokenToReplace);
        
         resolvedLine = resolvedLine;
    	
    	
    }
}