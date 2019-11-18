package com.specmate.cerecognition.trainer;

import java.util.ArrayList;
import java.util.HashMap;

import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author Julian Frattini
 * 
 * This class was introduced for evaluation purposes, mainly considering the author's master thesis.
 * Its main purpose is to store the results of training- and testing-procedures and output these in 
 * a human-readable way.
 */

public class EvaluationStatistics {
	
	/**
	 * Storage for results, where each type of result (CauseEffectRecognitionResult) is
	 * associated with a list of CausalityExamples, which produced the respective result
	 */
	private HashMap<CauseEffectRecognitionResult, ArrayList<CausalityExample>> results;
	
	/**
	 * True if only causality examples, which produce a negative result, shall be printed
	 */
	private boolean printOnlyFailingExamples;
	
	/**
	 * True, if only categories, that actually occur, shall be printed
	 */
	private boolean printOnlyOccurringCategories;
	
	public EvaluationStatistics(boolean printOnlyFailingExamples) {
		this.printOnlyFailingExamples = printOnlyFailingExamples;
		this.printOnlyOccurringCategories = false;
		
		results = new HashMap<CauseEffectRecognitionResult, ArrayList<CausalityExample>>();
		for(CauseEffectRecognitionResult type : CauseEffectRecognitionResult.values()) {
			results.put(type, new ArrayList<CausalityExample>());
		}
	}
	
	public void setPrintOnlyOccurringCategories(boolean printOnlyOccurringCategories) {
		this.printOnlyOccurringCategories = printOnlyOccurringCategories;
	}

	public void add(CausalityExample example, CauseEffectRecognitionResult resultType) {
		results.get(resultType).add(example);
	}
	
	/**
	 * Output the evaluation results in a human-readable form
	 */
	public void print() {
		CELogger.log().info("================================================");
		
		int successful = getNumberOfExamples(true);
		int examples = getNumberOfExamples(false);
		CELogger.log().info("Successfully trained " + successful + "/" + examples + " (" 
				+ getPercentile(successful, examples) + "%)");
		
		for(CauseEffectRecognitionResult type : CauseEffectRecognitionResult.values()) {
			if(printOnlyFailingExamples && isCERecResultPositive(type)) {
				continue;
			}
			
			int typeOccurrence = results.get(type).size();
			
			if(printOnlyOccurringCategories && typeOccurrence == 0) {
				continue;
			}
			
			CELogger.log().info("Examples trained with result type " + type.toString() + " (" + typeOccurrence + " times, " + getPercentile(typeOccurrence, examples) + "%): "); 
			for(CausalityExample obj : results.get(type)) {
				CELogger.log().info(" - " + obj.toString());
			}
		}
		CELogger.log().info("================================================");
	}
	
	/**
	 * Count the number of evaluated causality examples that produced a specific result
	 * @param onlySuccessful True, if only positive results shall be counted
	 * @return The number of (either all or only the positive) results
	 */
	private int getNumberOfExamples(boolean onlySuccessful) {
		int result = 0;
		for(CauseEffectRecognitionResult type : CauseEffectRecognitionResult.values()) {
			if(onlySuccessful) {
				if(isCERecResultPositive(type)) {
					result = result + results.get(type).size();
				}
			} else {
				result = result + results.get(type).size();
			}
		}
		return result;
	}
	
	/**
	 * Determines, whether a specific CauseEffectRecognitionResult is considered positive or not
	 * @param type The CauseEffectRecognitionResult to be evaluated
	 * @return True, if the type is considered positive
	 */
	private boolean isCERecResultPositive(CauseEffectRecognitionResult type) {
		if(type.equals(CauseEffectRecognitionResult.CREATION_SUCCESSFUL) || 
				type.equals(CauseEffectRecognitionResult.DISCARDING_SUCCESSFUL) ||
				type.equals(CauseEffectRecognitionResult.RECOGNITION_SUCCESSFUL) ||
				type.equals(CauseEffectRecognitionResult.DEFLECTION_SUCCESSFUL) ||
				type.equals(CauseEffectRecognitionResult.SPLITTING_SUCCESSFUL) )
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Calculates the devision between two integers and formats it with two positions after the decimal point
	 * @param z Numerator
	 * @param n Denominator
	 * @return Division z/n
	 */
	private double getPercentile(int z, int n) {
		double percentile = ((double) z)/((double) n);
		int t100 = (int) (percentile*1000);
		return t100/10.0;
	}
}
