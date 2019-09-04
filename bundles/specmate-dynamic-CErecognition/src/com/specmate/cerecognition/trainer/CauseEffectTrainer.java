package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

public class CauseEffectTrainer {
	
	private ICauseEffectRecognition subject;
	private TrainingStatistics statistics;

	public CauseEffectTrainer(ICauseEffectRecognition subject) {
		this.subject = subject;
		statistics = new TrainingStatistics(false);
		
		CELogger.log().initialize(System.out);
	}
	
	public void train(ICausalityExampleReader reader) {
		train(reader.readExamples());
	}
	
	public void train(ArrayList<CausalityExample> examples) {
		CELogger.log().info("============INITIALIZING TRAINING===============");
		
		for(CausalityExample example : examples) {
			CauseEffectRecognitionResult result = subject.train(example);
			
			statistics.add(example, result);
		}
		
		CELogger.log().info("==============ENDING TRAINING================");
	}
	
	public void resetStatistics() {
		statistics = new TrainingStatistics(false);
	}
	
	public void printStatistics() {
		statistics.print();
	}
}
