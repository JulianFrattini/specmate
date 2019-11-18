package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.genetics.SimpleCommand;
import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author julian
 * Wrapper around a SimpleCommand, which is the most basic form of genetic algorithms extracting
 * one specific substring from a sentence
 */

public class SimpleCauseEffectGenerator {
	
	private SimpleCommand command;
	
	public SimpleCauseEffectGenerator(SimpleCommand command) {
		this.command = command;
	}
	
	public SimpleCommand getCommand() {
		return command;
	}
	
	/**
	 * Applies the extraction-algorithm to a sentence in internal representation
	 * @param fragment grammatic root of a sentence
	 * @return the specific extracted phrase
	 */
	public String generateCEElement(Fragment fragment) {
		if(command == null) {
			CELogger.log().warn("Command is not defined (object is null)!");
			return null;
		}
		
		try {
			return command.generateOutput(fragment);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString() {
		return command.toString();
	}
}
