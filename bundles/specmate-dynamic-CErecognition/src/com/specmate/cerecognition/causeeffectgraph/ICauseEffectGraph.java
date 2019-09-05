package com.specmate.cerecognition.causeeffectgraph;

public interface ICauseEffectGraph {
	public String getCause();
	public String getEffect();
	public String getCausePrepared();
	public String getEffectPrepared();
	public boolean equals(ICauseEffectGraph other);
	public String toString();
}
