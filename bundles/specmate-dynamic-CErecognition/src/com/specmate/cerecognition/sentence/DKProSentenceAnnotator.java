package com.specmate.cerecognition.sentence;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.specmate.cerecognition.util.Globals;
import com.specmate.common.exception.SpecmateException;
import com.specmate.nlp.api.ELanguage;
import com.specmate.nlp.api.INLPService;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class DKProSentenceAnnotator {	
	private INLPService nlp = null;
		
	public DKProSentenceAnnotator(INLPService nlp) {
		this.nlp = nlp;
	}
	
	public Sentence createSentence(String text) {
		JCas processed = null;
		try {
			processed = nlp.processText(text, ELanguage.EN);
			
			Constituent topConstituent = JCasUtil.select(processed, Constituent.class).iterator().next();
			Fragment root = parseFeatureStructure(topConstituent);
			parseDependencies(root, JCasUtil.select(processed, Dependency.class));

			Sentence sentence = new Sentence(
					Globals.getInstance().getNewSentenceCounter(), 
					root);
			return sentence;
		} catch (SpecmateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	
	/**
	 * Parses all dependencies from the JCas object into the leaf elements of the sentence
	 * @param root Root of the parsed sentence
	 * @param dpc Dependency collection of the JCas object
	 */
	private void parseDependencies(Fragment root, Collection<Dependency> dpc) {
		ArrayList<Leaf> tokenNodes = root.getAllLeafs();
		ArrayList<Dependency> dependencies = new ArrayList<Dependency>(dpc);
		
		for(int i = 0; i < tokenNodes.size(); i++) {
			Leaf fragment = tokenNodes.get(i);
			Dependency dependency = dependencies.get(i);
			
			if(fragment.getCoveredText().contentEquals(dependency.getDependent().getCoveredText())) {
				int governorIndex = dependency.getGovernor().getBegin();
				Leaf governor = root.getLeafByToken(governorIndex);

				fragment.setDependencyRelationType(dependency.getDependencyType());
				if(!dependency.getDependencyType().equals("ROOT")) {
					// do not create a recursive root-relation
					fragment.setGovernor(governor);
				}
			} else {
				// error case: the sentence's leafs and list of dependency tokens do not align
			}
		}
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

