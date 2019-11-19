package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.sentence.ISentence;

/**
 * 
 * @author Julian Frattini
 *
 * Interface for command generators, which are responsible for creating the genetic algorithm 
 * to extract a phrase from a sentence
 */

public interface ICommandGenerator {
	
	/**
	 * Genetic algorithm generating a succession of commands, which extracts the
	 * given graph from the given sentence. The return value is an object containing
	 * these commands and, when given a sentence of similar structure, can generate
	 * the correct cause-effect-graph for its specific instance of this sentence
	 * structure.
	 * @param sentence The sentence, based on which a extraction algorithm is to be generated
	 * @param graph Cause-effect-relation, which shall be extracted from the given sentence
	 * @return Pattern containing the extraction algorithms to extract the graph from the sentence
	 */
	public ICauseEffectPattern generateCommandPatterns(ISentence sentence, ICauseEffectGraph graph);
}
