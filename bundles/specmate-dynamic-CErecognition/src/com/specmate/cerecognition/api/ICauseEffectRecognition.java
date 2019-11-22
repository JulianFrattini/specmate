package com.specmate.cerecognition.api;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.trainer.CausalityExample;

public interface ICauseEffectRecognition {

	/**
	 * Returns all the patterns generated in the training process
	 * @return List of existing patterns
	 */
	public ArrayList<IPattern> getPatterns();

	/**
	 * Attempts to generate a cause-effect-graph from a sentence
	 * @param sentence The sentence, where the causality shall be extracted
	 * @return A cause-effect-graph, if the sentence is causal and the pattern known
	 */
	public ICauseEffectGraph getCEG(String sentence);

	/**
	 * Trains the algorithm with a new sentence pattern
	 * @param sentence A causality example containing a sentence and a cause-effect-graph
	 * @return The result of the training process
	 */
	public CauseEffectRecognitionResult train(CausalityExample sentence);
	
	/**
	 * Checks whether a causality example is valid (cause-/effect-phrase must be a valid substring of the sentence
	 * @param sentence The causality example, which is to be checked
	 * @return True, if the causality example is valid
	 */
	public boolean isExampleValid(CausalityExample sentence);
}