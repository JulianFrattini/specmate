package com.specmate.cerecognition.main;

import java.util.ArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.specmate.cerecognition.api.ICauseEffectTrainer;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.causeeffectgraph.SimpleCauseEffectGraph;
import com.specmate.cerecognition.genetics.ICommandGenerator;
import com.specmate.cerecognition.genetics.SimpleCommandGenerator;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.pattern.Pattern;
import com.specmate.cerecognition.sentence.DKProSentenceAnnotator;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.util.Globals;
import com.specmate.nlp.api.INLPService;

@Component
public class Main implements ICauseEffectTrainer{
	private DKProSentenceAnnotator annotator;
	
	private ArrayList<IPattern> patterns;
	private ICommandGenerator generator;
	
	@Activate
	public void start() {
		train("It rains because of the humidity.", "humidity", "rains");
		train("It is warm because of the sun.", "sun", "warm");
	}
	
	public Main() {
		patterns = new ArrayList<IPattern>();
		generator = new SimpleCommandGenerator();
	}
	
	@Override
	public ArrayList<IPattern> getPatterns() {
		return patterns;
	}
	
	@Override
	public ICauseEffectGraph getCEG(String sentence) {
		ISentence sen = annotator.createSentence(sentence);
		
		IPattern patternFound = null;
		for(IPattern pattern : patterns) {
			if(pattern.checkCompliance(sen)) {
				patternFound = pattern;
				break;
			}
		}
		
		if(patternFound != null) {
			return patternFound.generateCauseEffectGraph(sen);
		} else {
			return null;
		}
	}
	
	@Override
	public void train(String sentence, String cause, String effect) {
		ISentence sen = annotator.createSentence(sentence);
		ICauseEffectGraph ceg = new SimpleCauseEffectGraph(cause, effect);
		
		IPattern patternFound = null;
		for(IPattern pattern : patterns) {
			if(pattern.checkCompliance(sen)) {
				patternFound = pattern;
				break;
			}
		}
		
		if(patternFound == null) {
			// no existing pattern found, creating a new one
			if(!cause.isEmpty() && !effect.isEmpty()) {
				ICauseEffectPattern newPattern = generator.generateCommandPatterns(sen, ceg);
				
				if(newPattern != null) {
					if(checkPatternCompliance(sen, newPattern, ceg)) {
						IPattern fullPattern = new Pattern(Globals.getInstance().getNewPatternCounter(),
								sen.generateStructure(),
								newPattern);
						fullPattern.addSentence(sen);
						
						patterns.add(fullPattern);
					} else {
						System.out.println("ERROR: the new cause effect pattern does not generate the correct CEG");
					}
				} else {
					System.out.println("ERROR: the cause effect generator did not generate a new pattern!");
				}
			}
		} else {
			// existing pattern found
			if(patternFound.checkSentenceCompliance(sen, ceg)) {
				patternFound.addSentence(sen);
			} else {
				// the sentence erroneously complies with the pattern
				System.out.println("ERROR: the found pattern does not generate the right CEG");
				// TODO improve pattern strictness
			}
		}
	}
	
	public boolean checkPatternCompliance(ISentence sentence, ICauseEffectPattern pattern, ICauseEffectGraph ceg) {
		ICauseEffectGraph generated = pattern.generateGraphFromSentence(sentence);
		return generated.equals(ceg);
	}
	
	@Reference
	void setNlptagging(INLPService nlp) {
		annotator = new DKProSentenceAnnotator(nlp);
	}
}
