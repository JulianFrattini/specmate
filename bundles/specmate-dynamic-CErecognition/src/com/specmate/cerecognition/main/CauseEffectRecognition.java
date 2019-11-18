package com.specmate.cerecognition.main;

import java.util.ArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.specmate.cerecognition.api.ICauseEffectRecognition;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.genetics.ICommandGenerator;
import com.specmate.cerecognition.genetics.SimpleCommandGenerator;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.pattern.IStructure;
import com.specmate.cerecognition.pattern.Pattern;
import com.specmate.cerecognition.sentence.DKProSentenceAnnotator;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.trainer.JSONCausalityExampleReader;
import com.specmate.cerecognition.trainer.CausalityExample;
import com.specmate.cerecognition.trainer.CauseEffectTester;
import com.specmate.cerecognition.trainer.CauseEffectTrainer;
import com.specmate.cerecognition.trainer.ExampleSet;
import com.specmate.cerecognition.trainer.ICausalityExampleReader;
import com.specmate.cerecognition.util.CELogger;
import com.specmate.cerecognition.util.Configuration;
import com.specmate.cerecognition.util.Globals;
import com.specmate.nlp.api.INLPService;

/**
 * 
 * @author Julian Frattini
 * 
 * Main component dealing with input
 */

@Component
public class CauseEffectRecognition implements ICauseEffectRecognition{
	private DKProSentenceAnnotator annotator;
	
	private ArrayList<IPattern> patterns;
	private ICommandGenerator generator;
	private CauseEffectTrainer trainer;
	private CauseEffectTester tester;
	
