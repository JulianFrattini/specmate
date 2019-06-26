package com.specmate.cerecognition.pattern;

import com.specmate.cerecognition.sentence.Fragment;

public interface IStructure {
	public StructureElement getRoot();
	
	public boolean equals(IStructure other);
	
	public boolean compliedBy(Fragment candidateStructure);
	
	public String toString();
}
