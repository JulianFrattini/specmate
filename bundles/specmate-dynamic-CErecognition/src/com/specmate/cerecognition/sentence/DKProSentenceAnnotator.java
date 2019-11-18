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

/**
 * 
 * @author Julian Frattini
 * 
 * Wrapper around the natural language sentence annotation, utilizing the INLPService of specmate.
 * The purpose of this class is to formalize natural language sentences and parse them into the
 * specific internal representation, which currently consists of a combination of constituencies and
 * dependencies.
 */

public class DKProSentenceAnnotator {	
	private INLPService nlp = null;
		
	public DKProSentenceAnnotator(INLPService nlp) {
		this.nlp = nlp;
	}
	
	/**
	 * Formalize a natural language sentence
	 * @param text The sentence to be formalized
	 * @return Internal, formal representation of the sentence
	 */
	public Sentence createSentence(String text) {
		JCas processed = null;
		try {
			// process the text via the INLPService
			processed = nlp.processText(text, ELanguage.EN);
			
			// parse the constituents into the internal representation
			Constituent topConstituent = JCasUtil.select(processed, Constituent.class).iterator().next();
			Fragment root = parseFeatureStructure(topConstituent);
			// add the dependencies to the constituents
			parseDependencies(root, JCasUtil.select(processed, Dependency.class));

			// create a new sentence object 
			Sentence sentence = new Sentence(
					Globals.getInstance().getNewSentenceCounter(), 
					root);
			return sentence;
		} catch (SpecmateException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses the feature structures of the constituency parser into the internal representation using fragments
	 * @param top Root node of the constituency parser
	 * @return Root node of the internal representation
	 */
	private Fragment parseFeatureStructure(FeatureStructure top) {
		Fragment f = null;
		
		if(top instanceof Token) {
			// tokens represent leaf nodes
			Token token = (Token) top;
			f = new Leaf(token.getPosValue(), token.getCoveredText(), token);
		} else if (top instanceof Constituent) {
			// constituents represent inner nodes
			Constituent constituent = (Constituent) top;
			f = new Node(constituent.getConstituentType(), constituent.getCoveredText());
			
			// recursively travers child nodes and add the full tree
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
}

