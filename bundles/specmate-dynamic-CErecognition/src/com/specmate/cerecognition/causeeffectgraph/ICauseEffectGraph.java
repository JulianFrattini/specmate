package com.specmate.cerecognition.causeeffectgraph;

/**
 * 
 * @author julian
 * Interface to a cause-effect-graph
 */

public interface ICauseEffectGraph {
	// returns the cause/effect portion of the graph in human-readable form
	public String getCause();
	public String getEffect();
	// returns the cause/effect portion of the graph with some fixes to errors produced by the NLP tools
	// use this to output the actual cause/effect portion
	public String getCausePrepared();
	public String getEffectPrepared();
	
	// main purpose of all implementing classes in order to check for equivalence
	public boolean equals(ICauseEffectGraph other);
	
	public String toString();
}
