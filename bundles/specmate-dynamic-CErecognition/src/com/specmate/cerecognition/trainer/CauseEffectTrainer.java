package com.specmate.cerecognition.trainer;

import java.util.ArrayList;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.pattern.IPattern;

public class CauseEffectTrainer {
	
	private ArrayList<CausalityExample> examples;
	private TrainingStatistics statistics;

	public CauseEffectTrainer(ICausalityExampleReader reader) {
		examples = reader.readExamples();
		statistics = new TrainingStatistics();
	}
	
	public void train(ICauseEffectRecognition subject) {
		statistics.setExamples(examples.size());
		System.out.println("============INITIALIZING TRAINING===============");
		
		for(CausalityExample example : examples) {
			
			IPattern result = subject.train(example.getSentence(), example.getCause(), example.getEffect());
			
			if(example.isCausal()) {
				if(result != null) {
					statistics.addSuccessful();
				} else {
					statistics.addFailing(example, true);
				}
			} else {
				if(result == null) {
					statistics.addSuccessful();
				} else {
					statistics.addFailing(example, false);
				}
			}
		}
		
		statistics.print();
		System.out.println("==============ENDING TRAINING================");
	}
}
