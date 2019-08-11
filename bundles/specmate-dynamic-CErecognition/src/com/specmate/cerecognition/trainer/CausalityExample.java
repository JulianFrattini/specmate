package com.specmate.cerecognition.trainer;

public class CausalityExample {
	private String sentence;
	private String cause;
	private String effect;
	
	public CausalityExample(String sentence) {
		super();
		this.sentence = sentence;
		cause = "";
		effect = "";
	}
	
	public CausalityExample(String sentence, String cause, String effect) {
		super();
		this.sentence = sentence;
		this.cause = cause;
		this.effect = effect;
	}
	
	public boolean isCausal() {
		return !cause.isEmpty() && !effect.isEmpty();
	}

	public String getSentence() {
		return sentence;
	}

	public String getCause() {
		return cause;
	}

	public String getEffect() {
		return effect;
	}
	
	@Override
	public String toString() {
		if(isCausal()) {
			return sentence + " (" + cause + " -> " + effect + ")";
		} else {
			return sentence;
		}
	}
}
