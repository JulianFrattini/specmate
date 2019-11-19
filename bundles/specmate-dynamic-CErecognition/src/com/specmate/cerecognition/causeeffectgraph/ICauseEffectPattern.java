package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.sentence.ISentence;

/**
 * 
 * @author Julian Frattini
 * 
 * Interface of cause-effect-patterns. The purpose of a pattern is to store the two genetic algorithms,
 * which are capable of extracting the cause-/effect-expression of a sentence
 */

public interface ICauseEffectPattern {

	/**
	 * Executes the genetic algorithms on a given sentence in order to extract the cause-effect-graph
	 * @param sentence Sentence, where the cause- and effect-expression is to be extracted
	 * @return Cause-effect-graph representing the causal relation of the sentence
	 */
	public ICauseEffectGraph generateGraphFromSentence(ISentence sentence);
	
	/**
	 * Gets a specific genetic algorithm in human-readable form
	 * @param cause True, if the genetic algorithm for the cause-extraction is to be formatted
	 * @return The extraction algorithm of for the cause- or effect-phrase in human-readable form
	 */
	public String getCommandString(boolean cause);
}
