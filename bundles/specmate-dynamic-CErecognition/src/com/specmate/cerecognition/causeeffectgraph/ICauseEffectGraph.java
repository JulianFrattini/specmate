package com.specmate.cerecognition.causeeffectgraph;

public interface ICauseEffectGraph {
	public String getCause();
	public String getEffect();
	public boolean equals(ICauseEffectGraph other);
	public String toString();
}
