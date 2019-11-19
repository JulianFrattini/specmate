package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author Julian Frattini
 * Simple version of a cause-effect-graph only containing one cause-phrase and one effect-phrase.
 * More complex relations like the resolution of conjunctions, negations, or else, is neglected here
 */

public class SimpleCauseEffectGraph implements ICauseEffectGraph {

	private String cause;
	private String effect;
	
	public SimpleCauseEffectGraph(String cause, String effect) {
		this.cause = cause;
		this.effect = effect;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCause() {
		return cause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEffect() {
		return effect;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCausePrepared() {
		return prepareExpression(cause);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEffectPrepared() {
		return prepareExpression(effect);
	}
	
	/**
	 * Counters some NLP-specific processing faults like splitting n't from negated words
	 * @param expression Expression to be corrected
	 * @return cause/effect expression corrected of NLP-processing faults
	 */
	private String prepareExpression(String expression) {
		String result = expression;
		
		if(result.contains("n't")) {
			System.out.println("Expression '" + result + "' contained n't");
			result = result.replaceAll("n't", " n't");
		}
		if(result.contains("'s")) {
			System.out.println("Expression '" + result + "' contained 's");
			result = result.replaceAll("'s", " 's");
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(ICauseEffectGraph other) {
		CELogger.log().info("Checking compliance between CEG's:");
		CELogger.log().info("  - generated: '" + getCause() + "' -> '" + getEffect() + "'");
		CELogger.log().info("  - given: '" + other.getCausePrepared() + "' -> '" + other.getCausePrepared() + "'");
		
		
		if(!getCause().equals(other.getCausePrepared())) {
			CELogger.log().warn("ERROR: The causes do not align:");
			CELogger.log().warn("\t- " + getCause());
			CELogger.log().warn("\t- " + other.getCausePrepared());
			return false;
		}
		if(!getEffect().equals(other.getEffectPrepared())) {
			CELogger.log().warn("ERROR: The effects do not align:");
			CELogger.log().warn("\t- " + getEffect());
			CELogger.log().warn("\t- " + other.getEffectPrepared());
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return cause + " -> " + effect;
	}
}
