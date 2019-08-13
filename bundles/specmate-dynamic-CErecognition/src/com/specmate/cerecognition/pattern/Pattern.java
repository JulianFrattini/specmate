package com.specmate.cerecognition.pattern;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.util.CELogger;

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
	private ArrayList<ISentence> accepted;
	
	public Pattern(int index, IStructure sentenceStructure, ICauseEffectPattern cePattern) {
		this.index = index;
		this.sentenceStructure = sentenceStructure;
		this.cePattern = cePattern;
		
		accepted = new ArrayList<ISentence>();
	}
	
	public int getIndex() {
		return index;
	}
	
	public IStructure getStructure() {
		return sentenceStructure;
	}
	
	public ICauseEffectPattern getGenerationPattern() {
		return cePattern;
	}
	
	/**
	 * Checks, whether a new sentence complies with the sentence structure of this pattern
	 * @param candidate Sentence which has to be checked
	 * @return True, if the sentence's structure equals the sentence structure of this pattern
	 */
	public boolean checkCompliance(ISentence candidate) {
		if(sentenceStructure.compliedBy(candidate.getRoot())) {
			return true;
		}
		return false;
	}
	
	public boolean checkSentenceCompliance(ISentence candidate, ICauseEffectGraph ceg) {
		return cePattern.generateGraphFromSentence(candidate).equals(ceg);
	}
	
	public void addSentence(ISentence candidate) {
		accepted.add(candidate);
	}
	
	public ICauseEffectGraph generateCauseEffectGraph(ISentence sentence) {
		return cePattern.generateGraphFromSentence(sentence);
	}
	
	public boolean deflectIntruder(ISentence intruder) {
		StructureElement differentiating = getDifferentiating(accepted, intruder);
		
		if(differentiating != null) {
			String differentiator = differentiating.getProposedKeywords().remove(0);
			differentiating.addKeyword(differentiator, true);
			return true;
		} else {
			CELogger.log().error("Unable to deflect intruding sentence in pattern #" + index + ":");
			CELogger.log().error("  Intruder: " + intruder.toString());
			CELogger.log().error("  Pattern Structure: " + sentenceStructure.toString());
			return false;
		}
	}
	
	public IStructure differentiateSimilar(ISentence intruder) {
		StructureElement differentiating = getDifferentiating(accepted, intruder);
		
		if(differentiating != null) {
			Structure otherStructure = new Structure(sentenceStructure.getRoot().clone());
			otherStructure.getRoot().blacklistAllProposed();
			
			String differentiator = differentiating.getProposedKeywords().remove(0);
			differentiating.addKeyword(differentiator, true);
			
			return otherStructure;
		} else {
			CELogger.log().error("Unable to split pattern #" + index + ":");
			CELogger.log().error("  Intruder: " + intruder.toString());
			CELogger.log().error("  Pattern Structure: " + sentenceStructure.toString());
			return null;
		}
	}
	
	// list of tags, that might trigger an differentiation, ordered by relevance
	private String[] differentiatingTags = {"IN", "PP", "DT"};
	/**
	 * Identifies a fragment that is similar in all approved sentences but different in the intruding sentence
	 * @param approved The list of sentences that do belong to this pattern
	 * @param intruding The sentence, which is identified to belong to this pattern but actually is intruding
	 * @return The fragment differentiating the approved list from the intruder
	 */
	private StructureElement getDifferentiating(ArrayList<ISentence> approved, ISentence intruding) {
		// find a leaf node type that is contained in all approved sentences
		ArrayList<String> containedTypesInAllApproved = new ArrayList<String>();
		for(String tag : differentiatingTags) {
			if(isContainedByAllSentences(approved, true, tag)) {
				containedTypesInAllApproved.add(tag);
			}
		}
		
		if(!containedTypesInAllApproved.isEmpty()) {
			for(String tag : containedTypesInAllApproved) {
				String differentiator = getWordDifferentInTagBetweenApprovedAndIntruder(approved, intruding, tag);
				
				if(differentiator != null) {
					StructureElement differentiatingStructureElement = getDifferentiator(approved.get(0).getRoot(), sentenceStructure.getRoot(), differentiator, tag);
					differentiatingStructureElement.addProposedKeywords(differentiator);
					return differentiatingStructureElement;
				}
			}
			
			CELogger.log().error("Could not find an applicable differentiator");
			return null;
		} else {
			CELogger.log().error("No type found which is contained in all approved sentences.");
			return null;
		}
	}
	
	private boolean isContainedByAllSentences(ArrayList<ISentence> sentences, boolean byType, String indicator) {
		for(ISentence sentence : sentences) {
			if(!sentence.getRoot().contains(byType, indicator)) {
				System.out.println((byType ? "Type" : "Word") + " " + indicator + " is not contained in " + sentence.getRoot().toString(true, false));
				return false;
			}
		}
		System.out.println((byType ? "Type" : "Word") + " " + indicator + " IS contained!");
		return true;
	}
	
	private String getWordDifferentInTagBetweenApprovedAndIntruder(ArrayList<ISentence> approved, ISentence intruder, String tag) {
		// gather all words of the approved sentences associated with this tag 
		ArrayList<String> correspondingWords = new ArrayList<String>();
		for(ISentence sentence : approved) {
			ArrayList<Fragment> set = new ArrayList<Fragment>();
			sentence.getRoot().getBy(true, tag, set);
			for(Fragment f : set) {
				if(!correspondingWords.contains(f.getCoveredText())) {
					correspondingWords.add(f.getCoveredText());
				}
			}
		}
		
		for(String candidate : correspondingWords) {
			if(isContainedByAllSentences(approved, false, candidate) &&
					!intruder.getRoot().contains(false, candidate)) {
				return candidate;
			}
		}
		
		return null;
	}
	
	private StructureElement getDifferentiator(Fragment rootFragment, StructureElement rootStructure, String word, String tag) {
		Fragment eligibleFragment = null;
		for(Fragment child : rootFragment.split()) {
			if(child.contains(true, tag) && child.contains(false, word)) {
				eligibleFragment = child;
				break;
			}
		}
		
		StructureElement eligibleStructure = null; 
		if(eligibleFragment != null) {
			for(StructureElement structChild : rootStructure.getChildren()) {
				if(structChild.getTag().contentEquals(eligibleFragment.getTag())) {
					eligibleStructure = structChild;
				}
			}
		} else {
			CELogger.log().error("No eligible child fragment found when searching for '" + word + "' (" + tag + ")");
			return null;
		}
		
		if(eligibleStructure != null) {
			return getDifferentiator(eligibleFragment, eligibleStructure, word, tag);
		} else {
			// no further improvement in search for governor possible
			return rootStructure;
		}
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
