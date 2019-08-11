package com.specmate.cerecognition.main;

import java.util.ArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.causeeffectgraph.SimpleCauseEffectGraph;
import com.specmate.cerecognition.genetics.ICommandGenerator;
import com.specmate.cerecognition.genetics.SimpleCommandGenerator;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.pattern.Pattern;
import com.specmate.cerecognition.sentence.DKProSentenceAnnotator;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.trainer.JSONCausalityExampleReader;
import com.specmate.cerecognition.trainer.CauseEffectTrainer;
import com.specmate.cerecognition.trainer.ICausalityExampleReader;
import com.specmate.cerecognition.util.Globals;
import com.specmate.nlp.api.INLPService;

@Component
public class CauseEffectRecognition implements ICauseEffectRecognition{
	private DKProSentenceAnnotator annotator;
	
	private ArrayList<IPattern> patterns;
	private ICommandGenerator generator;
	
	@Activate
	public void start() {
		if(Configuration.AUTO_TRAIN) {
			train();
		}
	}
	
	public CauseEffectRecognition() {
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
	public IPattern train(String sentence, String cause, String effect) {
		System.out.println("Training the sentence \"" + sentence + "\"");
		if(!cause.isEmpty() && !effect.isEmpty()) {
			System.out.println(" " + cause + " -> " + effect);
		}
		
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
			System.out.print("Found no pattern ... ");
			// no existing pattern found, creating a new one
			if(!cause.isEmpty() && !effect.isEmpty()) {
				System.out.println("generating a new one!");
				
				ICauseEffectPattern newPattern = generator.generateCommandPatterns(sen, ceg);
				
				if(newPattern != null) {
					if(checkPatternCompliance(sen, newPattern, ceg)) {
						System.out.println("All correct!");
						IPattern fullPattern = new Pattern(Globals.getInstance().getNewPatternCounter(),
								sen.generateStructure(),
								newPattern);
						fullPattern.addSentence(sen);
						
						patterns.add(fullPattern);
						System.out.println("------------------------------");
						return fullPattern;
					} else {
						System.out.println("ERROR: the new cause effect pattern does not generate the correct CEG");
						System.out.println("  Structure: " + sen.getRoot().toString(true, true));
						System.out.println("  Generation: ");
						System.out.println("   - Cause: " + newPattern.getCommandString(true));
						System.out.println("   - Effect: " + newPattern.getCommandString(false));
						System.out.println("------------------------------");
						return null;
					}
				} else {
					System.out.println("ERROR: the cause effect generator did not generate a new pattern!");

					System.out.println("------------------------------");
					return null;
				}
			} else {
				System.out.println("correctly!");
				System.out.println("------------------------------");
			}
		} else {
			System.out.println("Found a complying pattern :");
			System.out.println(patternFound.toString());
			// existing pattern found
			if(patternFound.checkSentenceCompliance(sen, ceg)) {
				System.out.println("Pattern found and correctly complied!");
				System.out.println("------------------------------");
				patternFound.addSentence(sen);
				return patternFound;
			} else {
				// the sentence erroneously complies with the pattern
				System.out.println("ERROR: the found pattern does not generate the right CEG");
				System.out.println("------------------------------");
				// TODO improve pattern strictness
				
				return null;
			}
		}
		
		return null;
	}
	
	public boolean checkPatternCompliance(ISentence sentence, ICauseEffectPattern pattern, ICauseEffectGraph ceg) {
		ICauseEffectGraph generated = pattern.generateGraphFromSentence(sentence);
		
		System.out.println("Checking pattern compliance between CEG's of " + sentence.toString());
		System.out.println("  - given: '" + ceg.getCause() + "' -> '" + ceg.getEffect() + "'");
		System.out.println("  - generated: '" + generated.getCause() + "' -> '" + generated.getEffect() + "'");
		
		return generated.equals(ceg);
	}
	
	public void train() {
		ICausalityExampleReader reader = new JSONCausalityExampleReader();
		reader.initialize(Configuration.TRAINING_FILE);
		CauseEffectTrainer trainer = new CauseEffectTrainer(reader);
		trainer.train(this);
	}
	
	@Reference
	void setNlptagging(INLPService nlp) {
		annotator = new DKProSentenceAnnotator(nlp);
	}
}
