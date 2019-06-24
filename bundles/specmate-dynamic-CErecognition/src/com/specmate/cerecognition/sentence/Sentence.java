package com.specmate.cerecognition.sentence;

public class Sentence {
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
	
	@Override
	public String toString() {
		return index + ": " + root.toString();
	}
}
