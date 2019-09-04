package com.specmate.cerecognition.trainer;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.SimpleCauseEffectGraph;

public class CausalityExample {
	private String sentence;
	private ICauseEffectGraph ceg;
	
	public CausalityExample(String sentence) {
		super();
		this.sentence = sentence;
		ceg = null;
	}
	
	public CausalityExample(String sentence, String cause, String effect) {
		super();
		this.sentence = sentence;
		this.ceg = new SimpleCauseEffectGraph(cause, effect);
	}
	
	public boolean isCausal() {
		return ceg != null;
	}

	public String getSentence() {
		return sentence;
	}

	public String getCause() {
		return ceg.getCause();
	}

	public String getEffect() {
		return ceg.getEffect();
	}
	
	public ICauseEffectGraph getCEG() {
		return ceg;
	}
	
	@Override
	public String toString() {
		if(isCausal()) {
			return sentence + " (" + getCause() + " -> " + getEffect() + ")";
		} else {
			return sentence;
		}
	}
}
