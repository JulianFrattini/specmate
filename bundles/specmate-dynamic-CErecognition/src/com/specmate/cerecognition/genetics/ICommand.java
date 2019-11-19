package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;

/**
 * 
 * @author Julian Frattini
 * 
 * Interface for commands. Each command has to be applicable to a fragment, which is a logical,
 * grammatical sub-unit of the internal representation of a sentence, and each command has to
 * ultimately output a phrase, which is the cause-/effect-expression of a sentence
 */

public interface ICommand {
	
	/**
	 * Extracts a specific phrase from a sentence based on the chain on commands
	 * @param fragment Root node of the sentence, where the phrase is to be extracted
	 * @return The phrase extracted from the sentence
	 * @throws IllegalArgumentException Thrown if specific commands are invoked on invalid commands
	 * @throws Exception Thrown if command invokations cannot be resolved
	 */
	public String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception;
	
	/**
	 * Converts the command chain into human-readable form
	 * @return Chain of commands in readable form
	 */
	public String toString();
}
