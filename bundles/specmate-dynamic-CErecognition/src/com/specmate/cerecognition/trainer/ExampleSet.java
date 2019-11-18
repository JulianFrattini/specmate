package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

/**
 * 
 * @author Julian Frattini
 * 
 * Container class for a list of CausalityExamples. The main purpose of this class is to
 * manage the list of examples and especially provide support for evaluation methods like
 * a cross-validation.
 */

public class ExampleSet {
	private ArrayList<CausalityExample> set;
	
	public ExampleSet() {
		set = new ArrayList<CausalityExample>();
	}
	
	public ArrayList<CausalityExample> getSet() {
		return set;
	}

	public void setSet(ArrayList<CausalityExample> set) {
		this.set = set;
	}

	public void addSet(ArrayList<CausalityExample> set) {
		this.set.addAll(set);
	}

	/**
	 * Returns a randomly selected portion of the full set
	 * @param percentage Relative size of the portion given in the range of 0 to 1 (=100%)
	 * @return A percentage of the set of examples
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<CausalityExample> getPortion(double percentage) {
		ArrayList<CausalityExample> portion = new ArrayList<CausalityExample>();
		ArrayList<CausalityExample> copy = (ArrayList<CausalityExample>) set.clone();
		
		if(percentage == 1) {
			return copy;
		}
		
		int n = (int) (percentage*set.size());
		
		for(int i = 0; i < n; i++) {
			int index = (int) (Math.random()*copy.size());
			index = Math.min(index, set.size());
			
			portion.add(copy.remove(index));
		}
		
		return portion;
	}

	/**
	 * Randomly splits the example set into two exclusive sets
	 * @param ratio Relative size of set one given in the range of 0 to 1 (=100%)
	 * @param one Resulting first set
	 * @param two Resulting second set
	 */
	@SuppressWarnings("unchecked")
	public void split(double ratio, ExampleSet one, ExampleSet two) {
		ArrayList<CausalityExample> portion = new ArrayList<CausalityExample>();
		ArrayList<CausalityExample> copy = (ArrayList<CausalityExample>) set.clone();
		
		int n = (int) (ratio*set.size());
		
		for(int i = 0; i < n; i++) {
			int index = (int) (Math.random()*copy.size());
			index = Math.min(index, set.size());
			
			portion.add(copy.remove(index));
		}
		
		one.setSet(portion);
		two.setSet(copy);
	}
}
