package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.util.CELogger;

public class SimpleCauseEffectGraph implements ICauseEffectGraph {

	private String cause;
	private String effect;
	
	public SimpleCauseEffectGraph(String cause, String effect) {
		this.cause = cause;
		this.effect = effect;
	}
	
	public String getCause() {
		return cause;
	}

	public String getEffect() {
		return effect;
	}

	@Override
	public boolean equals(ICauseEffectGraph other) {
		if(!cause.equals(other.getCause())) {
			CELogger.log().warn("ERROR: The causes do not align:");
			CELogger.log().warn("\t- " + cause);
			CELogger.log().warn("\t- " + other.getCause());
			return false;
		}
		if(!effect.equals(other.getEffect())) {
			CELogger.log().warn("ERROR: The effects do not align:");
			CELogger.log().warn("\t- " + effect);
			CELogger.log().warn("\t- " + other.getEffect());
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return cause + " -> " + effect;
	}
}
