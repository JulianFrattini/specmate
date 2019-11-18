package com.specmate.cerecognition.pattern;

import com.specmate.cerecognition.sentence.Fragment;

public class Structure implements IStructure {
	private StructureElement root;
	
	public Structure(StructureElement root) {
		this.root = root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureElement getRoot() {
		return root;
	}

	public void setRoot(StructureElement root) {
		this.root = root;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(IStructure other) {
		return root.equals(other.getRoot());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return root.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean compliedBy(Fragment candidateStructure) {
		return root.compliedBy(candidateStructure);
	}
}
