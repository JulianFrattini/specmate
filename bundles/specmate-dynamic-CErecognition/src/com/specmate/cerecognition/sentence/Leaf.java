package com.specmate.cerecognition.sentence;

import java.util.ArrayList;

import com.specmate.cerecognition.pattern.StructureElement;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Leaf extends Fragment {

	private Token reference;
	
	private Leaf governor;
	private ArrayList<Leaf> governed;
	private String dependencyRelationType;
	
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
	
	public boolean isGoverningAllPhrases(ArrayList<String> others) {
		for(String other : others) {
			if(!isGoverningPhrase(other)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isGoverningPhrase(String other) {
		for(Leaf gov : governed) {
			if(gov.getCoveredText().equals(other)) {
				return true;
			} else {
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
	
	public boolean isGoverningAllLeafs(ArrayList<Leaf> others) {
		for(Leaf other : others) {
			if(!isGoverningLeaf(other)) {
				return false;
			}
		}
		
		return true;
	}
	
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

	@Override
	public StructureElement generateStructure() {
		return null;
	}
	
	public ArrayList<Fragment> getChildren() {
		return null;
	}
	
	public ArrayList<Leaf> getAllLeafs() {
		ArrayList<Leaf> result = new ArrayList<Leaf>();
		result.add(this);
		return result;
	}
	
	public Leaf getLeafByToken(int beginIndex) {
		if(reference.getBegin() == beginIndex) {
			return this;
		}
		return null;
	}

	@Override
	public boolean isParenting(Fragment other) {
		return false;
	}

	public int getDegreeOfRelation(Fragment other) {
		int degree = 0;
		while(!other.isParenting(this)) {
			degree++;
			other = other.getParent();
		}
		
		return degree;
	}
	
	// TODO Specify Exception
	@Override 
	public ArrayList<Fragment> split() {
		System.out.println("ERROR: Trying to invoke a 'split'-command on a leaf node");
		return null;
	}
	
	@Override
	public ArrayList<Fragment> select(boolean byType, String indicator, ArrayList<Fragment> selected) {
		if(byType && super.getTag().equals(indicator)) {
			selected.add(this);
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			selected.add(this);
		}
		return selected;
	}

	@Override
	public ArrayList<Fragment> getBy(boolean byType, String indicator, ArrayList<Fragment> selected) {
		return select(byType, indicator, selected);
	}
	
	@Override
	public ArrayList<Leaf> getLeafs(boolean byType, String indicator, ArrayList<Leaf> selected) {
		if(byType && super.getTag().equals(indicator)) {
			selected.add(this);
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			selected.add(this);
		}
		return selected;
	}
	
	@Override
	public Fragment getParentOf(Fragment child) { 
		return null;
	}
	
	@Override
	public Fragment getDirectParentOf(Fragment child) {
		return null;
	}
	
	@Override
	public boolean contains(boolean byType, String indicator) {
		if(byType && super.getTag().equals(indicator)) {
			return true;
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean contains(Fragment fragment) {
		if(this.equals(fragment)) {
			return true;
		}
		return false;
	}
	
	/*@Override
	public boolean equals(Fragment other) {
		if(other instanceof Leaf) {
			if(super.getTag().equals(other.getTag()) && 
					super.getCoveredText().equals(other.getCoveredText())) {
				return true;
			}
		}
		return false;
	}*/
	
	@Override
	public String toString() {
		return super.getCoveredText();
	}

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
	
	@Override
	public String toString(ArrayList<Fragment> highlights) {
		if(highlights.contains(this)) {
			return "*" + super.getCoveredText() + "*";
		} else {
			return super.getCoveredText();
		}
	}
	
	@Override
	public String structureToString() {
		return null;
	}
}
