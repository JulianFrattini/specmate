package com.specmate.cerecognition.sentence;

import java.util.ArrayList;

import com.specmate.cerecognition.pattern.StructureElement;

public abstract class Fragment {
	private Fragment parent;
	
	private String tag;
	private String coveredText;
	
	public Fragment(String tag, String coveredText) {
		this.tag = tag;
		this.coveredText = coveredText;
	}

	public Fragment getParent() {
		return parent;
	}

	public void setParent(Fragment parent) {
		this.parent = parent;
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
	public abstract boolean isParenting(Fragment other);
	public abstract ArrayList<Leaf> getAllLeafs();
	public abstract Leaf getLeafByToken(int beginIndex);
	
	// Manipulation
	public abstract ArrayList<Fragment> split();
	public abstract ArrayList<Fragment> getBy(boolean byType, String indicator, ArrayList<Fragment> selected);
	public abstract ArrayList<Leaf> getLeafs(boolean byType, String indicator, ArrayList<Leaf> selected);
	public abstract ArrayList<Fragment> select(boolean byType, String indicator, ArrayList<Fragment> selected);
	public abstract boolean contains(boolean byType, String indicator);
	
	// General
	public abstract String toString(boolean structurized, boolean dependencies);
	public abstract String toString(ArrayList<Fragment> highlights);
	//public abstract boolean equals(Fragment other);
	
}
