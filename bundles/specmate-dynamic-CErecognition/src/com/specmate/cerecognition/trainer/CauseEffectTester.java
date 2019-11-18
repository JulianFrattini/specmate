package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

/** 
 * 
 * @author Julian Frattini
 * 
 * Analyzer class mainly introduced for the purpose of the author's master thesis.
 * A set of CausalityExamples are put to test in the cause-effect-recognition-system
 * and the tester evaluates, how many examples are successfully recognized
 */

public class CauseEffectTester {
	
	private ICauseEffectRecognition subject;
	private EvaluationStatistics statistics;

	public CauseEffectTester(ICauseEffectRecognition subject) {
		this.subject = subject;
		resetStatistics();
		
		CELogger.log().initializeIfNeccessary(System.out);
	}
	
	/**
	 * Execute test procedure with the given source
	 * @param reader Reader that outputs a list of causality examples
	 */
	public void test(ICausalityExampleReader reader) {
		test(reader.readExamples());
	}
	
	/**
	 * Execute test procedure with the given set of examples
	 * @param set Set of causality examples
	 */
	public void test(ExampleSet set) {
		test(set.getSet());
	}
	
	/**
	 * Perform the test procedure with the given set of examples
	 * @param examples Set of examples
	 */
	public void test(ArrayList<CausalityExample> examples) {
		CELogger.log().info("============INITIALIZING TESTING===============");
		
		for(CausalityExample example : examples) {
			CauseEffectRecognitionResult result = null;
			
			// initially check if the example is actually valid (cause-/effect-phrase a valid substring of the sentence)
			if(subject.isExampleValid(example)) {
				// test the causality example on the system and catch the resulting cause-effect-graph
				ICauseEffectGraph ceg = subject.getCEG(example.getSentence());
				
				// evaluate the result
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
	 * Reset the statistics
	 */
	public void resetStatistics() {
		statistics = new EvaluationStatistics(false);
		statistics.setPrintOnlyOccurringCategories(true);
	}
	
	/**
	 * Output the statistics in human-readable form
	 */
	public void printStatistics() {
		statistics.print();
	}
}
