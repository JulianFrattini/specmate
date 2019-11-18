package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author julian
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
	 * Applies the two genetic algorithms to the sentence and filters the cause- and effect-expression
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
	 * Generates a human-readable form of the genetic algorithms
	 * @param cause True, if the genetic algorithm for the cause-phrase is selected, False for effect-algorithm
	 * @return The genetic extraction-algorithm in human-readable form
	 */
	public String getCommandString(boolean cause) {
		if(cause) {
			return generateCause.toString();
		} else {
			return generateEffect.toString();
		}
	}
}
