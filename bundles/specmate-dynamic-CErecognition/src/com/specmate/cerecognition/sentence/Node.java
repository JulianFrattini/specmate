package com.specmate.cerecognition.sentence;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Node extends Fragment {
	
	private ArrayList<Fragment> children;

	public Node(String tag, String coveredText) {
		super(tag, coveredText);
		children = new ArrayList<Fragment>();
	}

	public void addChild(Fragment child) {
		children.add(child);
	}

	public ArrayList<Fragment> getChildren() {
		return children;
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(" ");
		children.forEach(c -> sj.add(c.toString()));
		return sj.toString();
	}
}
