package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.sentence.ISentence;

/**
 * 
 * @author julian
 * Interface of cause-effect-patterns. The purpose of a pattern is to store the two genetic algorithms,
 * which are capable of extracting the cause-/effect-expression of a sentence
 */

public interface ICauseEffectPattern {
	// execute the genetic algorithms on a given sentence in order to extract the cause-effect-graph
	public ICauseEffectGraph generateGraphFromSentence(ISentence sentence);
	
	// get a specific genetic algorithm in human-readable form
	public String getCommandString(boolean cause);
}
