package com.specmate.cerecognition.pattern;

import java.util.ArrayList;

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
}
