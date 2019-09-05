package com.specmate.cerecognition.api;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.trainer.CausalityExample;

public interface ICauseEffectRecognition {

	public ArrayList<IPattern> getPatterns();

	public ICauseEffectGraph getCEG(String sentence);

	public CauseEffectRecognitionResult train(CausalityExample sentence);
	public boolean isExampleValid(CausalityExample sentence);
}