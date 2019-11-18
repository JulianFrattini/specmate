package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

public class CauseEffectTrainer {
	
	private ICauseEffectRecognition subject;
	private EvaluationStatistics statistics;

	public CauseEffectTrainer(ICauseEffectRecognition subject) {
		this.subject = subject;
		statistics = new EvaluationStatistics(false);
		
		CELogger.log().initialize(System.out);
	}
	
	/**
	 * Execute training procedure with the given source
	 * @param reader Reader that outputs a list of causality examples
	 */
	public void train(ICausalityExampleReader reader) {
		train(reader.readExamples());
	}
	
	/**
	 * Execute training procedure with the given set of examples
	 * @param set Set of causality examples
	 */
	public void train(ExampleSet set) {
		train(set.getSet());
	}
	
	/**
	 * Perform the training procedure with the given set of examples
	 * @param examples Set of examples
	 */
	public void train(ArrayList<CausalityExample> examples) {
		CELogger.log().info("============INITIALIZING TRAINING===============");
		
		for(CausalityExample example : examples) {
			CauseEffectRecognitionResult result = subject.train(example);
			
			statistics.add(example, result);
		}
		
		CELogger.log().info("==============ENDING TRAINING================");
	}
	
	/**
	 * Reset the statistics
	 */
	public void resetStatistics() {
		statistics = new EvaluationStatistics(false);
	}
	
	/**
	 * Output the statistics in human-readable form
	 */
	public void printStatistics() {
		statistics.print();
	}
}
