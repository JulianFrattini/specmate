package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.Leaf;

public class CommandPick extends SimpleCommand {
	
	private String dependencyType;
	
	// if one leaf governs multiple leafs with the same dependency type, enable an indexed picking
	private int index;

	public CommandPick(String dependencyType) {
		super();
		this.dependencyType = dependencyType;
		index = 0;
	}
	
	public CommandPick(String dependencyType, int index) {
		super();
		this.dependencyType = dependencyType;
		this.index = index;
	}

	@Override
	public String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception {
		if(!(fragment instanceof Leaf)) {
			throw new IllegalArgumentException("Trying to invoke a 'pick'-command on a non-leaf node");
		}
		Leaf leaf = (Leaf) fragment;
		
		int countOccurrencesOfDependencyType = 0;
		for(Leaf gov : leaf.getGoverned()) {
			if(gov.getDependencyRelationType().contentEquals(dependencyType)) {
				if(countOccurrencesOfDependencyType == index) {
					if(successor == null) {
						return gov.getCoveredText();
					} else {
						return successor.generateOutput(gov);
					}
				} else {
					countOccurrencesOfDependencyType++;
				}
			}
		}
		
		throw new IllegalArgumentException("No governed leaf node found that complies the given dependency type");
	}

	@Override
	public String toString() {
		return "pick " + dependencyType;
	}

}
