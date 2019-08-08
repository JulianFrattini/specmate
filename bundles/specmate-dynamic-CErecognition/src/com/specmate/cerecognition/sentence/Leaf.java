package com.specmate.cerecognition.sentence;

import java.util.ArrayList;

import com.specmate.cerecognition.pattern.StructureElement;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Leaf extends Fragment {

	private Token reference;
	
	private Leaf governer;
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
	
	public Leaf getGoverner() {
		return governer;
	}

	public void setGoverner(Leaf governer) {
		this.governer = governer;
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
	
	public boolean isGoverningAll(ArrayList<String> others) {
		for(String other : others) {
			if(!isGoverning(other, false)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isGoverning(String other, boolean transitive) {
		for(Leaf gov : governed) {
			if(gov.getCoveredText().contentEquals(other)) {
				return true;
			}
		}
		
		// TODO check if transitivity is necessary and fully used
		// transitive relation: check if one of the govened is governing the searched for string
		if(transitive) {
			for(Leaf gov : governed) {
				if(!gov.getCoveredText().contentEquals(other)) {
					if(gov.isGoverning(other, true)) 
						return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public StructureElement generateStructure() {
		return null;
	}
	
	public ArrayList<Fragment> getChildren() {
		return null;
	}
	
	// TODO Specify Exception
	@Override 
	public ArrayList<Fragment> split() throws Exception {
		throw new Exception("Trying to invoke a 'split'-command on a leaf node");
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
	public boolean equals(Fragment other) {
		if(other instanceof Leaf) {
			if(super.getTag().equals(other.getTag()) && 
					super.getCoveredText().equals(other.getCoveredText())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(boolean structurized) {
		if(structurized) {
			return super.getCoveredText() + " (" + super.getTag() + ")";
		} else {
			return super.getCoveredText();
		}
	}
}
