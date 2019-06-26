package com.specmate.cerecognition.sentence;

import com.specmate.cerecognition.pattern.IStructure;
import com.specmate.cerecognition.pattern.Structure;

public class Sentence implements ISentence {
	private int index;
	private Fragment root;
	
	public Sentence(int index, Fragment root) {
		this.index = index;
		this.root = root;
	}

	public int getIndex() {
		return index;
	}

	public Fragment getRoot() {
		return root;
	}

	public void setRoot(Fragment root) {
		this.root = root;
	}

	/**
	 * Extracts the constituency tree without its leaf nodes
	 */
	@Override
	public IStructure generateStructure() {
		return new Structure(root.generateStructure());
	}
	
	@Override
	public String toString() {
		return index + ": " + root.toString();
	}
}
