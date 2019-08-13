package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.util.CELogger;

public class CauseEffectTrainer {
	
	private ArrayList<CausalityExample> examples;
	private TrainingStatistics statistics;

	public CauseEffectTrainer(ICausalityExampleReader reader) {
		examples = reader.readExamples();
		statistics = new TrainingStatistics(false);
		
		CELogger.log().initialize(System.out);
	}
	
	public void train(ICauseEffectRecognition subject) {
		CELogger.log().info("============INITIALIZING TRAINING===============");
		
		for(CausalityExample example : examples) {
			CauseEffectRecognitionResult result = subject.train(example.getSentence(), example.getCause(), example.getEffect());
			
			statistics.add(example, result);
		}
		
		statistics.print();
		CELogger.log().info("==============ENDING TRAINING================");
	}
}
