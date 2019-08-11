package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

public class TrainingStatistics {
	private int examples;
	private int successful;
	
	private ArrayList<CausalityExample> falseNegatives = new ArrayList<CausalityExample>();
	private ArrayList<CausalityExample> falsePositives = new ArrayList<CausalityExample>();
	
	public TrainingStatistics() {
		examples = 0;
		successful = 0;
		
		falseNegatives = new ArrayList<CausalityExample>();
		falsePositives = new ArrayList<CausalityExample>();
	}
	
	public void setExamples(int n) {
		examples = n;
	}
	
	public void addSuccessful() {
		successful++;
	}
	
	public void addFailing(CausalityExample failing, boolean falseNegative) {
		(falseNegative ? falseNegatives : falsePositives).add(failing);
	}
	
	public void print() {
		System.out.println("================================================");
		System.out.println("Successfully trained " + successful + "/" + examples + " (" 
				+ getPercentile(successful, examples) + "%)");
		if(!falsePositives.isEmpty()) {
			System.out.println("False positives: "); 
			for(CausalityExample obj : falsePositives) {
				System.out.println(" - " + obj.toString());
			}
		}
		if(!falseNegatives.isEmpty()) {
			System.out.println("False negatives: "); 
			for(CausalityExample obj : falseNegatives) {
				System.out.println(" - " + obj.toString());
			}
		}
		System.out.println("================================================");
	}
	
	private double getPercentile(int z, int n) {
		double percentile = ((double) z)/((double) n);
		int t100 = (int) (percentile*1000);
		return t100/10.0;
	}
}
