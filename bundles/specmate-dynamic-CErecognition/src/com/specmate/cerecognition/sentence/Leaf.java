package com.specmate.cerecognition.sentence;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.specmate.cerecognition.pattern.StructureElement;
import com.specmate.cerecognition.util.CELogger;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Leaf extends Fragment {

	/**
	 * Reference to the actual NLP-token, which is represented by this object
	 */
	private Token reference;
	
	/**
	 * Reference to the leaf node which is governing this node dependency-wise.
	 * If this attribute is null, this node is the root dependency node
	 */
	private Leaf governor;

	/**
	 * Tag of the dependency relation, in which this node is to its governor
	 */
	private String dependencyRelationType;
	
	/**
	 * List of all leaf nodes that are governed by this node
	 */
	private ArrayList<Leaf> governed;
	
	public Leaf(String tag, String coveredText, Token reference) {
		super(tag, coveredText);
		this.reference = reference;
		
		governed = new ArrayList<Leaf>();
	}

	public Token getReference() {
		return reference;
	}

	public void setReference(Token reference) {
		this.reference = reference;
	}
	
	public Leaf getGovernor() {
		return governor;
	}

	public void setGovernor(Leaf governer) {
		this.governor = governer;
		governer.addGoverned(this);
	}

	public ArrayList<Leaf> getGoverned() {
		return governed;
	}

	public void addGoverned(Leaf governed) {
		this.governed.add(governed);
	}

	public String getDependencyRelationType() {
		return dependencyRelationType;
	}

	public void setDependencyRelationType(String dependencyRelationType) {
		this.dependencyRelationType = dependencyRelationType;
	}
	
	/**
	 * Identify if this node directly or transitively governing all given phrases
	 * @param others List of phrases, where the governance is to be determined
	 * @return True, if all given phrases are governed by this leaf node
	 */
	public boolean isGoverningAllPhrases(ArrayList<String> others) {
		for(String other : others) {
			if(!isGoverningPhrase(other)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determine, whether a given phrase is directly or transitively governed by this node
	 * @param other The phrase, where the governance is to be determined
	 * @return True, if the given phrase is governed by this leaf node
	 */
	public boolean isGoverningPhrase(String other) {
		for(Leaf gov : governed) {
			if(gov.getCoveredText().equals(other)) {
				// phrase is directly governed by this node
				return true;
			} else {
				// phrase might be transitively governed by this node
				if(!gov.getCoveredText().contentEquals(getCoveredText())) {
					if(gov.isGoverningPhrase(other)) {
						return true;
					}
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Identify if this node directly or transitively governing all given leaf nodes
	 * @param others List of leaf nodes, where the governance is to be determined
	 * @return True, if all given leaf nodes are governed by this leaf node
	 */
	public boolean isGoverningAllLeafs(ArrayList<Leaf> others) {
		for(Leaf other : others) {
			if(!isGoverningLeaf(other)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determine, whether a given leaf node is directly or transitively governed by this node
	 * @param other The leaf node, where the governance is to be determined
	 * @return True, if the given leaf node is governed by this leaf node
	 */
	public boolean isGoverningLeaf(Leaf other) {
		for(Leaf gov : governed) {
			if(gov.equals(other)) {
				return true;
			} else {
				if(!gov.equals(other)) {
					if(gov.isGoverningLeaf(other)) {
						return true;
					}
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Determine, how many governed leaf nodes of the governor of the current node have the same
	 * dependency relation with the shared governor as this node. This may be necessary to specify
	 * this node
	 * @return
	 */
	public int getNumberOfDependencyRelationOccurrencesBeforeThis() {
		int result = 0; 
		
		for(Leaf gov : governor.getGoverned()) {
			if(gov.equals(this)) {
				break;
			} else {
				if(gov.getDependencyRelationType().equals(dependencyRelationType)) {
					result = result + 1;
				}
			}
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureElement generateStructure() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Fragment> getChildren() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Leaf> getAllLeafs() {
		ArrayList<Leaf> result = new ArrayList<Leaf>();
		result.add(this);
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Leaf getLeafByToken(int beginIndex) {
		if(reference.getBegin() == beginIndex) {
			return this;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParenting(Fragment other) {
		return false;
	}

	/**
	 * Calculate the degree of relation from this leaf node to any other given fragment by counting
	 * how many nodes lie between this node and the other
	 * @param other The fragment to which the relation is to be calculated
	 * @return Quantification of relatedness between this node and the other
	 */
	public int getDegreeOfRelation(Fragment other) {
		int degree = 0;
		while(!other.isParenting(this)) {
			degree++;
			other = other.getParent();
		}
		
		return degree;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Fragment> split() {
		CELogger.log().error("Trying to invoke a 'split'-command on a leaf node");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Fragment> select(boolean byType, String indicator, ArrayList<Fragment> selected) {
		if(byType && super.getTag().equals(indicator)) {
			selected.add(this);
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			selected.add(this);
		}
		return selected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Fragment> getBy(boolean byType, String indicator, ArrayList<Fragment> selected) {
		return select(byType, indicator, selected);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Leaf> getLeafs(boolean byType, String indicator, ArrayList<Leaf> selected) {
		if(byType && super.getTag().equals(indicator)) {
			selected.add(this);
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			selected.add(this);
		}
		return selected;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Fragment getParentOf(Fragment child) { 
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Fragment getDirectParentOf(Fragment child) {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(boolean byType, String indicator) {
		if(byType && super.getTag().equals(indicator)) {
			return true;
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Fragment fragment) {
		if(this.equals(fragment)) {
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.getCoveredText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(boolean structurized, boolean dependencies) {
		String result = super.getCoveredText();
		
		if(structurized) {
			result = result + " (" + super.getTag() + ")";
		}
		if(dependencies) {
			if(dependencyRelationType != null && governor != null) {
				result = result + " [--" + dependencyRelationType + "-> " + governor.getCoveredText() + "]";
			} else {
				if(dependencyRelationType.equals("ROOT")) {
					result = result + " [--> ROOT]";
				} else {
					result = result + " [no dependency]";
				}
			}
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(ArrayList<Fragment> highlights) {
		if(highlights.contains(this)) {
			return "*" + super.getCoveredText() + "*";
		} else {
			return super.getCoveredText();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String structureToString() {
		return null;
	}
}
