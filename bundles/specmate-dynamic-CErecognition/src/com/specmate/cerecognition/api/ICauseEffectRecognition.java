package com.specmate.cerecognition.api;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.pattern.IPattern;

public interface ICauseEffectRecognition {

	public ArrayList<IPattern> getPatterns();

	public ICauseEffectGraph getCEG(String sentence);

	public IPattern train(String sentence, String cause, String effect);

}