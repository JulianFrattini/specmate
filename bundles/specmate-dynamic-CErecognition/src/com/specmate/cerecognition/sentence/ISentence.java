package com.specmate.cerecognition.sentence;

import com.specmate.cerecognition.pattern.IStructure;

public interface ISentence {

	public String toString();
	public Fragment getRoot();
	
	public IStructure generateStructure();
}
