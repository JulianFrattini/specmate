package com.specmate.cerecognition.pattern;

import java.util.ArrayList;
import java.util.Collection;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.sentence.ISentence;

public class Pattern implements IPattern {
	
	private int index;

	/* 
	 * structure of the sentence: constituency tree of the sentence 
	 * stripped of the leaf nodes containing only words
	 */
	private IStructure sentenceStructure; 
	
	/*
	 * genetic algorithm extracting the cause and effect from the 
	 * sentence of a specific structure
	 */
	private ICauseEffectPattern cePattern;
	
	/*
	 * Collection of all sentences complying the sentenceStructure
	 */
	private Collection<ISentence> accepted;
	
	public Pattern(int index, IStructure sentenceStructure, ICauseEffectPattern cePattern) {
		this.index = index;
		this.sentenceStructure = sentenceStructure;
		this.cePattern = cePattern;
		
		accepted = new ArrayList<ISentence>();
	}
	
	public int getIndex() {
		return index;
	}
	
	/**
	 * Checks, whether a new sentence complies with the sentence structure of this pattern
	 * @param candidate Sentence which has to be checked
	 * @return True, if the sentence's structure equals the sentence structure of this pattern
	 */
	public boolean checkCompliance(ISentence candidate) {
		//IStructure candidateStructure = candidate.generateStructure();
		if(sentenceStructure.compliedBy(candidate.getRoot())) {
			return true;
		}
		return false;
	}
	
	public void addSentence(ISentence candidate) {
		accepted.add(candidate);
	}
	
	public ICauseEffectGraph generateCauseEffectGraph(ISentence sentence) {
		return cePattern.generateGraphFromSentence(sentence);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Pattern #" + index + ":\n");
		sb.append("Structure: " + sentenceStructure.toString() + "\n");
		for(ISentence sentence : accepted) {
			sb.append(sentence.toString() + "\n");
		}
		
		return sb.toString();
	}
}
