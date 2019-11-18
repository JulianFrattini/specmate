package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;

/**
 * @author Julian Frattini
 * 
 * Superclass of a simple version of commands. Commands can be chained together linearly via successors,
 * where the last command of each succession has to be a CommandSelect.
 */

public abstract class SimpleCommand implements ICommand {

	protected SimpleCommand successor;
	
	public SimpleCommand getSuccessor() {
		return successor;
	}
	
	public void setSuccessor(SimpleCommand successor) {
		this.successor = successor;
	}
	
	/**
	 * Pushes a new command to the end of the line of succession
	 * @param successor command, which shall be the last command of the succession
	 */
	public void chainCommand(SimpleCommand successor) {
		if(this.successor == null) {
			this.successor = successor;
		} else {
			this.successor.chainCommand(successor);
		}
	}
	
	/** 
	 * Generates a reference to the last command in the succession of commands.
	 * The final command has to be a 'select'-command. If not, there is an error.
	 * @return The final command in the succession
	 */
	public CommandSelect getFinal() {
		if(successor != null) {
			return successor.getFinal();
		} else {
			throw new IllegalArgumentException("Last command in the succession is not a 'select'-command");
		}
	}
	
	// generates the string phrase of a sentence
	public abstract String generateOutput(Fragment fragment);
	
	public abstract String toString();
}
