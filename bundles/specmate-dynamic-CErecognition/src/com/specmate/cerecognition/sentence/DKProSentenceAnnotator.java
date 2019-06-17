package com.specmate.cerecognition.sentence;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.specmate.cerecognition.util.Globals;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

public class DKProSentenceAnnotator {
	private static DKProSentenceAnnotator instance;
	private static AnalysisEngine engine;
	
	public static DKProSentenceAnnotator getInstance() {
		if(instance == null) 
			instance = new DKProSentenceAnnotator();
		
		return instance;
	}
		
	private DKProSentenceAnnotator() {
		AnalysisEngineDescription segmenter = null;
		AnalysisEngineDescription posTagger = null;
        AnalysisEngineDescription chunker = null;
		AnalysisEngineDescription parser = null;
		AnalysisEngineDescription dependencyParser = null;

		try {
			segmenter = createEngineDescription(OpenNlpSegmenter.class, OpenNlpSegmenter.PARAM_LANGUAGE, "en");
			posTagger = createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_LANGUAGE, "en",
					OpenNlpPosTagger.PARAM_VARIANT, "maxent");
            chunker = createEngineDescription(OpenNlpChunker.class, OpenNlpChunker.PARAM_LANGUAGE, "en");
			dependencyParser = createEngineDescription(MaltParser.class, MaltParser.PARAM_LANGUAGE, "en",
					MaltParser.PARAM_IGNORE_MISSING_FEATURES, true);
			parser = createEngineDescription(OpenNlpParser.class, OpenNlpParser.PARAM_PRINT_TAGSET, true,
					OpenNlpParser.PARAM_LANGUAGE, "en", OpenNlpParser.PARAM_WRITE_PENN_TREE, true,
					OpenNlpParser.PARAM_WRITE_POS, true);
			engine = createEngine(createEngineDescription(segmenter, posTagger, chunker, dependencyParser, parser));
		} catch (Exception e) {
			System.err.println("Error while creating the DKProAnnotator: " + e.getMessage());
			System.err.println(e.toString());
		}
	}
	
	public Sentence createSentence(String text) {
		
		JCas processed = processText(text);
		
		Constituent topConstituent = JCasUtil.select(processed, Constituent.class).iterator().next();
		Fragment root = parseFeatureStructure(topConstituent);
		

		Sentence sentence = new Sentence(Globals.getInstance().getNewSentenceCounter(), 
				root);
		return sentence;
	}
	
	private JCas processText(String text) {
		JCas jcas = null;
		try {
			jcas = JCasFactory.createJCas();
			jcas.setDocumentText(text);
			jcas.setDocumentLanguage("en");
			SimplePipeline.runPipeline(jcas, engine);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return jcas;
	}
	
	private Fragment parseFeatureStructure(FeatureStructure top) {
		Fragment f = null;
		
		if(top instanceof Token) {
			Token token = (Token) top;
			f = new Leaf(token.getPosValue(), token.getCoveredText(), token);
		} else if (top instanceof Constituent) {
			Constituent constituent = (Constituent) top;
			f = new Node(constituent.getConstituentType(), constituent.getCoveredText());
			
			if(constituent.getChildren() != null) {
				for(FeatureStructure child : constituent.getChildren()) {
					((Node) f).addChild(parseFeatureStructure(child));
				}
			}
		}
		
		return f;
	}
	
	/*public Sentence createSentence(int index, String text) {
		DKProSentenceWrapper wrapped = processText(text);
		
		if(wrapped != null) {
			Fragment root = parseFeatureStructure(wrapped.getTopConstituent());
			
			ArrayList<Fragment> tokenNodes = root.getTokenNodes();
			ArrayList<Dependency> dependencies = new ArrayList<>(wrapped.getDependencies());
						
			for(int i = 0; i < tokenNodes.size(); i++) {
				Fragment fragment = tokenNodes.get(i);
				Dependency dependency = dependencies.get(i);
				
				if(fragment.getCoveredText().equals(dependency.getDependent().getCoveredText())) {
					int governorIndex = dependency.getGovernor().getBegin();
					Fragment governor = root.getToken(governorIndex);
					
					fragment.setDependency(governor);
					fragment.setDependencyRelation(dependency.getDependencyType());
				} else {
					System.out.println("ERROR: parsing a sentence yields an error as the list of tokens and the list of dependencies do not align!");
					System.out.println("Tokens: ");
					for(Fragment t : tokenNodes) {
						System.out.print(t.getCoveredText() + " ");
					}
					System.out.println("\nDependencies: ");
					for(Dependency d : dependencies) {
						System.out.print(d.getDependent().getCoveredText() + " ");
					}
					System.out.println();
				}
			}
			
			return new Sentence(index, root);
		}
		
		return null;
	}*/
	
	/*private Fragment parseFeatureStructure(FeatureStructure top) {
		Fragment f = new Fragment();
		f.setCoveredText(((Annotation) top).getCoveredText());
		
		if(top instanceof Token) {
			f.setTag(((Token) top).getPosValue());
			f.setReference((Token) top);
		} else if(top instanceof Constituent) {
			f.setTag(((Constituent) top).getConstituentType());
			
			if(((Constituent) top).getChildren() != null) {
				for(FeatureStructure child : ((Constituent) top).getChildren()) {
					f.addChild(parseFeatureStructure(child));
				}
			}
		}
		
		return f;
	}*/
}

