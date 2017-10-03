
package com.useless;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.io.Files;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreLabel.OutputFormat;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/** A simple corenlp example ripped directly from the Stanford CoreNLP website using text from wikinews. */
public class SimpleExample {

  public static void main(String[] args) throws IOException {
    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos,lemma,ner, parse ");
    
   // props.put("annotators", "tokenize, ssplit, pos,lemma,ner, regexner  ");
   // props.put("regexner.mapping", "C:\\Users\\unmeshvinchurkar\\Desktop\\regexner.txt");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    
    // read some text from the file..
    File inputFile = new File("C:\\Users\\unmeshvinchurkar\\Desktop\\sample.txt");
    String text ="The strongest rain ever recorded in India shut down the financial hub of Mumbai, snapped communication lines, closed airports and forced thousands of people to sleep in their offices or walk home during the night, officials said today.";
	
    //Files.toString(inputFile, Charset.forName("UTF-8"));

    // create an empty Annotation just with the given text
    Annotation document = new Annotation(text);

    // run all Annotators on this text
    pipeline.annotate(document);
    
 /**
    for (CoreMap entityMention : document.get(CoreAnnotations.MentionsAnnotation.class)) {
      //  System.out.println(entityMention);
        
        String word = entityMention.get(CoreAnnotations.TextAnnotation.class);
        String ne = entityMention.get(CoreAnnotations.NamedEntityTagAnnotation.class);
        
        System.out.println("word: " + word + " ne:" + ne);
      }
*/
    
    
    // these are all the sentences in this document
    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    for(CoreMap sentence: sentences) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
        // this is the text of the token
        String word = token.get(TextAnnotation.class);
        // this is the POS tag of the token
      //  String pos = token.get(PartOfSpeechAnnotation.class);
       
        // this is the NER label of the token
        String ne = token.get(NamedEntityTagAnnotation.class);
        
        System.out.println("word: " + word + " ne:" + ne);
        
       // System.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
      }
      
      
    SemanticGraph graph =
		 sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
    System.out.println(graph.toString(SemanticGraph.OutputFormat.LIST));
      
  	Tree tree =
		 sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		 tree.pennPrint(System.out);
		 
		 graph = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
      
		 graph.toString(SemanticGraph.OutputFormat.LIST);
		 
     // System.out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString());
		// out.println("//////////////////////////////////////////////////////////");
		//

    
    }
    
    

 
    
  }

}