package com.specmate.cerecognition.genetics;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.causeeffectgraph.SimpleCauseEffectGenerator;
import com.specmate.cerecognition.causeeffectgraph.SimpleCauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.SimpleCauseEffectPattern;
import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.sentence.Leaf;
import com.specmate.cerecognition.util.CELogger;
import com.specmate.cerecognition.util.Utils;

public class SimpleCommandGenerator implements ICommandGenerator {

	public SimpleCommandGenerator() {
		
	}
	
	@Override
	public ICauseEffectPattern generateCommandPatterns(ISentence sentence, ICauseEffectGraph graph) {
		SimpleCauseEffectGraph simpleCE = (SimpleCauseEffectGraph) graph;		
		
		SimpleCauseEffectGenerator causeGenerator = generateCommandPattern(sentence, simpleCE.getCause());
		SimpleCauseEffectGenerator effectGenerator = generateCommandPattern(sentence, simpleCE.getEffect());

		if(causeGenerator == null || effectGenerator == null) {
			return null;
		} else {
			return new SimpleCauseEffectPattern(causeGenerator, effectGenerator);
		}
	}

	/**
	 * Generates a Generator, which extracts the given ce-String from the sentence
	 * @param sentence The sentence under test
	 * @param ce The cause- or effect-expression
	 * @param index The begin index of the word (optionally used when a sentence contains multiple identical leafs (same text and type) in order to differentiate them)
	 * @return
	 */
	private SimpleCauseEffectGenerator generateCommandPattern(ISentence sentence, String ce) {
		SimpleCommand command = null;
		
		ArrayList<Fragment> eligibleByExpression = new ArrayList<Fragment>();
		sentence.getRoot().select(false, ce, eligibleByExpression);
		
		if(eligibleByExpression.size() == 1) {
			// exactly one node in the constituency tree matches the given text
			Fragment eligible = eligibleByExpression.get(0);
			String eligibleType = eligible.getTag();
			
			// to generalize the detection, this node shall be referenced with its tag
			// in order for this to work it has to be ensured, that the selection by type is unique
			ArrayList<Fragment> eligibleByType = new ArrayList<Fragment>();
			sentence.getRoot().getBy(true, eligibleType, eligibleByType);
			
			if(eligibleByType.size() == 1) {
				// the search for the type is unique and a selector can be used
				command = new CommandSelect(true, eligibleType); 
			} else {
				// the search for the type is not unique yet, so the sentence has to be split up first
				/*SimpleCommand splitter = splitUntilSearchIsUnique(
						sentence.getRoot(),
						eligibleType,
						eligible.getCoveredText());
				command = addCommand(command, splitter);
				command = addCommand(command, new CommandSelect(true, eligibleType));*/
				
				command = getUniqueSelector(sentence.getRoot(), eligible);
			}
		} else if(eligibleByExpression.size() > 1) {
			// TODO more than one node eligible when searching for the given expression
			CELogger.log().info("More than one node eligible when searching for the expression '" + ce + "'");
			CELogger.log().info("  Sentence under test: " + sentence.toString());
			for(Fragment eligible : eligibleByExpression) {
				CELogger.log().info("  - " + eligible.toString());
			}
		} else if(eligibleByExpression.size() == 0) {
			// no node in the tree of constituents exclusively contains the full expression
			// horizontal approach: find one governing leaf among the nodes that make up the expression
			
			// clean attempt via leafs
			command = generateHorizontalSelectionByLeafs(sentence.getRoot(), ce);
			
			if(command == null) {
				// fallback attempt via words
				command = generateHorizontalSelectionByWords(sentence.getRoot(), ce);
			
				if(command == null) {
					CELogger.log().warn("Impossible to create a horizontal selection!");
				}
			}
		}
		
		if(command != null) {
			return new SimpleCauseEffectGenerator(command);
		} else {
			CELogger.log().warn("Unable to generate CauseEffectGenerator");
			return null;
		}
	}
	
	/**
	 * Attempt to generate a selector for a governing leaf within the expression, which uses horizontal selection to find the remaining words of the expression
	 * @param root The root fragment of the sentence
	 * @param phrase The phrase to be extracted
	 * @return Command, which extracts the given phrase from a sentence structure
	 */
	private SimpleCommand generateHorizontalSelectionByLeafs(Fragment root, String phrase) {
		// find the chain of leafs, that represent the phrase in the sentence
		ArrayList<Leaf> leafChain = findLeafChain(root, phrase);
		
		if(leafChain != null) {
			Leaf governor = null;
			
			// find the one leaf within the chain, which governs all other leafs
			ArrayList<Leaf> others = null;
			for(Leaf leaf : leafChain) {
				others = Utils.generateListWithout(leafChain, leaf);
				if(leaf.isGoverningAllLeafs(others)) {
					governor = leaf;
					break;
				}
			}
			
			if(governor != null) {
				// generate a selector picking the governing leaf
				SimpleCommand selectEligible = generateCommandSelector(root, governor);
				
				// add horizontal selection for all other leafs
				for(Leaf other : others) {
					CommandPick picker = generateCommandPickFor(governor, other);
					selectEligible.getFinal().addHorizontalSelection(picker);
				}
				// place the governor within the horizontal selections
				selectEligible.getFinal().setPositionOfSelectedBetweenHorizontalSelection(
						Utils.getPositionOfWordInExpression(phrase, governor.getCoveredText()));
				
				return selectEligible;
			} else {
				// unable to find a governing leaf in the leaf chain
				return null;
			}
		} else {
			// unable to find a chain of leafs representing the phrase
			return null;
		}
	}
	
