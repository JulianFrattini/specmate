package com.specmate.cerecognition.pattern;

import com.specmate.cerecognition.sentence.Fragment;

public class Structure implements IStructure {
	private StructureElement root;
	
	public Structure(StructureElement root) {
		this.root = root;
	}

	public StructureElement getRoot() {
		return root;
	}

	public void setRoot(StructureElement root) {
		this.root = root;
	}
	
	public boolean equals(IStructure other) {
		return root.equals(other.getRoot());
	}
	
	@Override
	public String toString() {
		return root.toString();
	}

	@Override
	public boolean compliedBy(Fragment candidateStructure) {
		return root.compliedBy(candidateStructure);
	}
}
