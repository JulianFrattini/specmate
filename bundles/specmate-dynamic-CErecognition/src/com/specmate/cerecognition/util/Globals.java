package com.specmate.cerecognition.util;

/**
 * 
 * @author Julian Frattini
 * 
 * Class for global operations like distribution of sentence indices
 */

public class Globals {

	private static Globals instance;
	
	public static Globals getInstance() {
		if(instance == null) {
			instance = new Globals();
		}
		return instance;
	}
	
	/**
	 * index of the annotated sentences
	 */
	private int sentenceCounter;
	
	private int patternCounter;
	
	private Globals() {
		sentenceCounter = 0;
		patternCounter = 0;
	}
	
	public int getCurrentSentenceCounter() {
		return sentenceCounter;
	}
	
	public int getNewSentenceCounter() {
		int current = sentenceCounter;
		sentenceCounter++;
		return current;
	}
	
	public int getNewPatternCounter() {
		int current = patternCounter;
		patternCounter++;
		return current;
	}
}