	/**
	 * If possible, find the chain of leafs that represent the phrase in the internal representation of the sentence
	 * @param root Root fragment of the internal representation of the sentence
	 * @param phrase Expression, that shall be found in the sentence
	 * @return The chain of leafs in the sentence, that cover the phrase
	 */
	private ArrayList<Leaf> findLeafChain(Fragment root, String phrase) {
		ArrayList<Leaf> all = root.getAllLeafs();
		ArrayList<Leaf> phraseAsLeafs = new ArrayList<Leaf>();
		String[] phraseWords = phrase.split(" ");
		
		boolean leafChainFound = false;
		int phraseIndex = 0;
		for(Leaf leaf : all) {
			if(phraseWords[phraseIndex].equals(leaf.getCoveredText())) {
				phraseAsLeafs.add(leaf);
				phraseIndex++;
				
				if(phraseIndex == phraseWords.length) {
					leafChainFound = true;
					break;
				}
			}
		}
		
		if(leafChainFound) 
			return phraseAsLeafs;
		else
			return null;
	}
	
	/**
	 * Attempt to generate a selector for a governing leaf within the expression, which uses horizontal selection to find the remaining words of the expression
	 * @param root The root fragment of the sentence
	 * @param phrase The phrase to be extracted
	 * @return Command, which extracts the given phrase from a sentence structure
	 */
	private SimpleCommand generateHorizontalSelectionByWords(Fragment root, String phrase) {
		String[] wordsOfExpression = phrase.split(" ");
		for(String word : wordsOfExpression) {
			// for each word of the expression: check if all other words have a reference to this word
			ArrayList<Leaf> set = new ArrayList<Leaf>();
			root.getLeafs(false, word, set);
			
			// words used in the expression can appear multiple times, but only the one belonging to the expression is possibly governing
			for(Fragment eligibleFragment : set) {
				ArrayList<String> remainingWordsOfExpression = Utils.generateListWithout(wordsOfExpression, word);
			
				Leaf governor = (Leaf) eligibleFragment;
				if(governor.isGoverningAllPhrases(remainingWordsOfExpression)) {
					// create a command that selects the governing leaf node
					SimpleCommand selectEligible = generateCommandSelector(root, governor);
					
					for(String other : remainingWordsOfExpression) {
						CommandPick picker = generateCommandPickFor(root, governor, other);
						selectEligible.getFinal().addHorizontalSelection(picker);
					}
					selectEligible.getFinal().setPositionOfSelectedBetweenHorizontalSelection(
							Utils.getPositionOfWordInExpression(phrase, governor.getCoveredText()));
					
					return selectEligible;
				}
			}
		}
		
		// unable to generate a command extracting the phrase
		return null;
	}
	
	private SimpleCommand generateCommandSelector(Fragment root, Leaf leaf) {
		SimpleCommand command = null;
		
		ArrayList<Leaf> eligibleByExpression = new ArrayList<Leaf>();
		root.getLeafs(false, leaf.getCoveredText(), eligibleByExpression);
		
		int eligibleIndex = 0;
		if(eligibleByExpression.size() > 1) {
			for(Fragment eligible : eligibleByExpression) {
				if(eligible.equals(leaf)) {
					break;
				} else {
					eligibleIndex++;
				}
			}
		}
		
		Fragment eligible = eligibleByExpression.get(eligibleIndex);
		String eligibleType = eligible.getTag();
		
		ArrayList<Fragment> eligibleByType = new ArrayList<Fragment>();
		root.getBy(true, eligibleType, eligibleByType);
		
		if(eligibleByType.size() == 1) {
			command = new CommandSelect(true, eligibleType); 
		} else {
			SimpleCommand splitter = splitUntilSearchIsUnique(
					root,
					eligibleType,
					eligible.getCoveredText());
			command = addCommand(command, splitter);
			command = addCommand(command, new CommandSelect(true, eligibleType));
		}
		
		return command;
	}
	
