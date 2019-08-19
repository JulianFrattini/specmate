package com.specmate.cerecognition.genetics;

import java.util.ArrayList;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.util.CELogger;

public class CommandSplit extends SimpleCommand {
	
	/*
	 * index of the child branch with which the commands are to be continued
	 */
	private int continuationIndex;
	
	public CommandSplit(int continuationIndex) {
		this.continuationIndex = continuationIndex;
	}

	@Override
	public String generateOutput(Fragment fragment) {
		ArrayList<Fragment> split = fragment.split();
		
		if(split.size() > continuationIndex) {
			return successor.generateOutput(split.get(continuationIndex));
		} else if(split.isEmpty()) {
			CELogger.log().warn("'split'-command produced no split results");
		} else if(split.size() <= continuationIndex) {
			CELogger.log().warn("'split'-command produced " + split.size() + " continuents, but the continuation index is " + continuationIndex);
		}
		return null;
	}

	@Override
	public String toString() {
		String result = "split&continue with " + continuationIndex;
		if(successor != null) {
			result = result + " --> " + successor.toString();
		}
		return result;
	}
}