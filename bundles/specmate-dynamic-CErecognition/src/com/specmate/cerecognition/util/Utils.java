package com.specmate.cerecognition.util;

import java.util.ArrayList;

import com.specmate.cerecognition.sentence.Leaf;

/**
 * 
 * @author Julian Frattini
 * 
 * Utility class with universally applicable operations which may be used in multiple instances
 */

public class Utils {
	
	/**
	 * Generates a list from an array without a given exception
	 * @param list The array of strings, where one string shall be removed
	 * @param except The exception, which has to be removed
	 * @return List of String where the exception has been removed
	 */
	public static ArrayList<String> generateListWithout(String[] list, String except) {
		ArrayList<String> set = new ArrayList<String>();
		
		for(String item : list) {
			if(!item.contentEquals(except)) {
				set.add(item);
			}
		}
		
		return set;
	}
	
	/**
	 * Generates a new list of leafs from an existing one with the exception of one leaf
	 * @param leafs The list of leaf nodes, where one leaf shall be removed
	 * @param except The exception, which has to be removed
	 * @return List of leaf nodes where the exception has been removed
	 */
	public static ArrayList<Leaf> generateListWithout(ArrayList<Leaf> leafs, Leaf except) {
		ArrayList<Leaf> set = new ArrayList<Leaf>();
		
		for(Leaf leaf : leafs) {
			if(!leaf.equals(except)) {
				set.add(leaf);
			}
		}
		
		return set;
	}
	
	/**
	 * Get the index of a word within an expression
	 * @param expression The expression covering the word
	 * @param word The word, where the index is of interest
	 * @return The index of the word within the expression
	 */
	public static int getPositionOfWordInExpression(String expression, String word) {
		String[] split = expression.split(" ");
		
		for(int i = 0; i < split.length; i++) {
			if(split[i].contentEquals(word)) 
				return i;
		}
		return 0;
	}
}