	private SimpleCommand splitUntilSearchIsUnique(Fragment current, String tag, String text) {
		ArrayList<Fragment> currentlyEligible = new ArrayList<Fragment>();
		current.getBy(true, tag, currentlyEligible);
		
		if(currentlyEligible.size() == 1) {
			// the search for the type yields exactly one result
			return null;
		} else {
			// the search is currently not yielding a distinct result
			ArrayList<Fragment> split = current.split();
			
			// find a child-node where the search would be distinct
			for(int i = 0; i < split.size(); i++) {
				// check if the current child contains the phrase searched for
				// this is to ensure that the node searched for is exactly the relevant one, not another of the same type
				ArrayList<Fragment> wordCheck = new ArrayList<Fragment>();
				split.get(i).getBy(false, text, wordCheck);
				
				if(!wordCheck.isEmpty()) {
					// check if the current child also contains the relevant type
					ArrayList<Fragment> relevant = new ArrayList<Fragment>();
					split.get(i).getBy(true, tag, relevant);
					
					if(relevant.size() > 0) {
						SimpleCommand cmd = new CommandSplit(i);
						addCommand(cmd, splitUntilSearchIsUnique(split.get(i), tag, text));
						return cmd;
					}
				}
			}
			
			CELogger.log().warn("ERROR: splitUntilSearchIsUnique yields no results!");
			return null;
		}
	}
	
	private SimpleCommand getUniqueSelector(Fragment current, Fragment desired) {
		if(current.equals(desired)) {
			// this case occurs, when the desired chunk has a child node with the same tag
			SimpleCommand cmd = new CommandSelect();
			return cmd;	
		} else {
			ArrayList<Fragment> currentlyEligible = new ArrayList<Fragment>();
			current.getBy(true, desired.getTag(), currentlyEligible);
			
			if(currentlyEligible.size() == 1) {
				// the search for the type yields exactly one result
				SimpleCommand cmd = new CommandSelect(true, desired.getTag());
				return cmd;	
			} else {
				// the search is currently not yielding a distinct result
				ArrayList<Fragment> split = current.split();
				
				// find a child-node where the search would be distinct
				for(int i = 0; i < split.size(); i++) {
					// check if the current child contains the phrase searched for
					// this is to ensure that the node searched for is exactly the relevant one, not another of the same type
					
					if(split.get(i).contains(desired)) {
						SimpleCommand cmd = new CommandSplit(i);
						addCommand(cmd, getUniqueSelector(split.get(i), desired));
						return cmd;
					}
				}
				
				CELogger.log().warn("ERROR: splitUntilSearchIsUnique yields no results!");
				return null;
			}
		}
	}
	
	/**
	 * Attempts to chain a command to an existing one, if it exists
	 * @param existing The possibly already existing predecessor
	 * @param next The successor
	 * @return Commands chained in succession
	 */
	private SimpleCommand addCommand(SimpleCommand existing, SimpleCommand next) {
		if(existing == null) {
			return next;
		} else {
			existing.chainCommand(next);
			return existing;
		}
	}
	
	private CommandPick generateCommandPickFor(Fragment root, Leaf governor, String governed) {
		ArrayList<Leaf> eligible = new ArrayList<Leaf>();
		root.getLeafs(false, governed, eligible);
		
		if(!eligible.isEmpty()) {
			if(eligible.size() == 1) {
				return generateCommandPickFor(governor, eligible.get(0));
			} else {			
				Leaf closest = null; 
				int closestDegreeOfRelation = Integer.MAX_VALUE;
				
				for(Leaf elig : eligible) {
					int degreeOfRelation = governor.getDegreeOfRelation(elig);
					
					ArrayList<Fragment> highlightable = new ArrayList<Fragment>();
					highlightable.add(governor);
					highlightable.add(elig);
					
					if(degreeOfRelation < closestDegreeOfRelation) {
						closestDegreeOfRelation = degreeOfRelation;
						closest = elig;
					}
				}
				
				ArrayList<Fragment> highlightable = new ArrayList<Fragment>();
				highlightable.add(closest);
				CommandPick command = generateCommandPickFor(governor, closest);
				return command;
			}
		} else {
			CELogger.log().warn("Unable to generate a Pick-Command");
			CELogger.log().warn("  Sentence: " + root.toString());
			CELogger.log().warn("  Governor: " + governor.toString());
			CELogger.log().warn("  Governed: " + governed);
			return null;
		}
	}
	
	private CommandPick generateCommandPickFor(Leaf governor, Fragment governed) {
		for(Leaf gov : governor.getGoverned()) {		
			if(gov.equals(governed)) {
				String dependencyRelationType = gov.getDependencyRelationType();
				int index = gov.getNumberOfDependencyRelationOccurrencesBeforeThis();
				
				return new CommandPick(dependencyRelationType, index);
			} 
		}
		
		// transitive approach, as the current node does not directly govern the relevant node
		for(Leaf gov : governor.getGoverned()) {
			CommandPick transitiveSearchResult = generateCommandPickFor(gov, governed);
			if(transitiveSearchResult != null) {
				String dependencyRelationType = gov.getDependencyRelationType();
				int index = gov.getNumberOfDependencyRelationOccurrencesBeforeThis();
			
				CommandPick initialize = new CommandPick(dependencyRelationType, index);
				initialize.chainCommand(transitiveSearchResult);
				return initialize;
			}
		}
		return null;
	}
}
