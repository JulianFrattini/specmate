package com.specmate.cerecognition.sentence;

import com.specmate.cerecognition.pattern.IStructure;

public interface ISentence {

	/**
	 * Provides the root node of this sentence
	 * @return The root fragment of the sentence
	 */
	public Fragment getRoot();
	
	/**
	 * Generates the sentence structure of this sentence by disregarding leaf nodes
	 * @return The sentence structure of this sentence
	 */
	public IStructure generateStructure();
	
	/**
	 * Converts the fragment structure into a human-readable form
	 * @return The fragment structure of the sentence converted into a String
	 */
	public String toString();
}
