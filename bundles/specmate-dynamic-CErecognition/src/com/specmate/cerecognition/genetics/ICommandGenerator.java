package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.sentence.ISentence;

public interface ICommandGenerator {
	
	/*
	 * Genetic algorithm generating a succession of commands, which extracts the
	 * given graph from the given sentence. The return value is an object containing
	 * these commands and, when given a sentence of similar structure, can generate
	 * the correct cause-effect-graph for its specific instance of this sentence
	 * structure.
	 */
	public ICauseEffectPattern generateCommandPatterns(ISentence sentence, ICauseEffectGraph graph);
}
