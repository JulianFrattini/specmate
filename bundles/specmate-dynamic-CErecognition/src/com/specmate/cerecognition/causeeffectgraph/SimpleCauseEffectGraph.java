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
		if(!cause.equals(other.getCause())) {
			System.out.println("ERROR: The causes do not align:");
			System.out.println("\t" + cause);
			System.out.println("\t" + other.getCause());
			return false;
		}
		if(!effect.equals(other.getEffect())) {
			System.out.println("ERROR: The effects do not align:");
			System.out.println("\t" + effect);
			System.out.println("\t" + other.getEffect());
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return cause + " -> " + effect;
	}
}
