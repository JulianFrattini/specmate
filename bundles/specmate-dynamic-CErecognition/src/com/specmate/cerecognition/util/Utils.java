package com.specmate.cerecognition.util;

import java.util.ArrayList;

import com.specmate.cerecognition.sentence.Leaf;

public class Utils {
	public static ArrayList<String> generateListWithout(String[] list, String except) {
		ArrayList<String> set = new ArrayList<String>();
		
		for(String item : list) {
			if(!item.contentEquals(except)) {
				set.add(item);
			}
		}
		
		return set;
	}
	
	public static ArrayList<Leaf> generateListWithout(ArrayList<Leaf> leafs, Leaf except) {
		ArrayList<Leaf> set = new ArrayList<Leaf>();
		
		for(Leaf leaf : leafs) {
			if(!leaf.equals(except)) {
				set.add(leaf);
			}
		}
		
		return set;
	}
	
	public static int getPositionOfWordInExpression(String expression, String word) {
		String[] split = expression.split(" ");
		
		for(int i = 0; i < split.length; i++) {
			if(split[i].contentEquals(word)) 
				return i;
		}
		return 0;
	}
}
