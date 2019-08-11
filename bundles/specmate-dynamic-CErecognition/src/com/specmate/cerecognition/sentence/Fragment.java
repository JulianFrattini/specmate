package com.specmate.cerecognition.sentence;

import java.util.ArrayList;

import com.specmate.cerecognition.pattern.StructureElement;

public abstract class Fragment {
	private String tag;
	private String coveredText;
	
	public Fragment(String tag, String coveredText) {
		this.tag = tag;
		this.coveredText = coveredText;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCoveredText() {
		return coveredText;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}
	
	public abstract StructureElement generateStructure();
	public abstract ArrayList<Fragment> getChildren();
	public abstract ArrayList<Leaf> getAllLeafs();
	public abstract Leaf getLeafByToken(int beginIndex);
	
	// Manipulation
	public abstract ArrayList<Fragment> split() throws Exception;
	public abstract ArrayList<Fragment> getBy(boolean byType, String indicator, ArrayList<Fragment> selected);
	public abstract ArrayList<Fragment> select(boolean byType, String indicator, ArrayList<Fragment> selected);
	
	// General
	public abstract String toString(boolean structurized, boolean dependencies);
	public abstract boolean equals(Fragment other);
	
}
