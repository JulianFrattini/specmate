package com.specmate.cerecognition.pattern;

import java.util.ArrayList;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.causeeffectgraph.ICauseEffectPattern;
import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.ISentence;
import com.specmate.cerecognition.util.CELogger;

public class Pattern implements IPattern {
	
	private int index;

	/**
	 * structure of the sentence: constituency tree of the sentence 
	 * stripped of the leaf nodes containing only words
	 */
	private IStructure sentenceStructure; 
	
	/**
	 * genetic algorithm extracting the cause and effect from the 
	 * sentence of a specific structure
	 */
	private ICauseEffectPattern cePattern;
	
	/**
	 * Collection of all sentences complying the sentenceStructure
	 */
	private ArrayList<ISentence> accepted;
	
	public Pattern(int index, IStructure sentenceStructure, ICauseEffectPattern cePattern) {
		this.index = index;
		this.sentenceStructure = sentenceStructure;
		this.cePattern = cePattern;
		
		accepted = new ArrayList<ISentence>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndex() {
		return index;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStructure getStructure() {
		return sentenceStructure;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ICauseEffectPattern getGenerationPattern() {
		return cePattern;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ISentence> getAccepted() {
		return accepted;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkCompliance(ISentence candidate) {
		if(sentenceStructure.compliedBy(candidate.getRoot())) {
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAffiliated(ISentence candidate, ICauseEffectGraph ceg) {
		ICauseEffectGraph generated = cePattern.generateGraphFromSentence(candidate);
		
		if(generated != null) {
			return generated.equals(ceg);
		} else {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSentence(ISentence candidate) {
		accepted.add(candidate);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ICauseEffectGraph generateCauseEffectGraph(ISentence sentence) {
		return cePattern.generateGraphFromSentence(sentence);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deflectIntruder(ISentence intruder) {
		// try to identify a fragment, which contains a differentiating word
		StructureElement differentiating = getDifferentiating(accepted, intruder);
		
		if(differentiating != null) {
			// select the differentiating keyword
			String differentiator = differentiating.getProposedKeywords().remove(0);
			// add the keyword to the whitelist of the differentiating fragment
			differentiating.addKeyword(differentiator, true);
			return true;
		} else {
			CELogger.log().error("Unable to deflect intruding sentence in pattern #" + index + ":");
			CELogger.log().error("  Intruder: " + intruder.toString());
			CELogger.log().error("  Pattern Structure: " + sentenceStructure.toString());
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStructure differentiateSimilar(ISentence intruder) {
		// attempt to find a keyword in the approved sentences, that is not contained in the intruder
		StructureElement differentiating = getDifferentiating(accepted, intruder);
		
		if(differentiating != null) {
			// differentiating fragment found
			Structure otherStructure = new Structure(sentenceStructure.getRoot().clone());
			// as the cloned sentence structure, which will be used for the pattern of the intruding sentence,
			// already contains the keyword in his proposed keywords, all proposed keywords are added to the blacklist
			otherStructure.getRoot().listAllProposed(false);
			
			// the proposed keywords in this pattern will be added to the whitelist
			String differentiator = differentiating.getProposedKeywords().remove(0);
			differentiating.addKeyword(differentiator, true);
			
			return otherStructure;
		} else {
			// attempt to find a keyword in the intruder, that is not contained in the approved sentences
			// this is basically the reverse process of the aforementioned algorithm
			differentiating = getDifferentiating(intruder, accepted);
			
			if(differentiating != null) {
				// differentiating keyword found
				Structure otherStructure = new Structure(sentenceStructure.getRoot().clone());
				// as the cloned sentence structure, which will be used for the pattern of the intruding sentence,
				// already contains the keyword in his proposed keywords, all proposed keywords are added to the whitelist
				otherStructure.getRoot().listAllProposed(true);
				
				// the proposed keywords in this pattern will be added to the whitelist
				String differentiator = differentiating.getProposedKeywords().remove(0);
				differentiating.addKeyword(differentiator, false);
				
				return otherStructure;
			} else {
				CELogger.log().error("Unable to split pattern #" + index + ":");
				CELogger.log().error("  Intruder: " + intruder.toString());
				CELogger.log().error("  Pattern Structure: " + sentenceStructure.toString());
				return null;
			}
		}
	}
	
	// list of tags, that might trigger an differentiation, ordered by relevance
	private String[] differentiatingTags = {"IN", "PP", "DT", "VP", "NP"};
	/**
	 * Identifies a fragment that is similar in all approved sentences but different in the intruding sentence
	 * In this case, the approved sentences must contain the differentiator
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
			// when a leaf node type, that is contained in all approved sentences, is found, try to find one
			// type which is associated with a word throughout all sentence
			for(String tag : containedTypesInAllApproved) {
				// find a word that is the same in all approved sentence but different in the intruder
				String differentiator = getWordDifferentInTagBetweenApprovedAndIntruder(approved, intruding, tag);
				
				if(differentiator != null) {
					// find the structure element which hosts this differentiating word
					StructureElement differentiatingStructureElement = getDifferentiator(approved.get(0).getRoot(), sentenceStructure.getRoot(), differentiator, tag);
					if(differentiatingStructureElement != null) {
						// propose this differentiating word as a keyword for differentiation
						differentiatingStructureElement.addProposedKeywords(differentiator);
						return differentiatingStructureElement;
					} else {
						CELogger.log().error("Could not find an applicable differentiator");
					}
				}
			}
			
			CELogger.log().error("Positive Differentiation failed");
			return null;
		} else {
			CELogger.log().error("No type found which is contained in all approved sentences.");
			return null;
		}
	}
	
	/**
	 * Identifies a fragment that is similar in all approved sentences but different in the intruding sentence
	 * In this case, the intruding sentence must contain the differentiator
	 * @param approved The list of sentences that do belong to this pattern
	 * @param intruding The sentence, which is identified to belong to this pattern but actually is intruding
	 * @return The fragment differentiating the approved list from the intruder
	 */
	private StructureElement getDifferentiating(ISentence intruding, ArrayList<ISentence> approved) {
		// find a leaf node type that is contained in all approved sentences as well as the intruding sentence
		ArrayList<String> containedTypesInAllApproved = new ArrayList<String>();
		for(String tag : differentiatingTags) {
			if(isContainedByAllSentences(approved, true, tag) &&
					intruding.getRoot().contains(true, tag)) {
				containedTypesInAllApproved.add(tag);
			}
		}
		
		if(!containedTypesInAllApproved.isEmpty()) {
			for(String tag : containedTypesInAllApproved) {
				// find a word that is the same in all approved sentence but different in the intruder
				String differentiator = getWordDifferentInTagBetweenIntruderAndApproved(intruding, approved, tag);
				
				if(differentiator != null) {
					// find the structure element which hosts this differentiating word
					StructureElement differentiatingStructureElement = getDifferentiator(intruding.getRoot(), sentenceStructure.getRoot(), differentiator, tag);
					if(differentiatingStructureElement != null) {
						// propose this differentiating word as a keyword for differentiation
						differentiatingStructureElement.addProposedKeywords(differentiator);
						return differentiatingStructureElement;
					} else {
						CELogger.log().error("Could not find an applicable differentiator");
					}
				}
			}
			
			CELogger.log().error("Negative Differentiation failed");
			return null;
		} else {
			CELogger.log().error("No type found which is contained in all approved sentences.");
			return null;
		}
	}
	
	/**
	 * Checks if all sentences of a specific set contain a certain tag or word
	 * @param sentences List of sentences
	 * @param byType True, if the sentences are checked for tags, false if for words
	 * @param indicator Tag or word which has to be contained in all sentence
	 * @return True, if the given tag/word is contained in all sentences
	 */
	private boolean isContainedByAllSentences(ArrayList<ISentence> sentences, boolean byType, String indicator) {
		for(ISentence sentence : sentences) {
			if(!sentence.getRoot().contains(byType, indicator)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Attempt to identify a word, which is contained in all approved sentences but not the intruder
	 * @param approved List of approved sentences
	 * @param intruder Intruding sentence
	 * @param tag Type of the fragment where the differentiator is to be found 
	 * @return The word which differentiates the approved sentences from the intruder, if it exists
	 */
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
			// check if one of the words is contained by all approved sentences but not the intruder
			if(isContainedByAllSentences(approved, false, candidate) &&
					!intruder.getRoot().contains(false, candidate)) {
				return candidate;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Attempt to identify a word, which is contained in all approved sentences but not the intruder
	 * @param intruder Intruding sentence
	 * @param approved List of approved sentences
	 * @param tag Type of the fragment where the differentiator is to be found 
	 * @return The word which differentiates the approved sentences from the intruder, if it exists
	 */
	private String getWordDifferentInTagBetweenIntruderAndApproved(ISentence intruder, ArrayList<ISentence> approved, String tag) {
		// gather all words of the approved sentences associated with this tag 
		ArrayList<String> approvedWords = new ArrayList<String>();
		for(ISentence sentence : approved) {
			ArrayList<Fragment> set = new ArrayList<Fragment>();
			sentence.getRoot().getBy(true, tag, set);
			for(Fragment f : set) {
				if(!approvedWords.contains(f.getCoveredText())) {
					approvedWords.add(f.getCoveredText());
				}
			}
		}
		
		// gather all words of the intruding sentence which are associated with the given tag under inspection
		ArrayList<String> intrudingWords = new ArrayList<String>();
		ArrayList<Fragment> intrudingNodes = new ArrayList<Fragment>();
		intruder.getRoot().getBy(true, tag, intrudingNodes);
		for(Fragment intrudingNode : intrudingNodes) {
			if(!intrudingWords.contains(intrudingNode.getCoveredText())) {
				intrudingWords.add(intrudingNode.getCoveredText());
			}
		}
		
		// check if one of the words of the intruding sentence is not covered by the word pool of the approved sentence
		for(String candidate : intrudingWords) {
			if(!approvedWords.contains(candidate)) {
				return candidate;
			}
		}
		
		return null;
	}
	
	/**
	 * When given a sentence and a sentence structure, find the node in the sentence structure (StructureElement), where
	 * differentiating by a given word would yield a specific result. The comparison with an actual sentence has to be 
	 * made because the sentence structure holds no information about contained words and could not guarantee, at which
	 * position a differentiation would yield the desired results
	 * @param rootFragment Root node of the sentence
	 * @param rootStructure Root node of the sentence structure
	 * @param word Text of the differentiating element
	 * @param tag Tag of the differentiating element
	 * @return The node of the sentence structure, where a differentiation by the given word will yield the desired result
	 */
	private StructureElement getDifferentiator(Fragment rootFragment, StructureElement rootStructure, String word, String tag) {
		// find all child nodes of the sentence root, which contain the text as well as the tag of the differentiator
		Fragment eligibleFragment = null;
		for(Fragment child : rootFragment.split()) {
			if(child.contains(true, tag) && child.contains(false, word)) {
				eligibleFragment = child;
				break;
			}
		}
		
		if(eligibleFragment == null) {
			// no child node contains the differentiating term, therefore this root element must be the most precise differentiator
			return rootStructure;
		} else {
			// multiple child nodes contain the text-tag-combination that identifies the differentiator
			// hence the selection must be more precise
			
			// identify the node in the structure node, which is associated to the current sentence node
			StructureElement eligibleStructure = null; 
			for(StructureElement structChild : rootStructure.getChildren()) {
				if(structChild.getTag().contentEquals(eligibleFragment.getTag())) {
					eligibleStructure = structChild;
				}
			}
			
			if(eligibleStructure != null) {
				// recursively search for a more precise differentiator
				return getDifferentiator(eligibleFragment, eligibleStructure, word, tag);
			} else {
				// no further improvement in search for governor possible
				return rootStructure;
			}
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
