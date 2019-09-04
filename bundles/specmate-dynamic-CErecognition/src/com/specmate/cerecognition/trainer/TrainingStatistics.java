package com.specmate.cerecognition.trainer;

import java.util.ArrayList;
import java.util.HashMap;

import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

public class TrainingStatistics {
	private HashMap<CauseEffectRecognitionResult, ArrayList<CausalityExample>> results;
	private boolean printOnlyFailingExamples;
	private boolean printOnlyOccurringCategories;
	
	public TrainingStatistics(boolean printOnlyFailingExamples) {
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
	
	private double getPercentile(int z, int n) {
		double percentile = ((double) z)/((double) n);
		int t100 = (int) (percentile*1000);
		return t100/10.0;
	}
}