	@Activate
	public void start() {
		// initialize statistics
		trainer = new CauseEffectTrainer(this);
		tester = new CauseEffectTester(this);
		
		if(Configuration.AUTO_TRAIN) {
			// train with all (1 = 100%) of the training data sets
			train(1);
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
	
	/**
	 * Attempt to generate a cause-effect-graph from a given sentence. This will work when 
	 * the system already knows a causality pattern associated with the sentence's 
	 * grammatical structure
	 * @param sentence The sentence under test
	 * @return A cause-effect-graph, if the sentence is identified by a causality pattern
	 */
	@Override
	public ICauseEffectGraph getCEG(String sentence) {
		// preprocess the sentence with NLP techniques
		ISentence sen = annotator.createSentence(sentence);
		
		// try to find a causality pattern in the database of patterns
		IPattern patternFound = null;
		for(IPattern pattern : patterns) {
			if(pattern.checkCompliance(sen)) {
				patternFound = pattern;
				break;
			}
		}
		
		if(patternFound != null) {
			// pattern found: attempt to generate a cause-effect-graph from the sentence with the given causality pattern
			CELogger.log().info("Sentence '" + sentence + "' is accepted by pattern #" + patternFound.getIndex());
			ICauseEffectGraph ceg = patternFound.generateCauseEffectGraph(sen);
			
			if(ceg != null) {
				return ceg;
			} else {
				// unable to generate a cause-effect, possibly because the sentence was compliant to the pattern due to lack of training
				CELogger.log().error("But the patterns generation algorithm does not create a cause-effect-graph!");
				CELogger.log().error("This sentence probably has to be deflected.");
				return null;
			}
		} else {
			// no pattern found: the sentence is assumed to be non-causal
			return null;
		}
	}
	
	/**
	 * Provide the system with a causality example containing a sentence and its causality portion.
	 * The system will then attempt to generate a new causality pattern, which in turn will be able to
	 * recognize grammatically similar sentences and extract the cause- and effect-expressions from it
	 * @param sentence CausalityExample containing a sentence and its causality portion
	 * @return CauseEffectRecognitionResult evaluating the training process
	 */
	@Override
	public CauseEffectRecognitionResult train(CausalityExample sentence) {
		CauseEffectRecognitionResult result = null;
		
		CELogger.log().info("Training the sentence \"" + sentence.getSentence() + "\"");
		if(sentence.isCausal()) {
			CELogger.log().info(" Causal relation: " + sentence.getCause() + " -> " + sentence.getEffect());
		}
		
		// check if the CausalityExample is actually valid: the cause- and effect-expression must be a valid substring of the sentence
		if(!isExampleValid(sentence)) {
			result = CauseEffectRecognitionResult.CREATION_IMPOSSIBLE;
			CELogger.log().warn("The sentence cannot be analyzed, because cause and/or effect are not substrings of the sentence.");
			return result;
		}
		
		ISentence sen = annotator.createSentence(sentence.getSentence());
		ICauseEffectGraph ceg = sentence.getCEG();
		CELogger.log().info(" " + sen.getRoot().structureToString());
		CELogger.log().info(" " + sen.getRoot().toString(true, false));
		CELogger.log().info(" " + sen.getRoot().toString(false, true));

		// verify if there already exists a pattern complying to the sentence's structure in the database
		CELogger.log().info("Searching for a pattern complying to the sentence's structure");
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
			if(sentence.isCausal()) {
				CELogger.log().info("generating a new one!");
				
				ICauseEffectPattern newPattern = generator.generateCommandPatterns(sen, ceg);
				
				if(newPattern != null) {
					// check if the new pattern does generate the desired cause-effect-graph of the CausalityExample
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
					// creation failed due to an unknown reason
					CELogger.log().warn("The cause effect generator did not generate a new pattern!");

					result = CauseEffectRecognitionResult.CREATION_FAILED;
				}
			} else {
				// non-causal sentence correctly discarded
				CELogger.log().info("The sentence was discarded correctly");
				result = CauseEffectRecognitionResult.DISCARDING_SUCCESSFUL;
			}
		} else {
			// existing pattern found
			CELogger.log().info("Found a complying pattern :");
			CELogger.log().info(patternFound.toString());
			
			if(sentence.isCausal()) {
				// check compliance
				if(patternFound.isAffiliated(sen, ceg)) {
					CELogger.log().info("The complying pattern does generate the correct expressions!");
					patternFound.addSentence(sen);
					
					result = CauseEffectRecognitionResult.RECOGNITION_SUCCESSFUL;
				} else {
					// the sentence erroneously complies with the pattern
					CELogger.log().warn("The found pattern does not generate the right CEG!");
					
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
			} else {
				// intruding sentence, attempt deflection by specifying the pattern
				boolean deflectionSuccessful = patternFound.deflectIntruder(sen);
			
				if(deflectionSuccessful) {
					CELogger.log().info("The found pattern now deflects the sentence correctly");
					result = CauseEffectRecognitionResult.DEFLECTION_SUCCESSFUL;
				} else {
					CELogger.log().warn("The found pattern does is unable to deflect the intruder!");
					result = CauseEffectRecognitionResult.DEFLECTION_FAILED;
				}
			}
		}
		
		CELogger.log().info("---------------------------------");
		return result;
	}
	
	/**
	 * Checks, whether the cause- and effect-expression of a causal example are valid substrings of the sentence
	 * @param example Causal or non-causal sentence
	 * @return True, if the example is possibly analyzable
	 */
	public boolean isExampleValid(CausalityExample example) {
		if(example.isCausal()) {
			if(example.getSentence().contains(example.getCause()) &&
					example.getSentence().contains(example.getEffect())) {
				// an example is only valid, if the cause- and effect-expression is a substring of the sentence
				return true;
			} else {
				// there is no possibility to extract the expressions
				return false;
			}
		} else {
			// non-causal sentences just have to be discarded
			return true;
		}
	}
	
	/**
	 * Checks, whether a given sentence is compliant to a pattern and generates the desired cause-effect-graph
	 * @param sentence Sentence under test
	 * @param pattern Possibly compliant pattern
	 * @param ceg Cause-effect-graph that should result when applying the patterns extraction algorithms to the sentence
	 * @return True, if the sentence is compliant to the pattern
	 */
	public boolean checkPatternCompliance(ISentence sentence, ICauseEffectPattern pattern, ICauseEffectGraph ceg) {
		ICauseEffectGraph generated = pattern.generateGraphFromSentence(sentence);
		
		return generated.equals(ceg);
	}
	
	/**
	 * Auxiliary method for performing the research study in the Master's Thesis of Julian Frattini
	 * @param RQ Index of the research question
	 * @param study Sub-index of the research question
	 * @param iteration Sub-sub-index of the research question
	 */
	public void performStudy(int RQ, int study, int iteration) {
		if(RQ == 1) {
			if(study == 1) {
				train(1);
			} else if(study == 2) {
				if(iteration == 1) {
					trainAndTestRandomPortion(0.2);
				} else if(iteration == 2) {
					trainAndTestRandomPortion(0.1);
				}
			} else if (study == 3) {
				switch(iteration) {
					case 1: trainAndTestRandomPortion(0.2, 0.2); break;
					case 2: trainAndTestRandomPortion(0.2, 0.4); break;
					case 3: trainAndTestRandomPortion(0.2, 0.6); break;
					case 4: trainAndTestRandomPortion(0.2, 0.8); break;
					case 5: trainAndTestRandomPortion(0.2, 1); break;
					default: trainAndTestRandomPortion(0.2, 1); break;
				}
			}
		} else if(RQ == 2) {
			if(study == 1) {
				train(1);
				test(true);
			} else if(study == 2) {
				train(1);
				test(false);
			} else if(study == 3) {
				switch(iteration) {
					case 1: train(0.2); break;
					case 2: train(0.4); break;
					case 3: train(0.6); break;
					case 4: train(0.8); break;
					case 5: train(1); break;
					default: train(1); break;
				}
				test(false);
			}
		}
	}
	
	/**
	 * Train the database of pattern with a set of training files
	 * @param portion Gradient (between 0 and 1) of how much percent of the data shall be used to train the system, where train(1) uses all data entries
	 */
	public void train(double portion) {
		ExampleSet trainingData = new ExampleSet();
		for(String file : Configuration.TRAINING_FILES) {
			ICausalityExampleReader reader = new JSONCausalityExampleReader();
			reader.initialize(file);
			trainingData.addSet(reader.readExamples());
		}
		trainer.train(trainingData.getPortion(portion));
		trainer.printStatistics();
	}
	
	/**
	 * Automatically testing all provided test data sets
	 * @param original True, if the originally provided data shall be used, false if the purified (partially corrected) data shall be used
	 */
	public void test(boolean original) {
		String path = original ? Configuration.TESTING_PATH_ORIGINAL_ : Configuration.TESTING_PATH_PURE;
		
		tester.resetStatistics();
		ExampleSet testingData = new ExampleSet();
		for(String file : Configuration.TESTING_FILES) {
			ICausalityExampleReader reader = new JSONCausalityExampleReader();
			reader.initialize(path+file);
			testingData.addSet(reader.readExamples());
		}
		tester.test(testingData);
		tester.printStatistics();
	}
	
	/**
	 * Splits the training data into two parts, trains with one and tests with the other
	 * @param segment Percentage [0, 1.0] of testing data
	 */
	public void trainAndTestRandomPortion(double segment) {
		// setup the training
		ExampleSet allData = new ExampleSet();
		for(String file : Configuration.TRAINING_FILES) {
			ICausalityExampleReader reader = new JSONCausalityExampleReader();
			reader.initialize(file);
			allData.addSet(reader.readExamples());
		}
		
		// split the set
		ExampleSet trainingData = new ExampleSet();
		ExampleSet testingData = new ExampleSet();
		allData.split(segment, testingData, trainingData);
		
		// perform training
		trainer.train(trainingData);
		trainer.printStatistics();
		
		// perform testing
		tester.test(testingData);
		tester.printStatistics();
	}
	
	/**
	 * Trains the system and with a portion of the training data
	 * @param segment Percentage (given as a value from 0 to 1) of the data entries, which are used as the testing data
	 * @param evolution Percentage (given as a value from 0 to 1) of the data entries, which are used at all
	 */
	public void trainAndTestRandomPortion(double segment, double evolution) {
		// setup the training
		ExampleSet allData = new ExampleSet();
		for(String file : Configuration.TRAINING_FILES) {
			ICausalityExampleReader reader = new JSONCausalityExampleReader();
			reader.initialize(file);
			allData.addSet(reader.readExamples());
		}
		
		allData.setSet(allData.getPortion(evolution));
		
		// split the set
		ExampleSet trainingData = new ExampleSet();
		ExampleSet testingData = new ExampleSet();
		allData.split(segment, testingData, trainingData);
		
		// perform training
		trainer.train(trainingData);
		trainer.printStatistics();
		
		// perform testing
		tester.test(testingData);
		tester.printStatistics();
	}
	
	@Reference
	void setNlptagging(INLPService nlp) {
		annotator = new DKProSentenceAnnotator(nlp);
	}
}
