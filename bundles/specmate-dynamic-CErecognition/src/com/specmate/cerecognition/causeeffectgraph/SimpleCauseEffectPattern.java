package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author Julian Frattini
 * Simple version of a cause-effect-pattern mapped to the SimpleCauseEffectGraph, where a 
 * sentence is processed be exactly two genetic algorithms, yielding one cause- and one
 * effect-phrase.
 */

public class SimpleCauseEffectPattern implements ICauseEffectPattern {
	private SimpleCauseEffectGenerator generateCause;
	private SimpleCauseEffectGenerator generateEffect;
	
	public SimpleCauseEffectPattern(SimpleCauseEffectGenerator generateCause,
			SimpleCauseEffectGenerator generateEffect) {
		super();
		this.generateCause = generateCause;
		this.generateEffect = generateEffect;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ICauseEffectGraph generateGraphFromSentence(ISentence sentence) {
		String cause = generateCause.generateCEElement(sentence.getRoot());
		String effect = generateEffect.generateCEElement(sentence.getRoot());
		
		if(cause == null) {
			CELogger.log().error("The cause-expression could not be extracted correctly by the current genetic algorithm.");
			return null;
		}
		if(effect == null) {
			CELogger.log().error("The effect-expression could not be extracted correctly by the current genetic algorithm.");
			return null;
		}
		
		return new SimpleCauseEffectGraph(cause, effect);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCommandString(boolean cause) {
		if(cause) {
			return generateCause.toString();
		} else {
			return generateEffect.toString();
		}
	}
}
