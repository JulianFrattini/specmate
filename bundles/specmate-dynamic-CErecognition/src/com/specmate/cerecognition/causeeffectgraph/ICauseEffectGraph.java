package com.specmate.cerecognition.causeeffectgraph;

/**
 * 
 * @author Julian Frattini
 * 
 * Interface to a cause-effect-graph, which represent the formalized causality relation expressed by
 * a causal sentence
 */

public interface ICauseEffectGraph {

	/**
	 * Gets the cause portion of the graph in human-readable form
	 * @return The cause of the cause-effect-graph formatted into a string
	 */
	public String getCause();
	
	/**
	 * Gets the effect portion of the graph in human-readable form
	 * @return The effect of the cause-effect-graph formatted into a string
	 */
	public String getEffect();
	
	/**
	 * Returns the cause portion of the graph with some fixes to errors produced by the NLP tools
	 * @return The corrected cause of the cause-effect-graph formatted into a string
	 */
	public String getCausePrepared();
	
	/**
	 * Returns the effect portion of the graph with some fixes to errors produced by the NLP tools
	 * @return The corrected effect of the cause-effect-graph formatted into a string
	 */
	public String getEffectPrepared();
	
	/**
	 * Checks the equivalence between this cause-effect-graph and another
	 * @param other Other cause-effect-graph
	 * @return True, if the both cause-effect-graphs are equal
	 */
	public boolean equals(ICauseEffectGraph other);
	
	/**
	 * Formatting the cause-effect-graph into a human-readable form
	 * @return The cause-effect-graph as a string
	 */
	public String toString();
}
