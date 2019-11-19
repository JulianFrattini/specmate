package com.specmate.cerecognition.genetics;

import java.util.ArrayList;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author Julian Frattini
 * 
 * Command for splitting the current fragment. When traversing the internal representation of
 * a sentence, which consists of the tree of fragments, a select command might not be specific
 * enough, since for example multiple child fragments are associated with the same tag.
 * Splitting the current fragment and continuing with one specific child node until the selection
 * of a fragment node with a specific tag yields only one fragment node solves this problem.
 */

public class CommandSplit extends SimpleCommand {
	
	/**
	 * index of the child branch with which the commands are to be continued
	 */
	private int continuationIndex;
	
	public CommandSplit(int continuationIndex) {
		this.continuationIndex = continuationIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateOutput(Fragment fragment) {
		// split the current fragment into its child fragments
		ArrayList<Fragment> split = fragment.split();
		
		if(split.size() > continuationIndex) {
			// recursively continue the genetic algorithm with the specific child node
			return successor.generateOutput(split.get(continuationIndex));
		} else if(split.isEmpty()) {
			// error occurring when the current fragment had no child nodes
			CELogger.log().warn("'split'-command produced no split results");
		} else if(split.size() <= continuationIndex) {
			// error occurring when the current fragment did not have enough child nodes
			CELogger.log().warn("'split'-command produced " + split.size() + " continuents, but the continuation index is " + continuationIndex);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result = "split&continue with " + continuationIndex;
		if(successor != null) {
			result = result + " --> " + successor.toString();
		}
		return result;
	}
}
