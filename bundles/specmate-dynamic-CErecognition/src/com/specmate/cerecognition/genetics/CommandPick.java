package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.Leaf;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author Julian Frattini
 * 
 * Command for horizontal selection: pick commands traverse a sentence through the tree spanned by 
 * the dependency parser. Pick commands can be executed recursively in order to reach deeper nodes of 
 * the spanning tree.
 * It is defined by a dependencyType, which is the type of association between two nodes.
 */

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateOutput(Fragment fragment) {
		if(!(fragment instanceof Leaf)) {
			CELogger.log().warn("Attempting to invoke a pick-command on a non-Leaf node");
			CELogger.log().error(toString() + " on " + fragment.toString());
		}
		
		Leaf leaf = (Leaf) fragment;
		
		// keep track of the number of occurrences of the dependency relation type
		// if one node has multiple child nodes associated to it via the same type, this count allows 
		// to distinct, which child node to select
		int countOccurrencesOfDependencyType = 0;
		
		for(Leaf gov : leaf.getGoverned()) {
			// check for the specific dependency type
			if(gov.getDependencyRelationType().contentEquals(dependencyType)) {
				// only select, if the index of the child node equals the counted occurrences of the dependency type
				if(countOccurrencesOfDependencyType == index) {
					if(successor == null) {
						// generate the cause-/effect-expression
						return gov.getCoveredText();
					} else {
						// continue recursive traversal
						return successor.generateOutput(gov);
					}
				} else {
					countOccurrencesOfDependencyType++;
				}
			}
		}
		
		CELogger.log().warn("No governed leaf node found that complies the given dependency type");
		return "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "pick " + dependencyType + (successor == null ? "" : "->" + successor.toString());
	}

}
