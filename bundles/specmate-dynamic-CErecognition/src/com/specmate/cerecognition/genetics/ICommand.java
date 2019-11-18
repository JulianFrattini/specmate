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
	public String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception;
	public String toString();
}
