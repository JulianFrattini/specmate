package com.specmate.cerecognition.util;

public class Globals {

	private static Globals instance;
	
	public static Globals getInstance() {
		if(instance == null) {
			instance = new Globals();
		}
		return instance;
	}
	
	// index of the annotated sentences
	private int sentenceCounter;
	
	private Globals() {
		sentenceCounter = 0;
	}
	
	public int getCurrentSentenceCounter() {
		return sentenceCounter;
	}
	
	public int getNewSentenceCounter() {
		int current = sentenceCounter;
		sentenceCounter++;
		return current;
	}
}
