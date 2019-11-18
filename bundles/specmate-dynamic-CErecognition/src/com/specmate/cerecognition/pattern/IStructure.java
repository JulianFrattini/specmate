package com.specmate.cerecognition.pattern;

import com.specmate.cerecognition.sentence.Fragment;

/**
 * 
 * @author Julian Frattini
 * 
 * Interface for the sentence structure. Its main purpose is to prove compliance
 * to a sentence. It is different to the internal representation of a sentence mainly
 * because it lacks all leaf nodes of a sentence's tree nodes.
 */

public interface IStructure {
	/**
	 * Yields the root node of the sentence structure's tree
	 * @return The root node of the sentence structure
	 */
	public StructureElement getRoot();
	
	/**
	 * Checks equality to a different sentence structure
	 * @param other The sentence structure with which this one is compared
	 * @return True, if the two sentence structures are equal
	 */
	public boolean equals(IStructure other);
	
	/**
	 * Checks if a sentence is compliant to this sentence structure
	 * @param candidateStructure The root node of a sentence's internal representation
	 * @return True, if the inner nodes of the given candidate structure equals this sentence structure's nodes
	 */
	public boolean compliedBy(Fragment candidateStructure);
	
	public String toString();
}
