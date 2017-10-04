package com.coref;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CorefExample {
  public static void main(String[] args) throws Exception {
    Annotation document = new Annotation("Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.");
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.annotate(document);
    System.out.println("---");
    System.out.println("coref chains");
    
    
    /**
    for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
      System.out.println("\t" + cc);
    }
    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      System.out.println("---");
      System.out.println("mentions");
      for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
        System.out.println("\t" + m);
       }
    }
    */
    
    Map<Integer, CorefChain> coref = document.get(CorefChainAnnotation.class);

    for(Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
        CorefChain c = entry.getValue();

        //this is because it prints out a lot of self references which aren't that useful
        if(c.getMentionsInTextualOrder().size() <= 1)
            continue;

        CorefMention cm = c.getRepresentativeMention();
        String clust = "";
        List<CoreLabel> tks = document.get(SentencesAnnotation.class).get(cm.sentNum-1).get(TokensAnnotation.class);
        for(int i = cm.startIndex-1; i < cm.endIndex-1; i++)
            clust += tks.get(i).get(TextAnnotation.class) + " ";
        clust = clust.trim();
        System.out.println("representative mention: \"" + clust + "\" is mentioned by:");
        
        //c.getMentionsInTextualOrder()

        for(CorefMention m : c.getMentionsInTextualOrder()){
            String clust2 = "";
            tks = document.get(SentencesAnnotation.class).get(m.sentNum-1).get(TokensAnnotation.class);
            for(int i = m.startIndex-1; i < m.endIndex-1; i++)
                clust2 += tks.get(i).get(TextAnnotation.class) + " ";
            clust2 = clust2.trim();
            //don't need the self mention
            if(clust.equals(clust2))
                continue;

            System.out.println("\t" + clust2);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  }
}