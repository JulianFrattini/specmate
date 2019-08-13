package com.specmate.cerecognition.pattern;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.sentence.ISentence;

public interface IPattern {
	public int getIndex();
	public IStructure getStructure();
	public ICauseEffectPattern getGenerationPattern();
	
	public boolean checkCompliance(ISentence candidate);
	
	public void addSentence(ISentence candidate);
	
	public ICauseEffectGraph generateCauseEffectGraph(ISentence sentence);
	public boolean checkSentenceCompliance(ISentence candidate, ICauseEffectGraph ceg);
	
	// case: a non-causal sentence has the same sentence structure as defined in this pattern and is currently recognized
	public boolean deflectIntruder(ISentence sentence);
	// case: a causal sentence with the same sentence structure requires a differentiation
	public IStructure differentiateSimilar(ISentence sentence);
	
	public String toString();
}
