package com.specmate.cerecognition.api;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.pattern.IPattern;

public interface ICauseEffectTrainer {

	ArrayList<IPattern> getPatterns();

	ICauseEffectGraph getCEG(String sentence);

	void train(String sentence, String cause, String effect);

}