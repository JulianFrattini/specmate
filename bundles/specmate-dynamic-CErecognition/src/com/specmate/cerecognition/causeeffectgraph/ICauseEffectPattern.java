package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.sentence.ISentence;

public interface ICauseEffectPattern {
	public ICauseEffectGraph generateGraphFromSentence(ISentence sentence);
}
