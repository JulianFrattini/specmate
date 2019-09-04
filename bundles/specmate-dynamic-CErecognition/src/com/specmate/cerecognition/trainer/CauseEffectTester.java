package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

public class CauseEffectTester {
	
	private ICauseEffectRecognition subject;
	private TrainingStatistics statistics;

	public CauseEffectTester(ICauseEffectRecognition subject) {
		this.subject = subject;
		resetStatistics();
		
		CELogger.log().initializeIfNeccessary(System.out);
	}
	
	public void test(ICausalityExampleReader reader) {
		test(reader.readExamples());
	}
	
	public void test(ArrayList<CausalityExample> examples) {
		CELogger.log().info("============INITIALIZING TESTING===============");
		
		for(CausalityExample example : examples) {
			CauseEffectRecognitionResult result = null;
			
			if(isExampleValid(example)) {
				ICauseEffectGraph ceg = subject.getCEG(example.getSentence());
				
				if(example.isCausal()) {
					if(ceg != null) {
						if(ceg.equals(example.getCEG())) {
							result = CauseEffectRecognitionResult.RECOGNITION_SUCCESSFUL;
						} else {
							result = CauseEffectRecognitionResult.RECOGNITION_FAILED;
						}
					} else {
						result = CauseEffectRecognitionResult.RECOGNITION_FAILED;
					}
				} else {
					if(ceg == null) {
						result = CauseEffectRecognitionResult.DISCARDING_SUCCESSFUL;
					} else {
						result = CauseEffectRecognitionResult.DISCARDING_FAILED;
					}
				}
			} else {
				// invalid example
				result = CauseEffectRecognitionResult.RECOGNITION_IMPOSSIBLE;
			}
			
			statistics.add(example, result);
		}
		
		CELogger.log().info("==============ENDING TESTING================");
	}
	
	/**
	 * Checks, whether the cause- and effect-expression of a causal example are valid substrings of the sentence
	 * @param example Causal or non-causal sentence
	 * @return True, if the example is possibly analyzable
	 */
	private boolean isExampleValid(CausalityExample example) {
		if(example.isCausal()) {
			if(example.getSentence().contains(example.getCause()) &&
					example.getSentence().contains(example.getEffect())) {
				// an example is only valid, if the cause- and effect-expression is a substring of the sentence
				return true;
			} else {
				// there is no possibility to extract the expressions
				return false;
			}
		} else {
			// non-causal sentences just have to be discarded
			return true;
		}
	}
	
	public void resetStatistics() {
		statistics = new TrainingStatistics(false);
		statistics.setPrintOnlyOccurringCategories(true);
	}
	
	public void printStatistics() {
		statistics.print();
	}
}
