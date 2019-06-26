package com.specmate.cerecognition.pattern;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.sentence.ISentence;

public interface IPattern {
	public boolean checkCompliance(ISentence candidate);
	
	public void addSentence(ISentence candidate);
	
	public ICauseEffectGraph generateCauseEffectGraph(ISentence sentence);
}
