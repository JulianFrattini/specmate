package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.Leaf;

public class CommandPick extends SimpleCommand {
	
	private String dependencyType;

	public CommandPick(String dependencyType) {
		super();
		this.dependencyType = dependencyType;
	}

	@Override
	public String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception {
		if(!(fragment instanceof Leaf)) {
			throw new IllegalArgumentException("Trying to invoke a 'pick'-command on a non-leaf node");
		}
		Leaf leaf = (Leaf) fragment;
		
		for(Leaf gov : leaf.getGoverned()) {
			if(gov.getDependencyRelationType().contentEquals(dependencyType)) {
				if(successor == null) {
					return gov.getCoveredText();
				} else {
					return successor.generateOutput(gov);
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
