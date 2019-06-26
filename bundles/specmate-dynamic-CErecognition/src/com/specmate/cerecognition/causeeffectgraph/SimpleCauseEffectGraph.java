package com.specmate.cerecognition.causeeffectgraph;

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
		return (cause.equals(other.getCause()) && effect.equals(other.getEffect()));
	}
	
	@Override
	public String toString() {
		return cause + " -> " + effect;
	}
}
