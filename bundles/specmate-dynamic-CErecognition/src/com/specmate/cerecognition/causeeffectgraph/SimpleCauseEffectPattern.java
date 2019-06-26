package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.sentence.ISentence;

public class SimpleCauseEffectPattern implements ICauseEffectPattern {
	private SimpleCauseEffectGenerator generateCause;
	private SimpleCauseEffectGenerator generateEffect;
	
	public SimpleCauseEffectPattern(SimpleCauseEffectGenerator generateCause,
			SimpleCauseEffectGenerator generateEffect) {
		super();
		this.generateCause = generateCause;
		this.generateEffect = generateEffect;
	}

	@Override
	public ICauseEffectGraph generateGraphFromSentence(ISentence sentence) {
		return new SimpleCauseEffectGraph(
				generateCause.generateCEElement(sentence.getRoot()),
				generateEffect.generateCEElement(sentence.getRoot()));
	}

}
