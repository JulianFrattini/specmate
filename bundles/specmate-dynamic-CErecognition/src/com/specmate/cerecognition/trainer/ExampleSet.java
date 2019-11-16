package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

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
