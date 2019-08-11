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
import com.specmate.cerecognition.util.StringUtils;

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

	private SimpleCauseEffectGenerator generateCommandPattern(ISentence sentence, String ce) {
		SimpleCommand command = null;
		
		ArrayList<Fragment> eligibleByExpression = new ArrayList<Fragment>();
		sentence.getRoot().getBy(false, ce, eligibleByExpression);
		
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
				SimpleCommand splitter = splitUntilSearchIsUnique(
						sentence.getRoot(),
						eligibleType,
						eligible.getCoveredText());
				command = addCommand(command, splitter);
				command = addCommand(command, new CommandSelect(true, eligibleType));
			}
		} else if(eligibleByExpression.size() > 1) {
			// TODO more than one node eligible when searching for the given expression
			System.out.println("ERROR: More than one node eligible when searching for the expression '" + ce + "'");
			System.out.println("  Sentence under test: " + sentence.toString());
			for(Fragment eligible : eligibleByExpression) {
				System.out.println("  - " + eligible.toString());
			}
		} else if(eligibleByExpression.size() == 0) {
			// no node in the tree of constituents exclusively contains the full expression
			// horizontal approach: find one governing leaf among the nodes that make up the expression
			
			// TODO end loop once governing is found
			boolean governingFound = false;
			
			String[] wordsOfExpression = ce.split(" ");
			for(String word : wordsOfExpression) {
				// for each word of the expression: check if all other words have a reference to this word
				ArrayList<Fragment> set = new ArrayList<Fragment>();
				sentence.getRoot().getBy(false, word, set);
				
				// words used in the expression can appear multiple times, but only the one belonging to the expression is possibly governing
				for(Fragment eligibleFragment : set) {
					ArrayList<String> remainingWordsOfExpression = StringUtils.generateListWithout(wordsOfExpression, word);
				
					Leaf governor = (Leaf) eligibleFragment;
					if(governor.isGoverningAll(remainingWordsOfExpression)) {
						governingFound = true;
						
						// create a command that selects the governing leaf node
						SimpleCommand selectEligible = generateCommandPattern(sentence, eligibleFragment.getCoveredText()).getCommand();
						
						//TODO create pickers for the governed leaf nodes
						for(String other : remainingWordsOfExpression) {
							CommandPick picker = generateCommandPickFor(governor, other);
							selectEligible.getFinal().addHorizontalSelection(picker);
						}
						selectEligible.getFinal().setPositionOfSelectedBetweenHorizontalSelection(
								StringUtils.getPositionOfWordInExpression(ce, governor.getCoveredText()));
						command = selectEligible;
						break;
					}
				}
			}
		}
		
		if(command != null) {
			return new SimpleCauseEffectGenerator(command);
		} else {
			System.out.println("Unable to generate CauseEffectGenerator");
			return null;
		}
	}
	
	private SimpleCommand splitUntilSearchIsUnique(Fragment current, String tag, String text) {
		ArrayList<Fragment> currentlyEligible = new ArrayList<Fragment>();
		current.getBy(true, tag, currentlyEligible);
		
		if(currentlyEligible.size() == 1) {
			// the search for the type yields exactly one result
			return null;
		} else {
			// the search is currently not yielding a distinct result
			ArrayList<Fragment> split = null;
			try {
				split = current.split();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
			
			System.out.println("ERROR: splitUntilSearchIsUnique yields no results!");
			return null;
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
	
	private CommandPick generateCommandPickFor(Leaf leaf, String governed) {
		for(Leaf gov : leaf.getGoverned()) {		
			if(gov.getCoveredText().contentEquals(governed)) {
				String dependencyRelationType = gov.getDependencyRelationType();
				int index = gov.getNumberOfDependencyRelationOccurrencesBeforeThis();
				
				return new CommandPick(dependencyRelationType, index);
			} 
		}
		
		// transitive approach, as the current node does not directly govern the relevant node
		for(Leaf gov : leaf.getGoverned()) {
			CommandPick transitiveSearchResult = generateCommandPickFor(gov, governed);
			if(transitiveSearchResult != null) {
				CommandPick initialize = new CommandPick(gov.getDependencyRelationType());
				initialize.chainCommand(transitiveSearchResult);
				return initialize;
			}
		}
		return null;
	}
}
