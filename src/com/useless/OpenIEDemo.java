package com.useless;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.*;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.nlp.Concept;
import com.nlp.DependencyExtractor;

/**
 * A demo illustrating how to call the OpenIE system programmatically.
 */
public class OpenIEDemo {

  public static void main(String[] args) throws Exception {
    // Create the Stanford CoreNLP pipeline
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma, ner , depparse,mention, parse, dcoref,natlog,openie, dcoref");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // Annotate an example document.
    Annotation doc = new Annotation("suppose you own an insurance company, that wants to use a product to do some preliminary processing for car insurance.");
    pipeline.annotate(doc);
    
    DependencyExtractor ex = new DependencyExtractor(doc);

    // Loop over sentences in the document
    int sNo =0;
    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
    	Map<String, CoreLabel> tokenMap = new HashMap<String, CoreLabel>();
      // Get the OpenIE triples for the sentence
      Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
      // Print the triples
      for (RelationTriple triple : triples) {
        System.out.println(triple.confidence + "\t" +
            triple.subjectLemmaGloss() + "\t" +
            triple.relationLemmaGloss() + "\t" +
            triple.objectLemmaGloss());
      }
      
      
      SemanticGraph graph = sentence
				.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		String dependentStr = graph.toString(SemanticGraph.OutputFormat.LIST);
		
		 System.out.println(dependentStr ) ;
		 System.out.println();
		Set<Concept> concepts = ex.extractDependencies(tokenMap, dependentStr,  sNo++);
		
		
		for(Concept c: concepts){
			
			System.out.println(c);
			System.out.println("_________________________________________________________________________");
			
			
		}

      
      
    }
  }
}
