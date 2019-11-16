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
	
	public void test(ExampleSet set) {
		test(set.getSet());
	}
	
	public void test(ArrayList<CausalityExample> examples) {
		CELogger.log().info("============INITIALIZING TESTING===============");
		
		for(CausalityExample example : examples) {
			CauseEffectRecognitionResult result = null;
			
			if(subject.isExampleValid(example)) {
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
	
	public void resetStatistics() {
		statistics = new TrainingStatistics(false);
		statistics.setPrintOnlyOccurringCategories(true);
	}
	
	public void printStatistics() {
		statistics.print();
	}
}
