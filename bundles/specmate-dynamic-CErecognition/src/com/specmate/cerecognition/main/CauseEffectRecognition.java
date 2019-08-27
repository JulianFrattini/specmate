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
import com.specmate.cerecognition.pattern.IStructure;
import com.specmate.cerecognition.pattern.Pattern;
import com.specmate.cerecognition.sentence.DKProSentenceAnnotator;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.trainer.JSONCausalityExampleReader;
import com.specmate.cerecognition.trainer.CauseEffectTrainer;
import com.specmate.cerecognition.trainer.ICausalityExampleReader;
import com.specmate.cerecognition.util.CELogger;
import com.specmate.cerecognition.util.Configuration;
import com.specmate.cerecognition.util.Globals;
import com.specmate.nlp.api.INLPService;

@Component
public class CauseEffectRecognition implements ICauseEffectRecognition{
	private DKProSentenceAnnotator annotator;
	
	private ArrayList<IPattern> patterns;
	private ICommandGenerator generator;
	private CauseEffectTrainer trainer;
	
	@Activate
	public void start() {
		trainer = new CauseEffectTrainer(this);
		if(Configuration.AUTO_TRAIN) {
			train();
			
			test();
		}
		//trainSpecial();
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
			System.out.println("Sentence '" + sentence + "' is accepted by pattern #" + patternFound.getIndex());
			return patternFound.generateCauseEffectGraph(sen);
		} else {
			return null;
		}
	}
	
	@Override
	public CauseEffectRecognitionResult train(String sentence, String cause, String effect) {
		CauseEffectRecognitionResult result = null;
		
		CELogger.log().info("Training the sentence \"" + sentence + "\"");
		if(!cause.isEmpty() && !effect.isEmpty()) {
			CELogger.log().info(" Causal relation: " + cause + " -> " + effect);
		}
		
		ISentence sen = annotator.createSentence(sentence);
		ICauseEffectGraph ceg = new SimpleCauseEffectGraph(cause, effect);
		CELogger.log().info(" " + sen.getRoot().toString(true, false));
		CELogger.log().info(" " + sen.getRoot().toString(false, true));

		CELogger.log().info("Searching for a pattern complying the sentence's structure");
		IPattern patternFound = null;
		for(IPattern pattern : patterns) {
			if(pattern.checkCompliance(sen)) {
				patternFound = pattern;
				break;
			}
		}
		if(patternFound == null) {
			CELogger.log().info("Found no pattern ... ");
			// no existing pattern found, creating a new one
			if(!cause.isEmpty() && !effect.isEmpty()) {
				CELogger.log().info("generating a new one!");
				
				ICauseEffectPattern newPattern = generator.generateCommandPatterns(sen, ceg);
				
				if(newPattern != null) {
					if(checkPatternCompliance(sen, newPattern, ceg)) {
						CELogger.log().info("The new pattern creates the desired expressions accordingly.");
						IPattern fullPattern = new Pattern(Globals.getInstance().getNewPatternCounter(),
								sen.generateStructure(),
								newPattern);
						fullPattern.addSentence(sen);
						
						patterns.add(fullPattern);
						result = CauseEffectRecognitionResult.CREATION_SUCCESSFUL;
					} else {
						CELogger.log().warn("The new cause effect pattern does not generate the correct CEG");
						CELogger.log().warn("  Structure: " + sen.getRoot().toString(true, true));
						CELogger.log().warn("  Generation: ");
						CELogger.log().warn("   - Cause: " + newPattern.getCommandString(true));
						CELogger.log().warn("   - Effect: " + newPattern.getCommandString(false));
						result = CauseEffectRecognitionResult.CREATION_FAILED;
					}
				} else {
					CELogger.log().warn("The cause effect generator did not generate a new pattern!");

					result = CauseEffectRecognitionResult.CREATION_FAILED;
				}
			} else {
				CELogger.log().info("The sentence was discarded correctly");
				result = CauseEffectRecognitionResult.DISCARDING_SUCCESSFUL;
			}
		} else {
			CELogger.log().info("Found a complying pattern :");
			CELogger.log().info(patternFound.toString());
			// existing pattern found
			if(patternFound.checkSentenceCompliance(sen, ceg)) {
				CELogger.log().info("The complying pattern does generate the correct expressions!");
				patternFound.addSentence(sen);
				
				result = CauseEffectRecognitionResult.RECOGNITION_SUCCESSFUL;
			} else {
				// the sentence erroneously complies with the pattern
				CELogger.log().warn("The found pattern does not generate the right CEG!");
				
				if(cause.isEmpty() && effect.isEmpty()) {
					// intruding sentence
					boolean deflectionSuccessful = patternFound.deflectIntruder(sen);
				
					if(deflectionSuccessful) {
						CELogger.log().info("The found pattern now deflects the sentence correctly");
						result = CauseEffectRecognitionResult.DEFLECTION_SUCCESSFUL;
					} else {
						CELogger.log().warn("The found pattern does is unable to deflect the intruder!");
						result = CauseEffectRecognitionResult.DEFLECTION_FAILED;
					}
				} else {
					// pattern has to be split in two
					IStructure otherStructure = patternFound.differentiateSimilar(sen);
					
					if(otherStructure != null) {
						ICauseEffectPattern newPattern = generator.generateCommandPatterns(sen, ceg);
						
						if(newPattern != null) {
							if(checkPatternCompliance(sen, newPattern, ceg)) {
								IPattern fullPattern = new Pattern(Globals.getInstance().getNewPatternCounter(),
										otherStructure,
										newPattern);
								fullPattern.addSentence(sen);
								
								CELogger.log().info("Splitting successful! New pattern #" + fullPattern.getIndex() + " now covers the differentiating case!");
								
								
								patterns.add(fullPattern);
								result = CauseEffectRecognitionResult.SPLITTING_SUCCESSFUL;
							} else {
								CELogger.log().warn("The new cause effect pattern does not generate the correct CEG");
								CELogger.log().warn("  Structure: " + sen.getRoot().toString(true, true));
								CELogger.log().warn("  Generation: ");
								CELogger.log().warn("   - Cause: " + newPattern.getCommandString(true));
								CELogger.log().warn("   - Effect: " + newPattern.getCommandString(false));
								result = CauseEffectRecognitionResult.CREATION_FAILED;
							}
						} else {
							CELogger.log().warn("The cause effect generator did not generate a new pattern!");

							result = CauseEffectRecognitionResult.CREATION_FAILED;
						}
					} else {
						CELogger.log().warn("The found pattern could not be split accordingly.");
					
						result = CauseEffectRecognitionResult.SPLITTING_FAILED;
					}
				}
			}
		}
		
		CELogger.log().info("---------------------------------");
		return result;
	}
	
	public boolean checkPatternCompliance(ISentence sentence, ICauseEffectPattern pattern, ICauseEffectGraph ceg) {
		ICauseEffectGraph generated = pattern.generateGraphFromSentence(sentence);
		
		CELogger.log().info("Checking pattern compliance between CEG's of " + sentence.toString());
		CELogger.log().info("  - given: '" + ceg.getCause() + "' -> '" + ceg.getEffect() + "'");
		CELogger.log().info("  - generated: '" + generated.getCause() + "' -> '" + generated.getEffect() + "'");
		
		return generated.equals(ceg);
	}
	
	public void train() {
		for(String file : Configuration.TRAINING_FILES) {
			ICausalityExampleReader reader = new JSONCausalityExampleReader();
			reader.initialize(file);
			trainer.train(reader);
		}
		trainer.printStatistics();
	}
	
	public void trainSpecial() {
		ICausalityExampleReader reader = new JSONCausalityExampleReader();
		reader.initialize(Configuration.TRAINING_FILE_SPECIAL);
		trainer.train(reader);
		trainer.printStatistics();
	}
	
	public void test() {
		trainer.resetStatistics();
		for(String file : Configuration.TESTING_FILES) {
			ICausalityExampleReader reader = new JSONCausalityExampleReader();
			reader.initialize(file);
			trainer.train(reader);
		}
		trainer.printStatistics();
	}
	
	@Reference
	void setNlptagging(INLPService nlp) {
		annotator = new DKProSentenceAnnotator(nlp);
	}
}
