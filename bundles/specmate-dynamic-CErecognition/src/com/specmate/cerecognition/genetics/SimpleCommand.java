package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;

public abstract class SimpleCommand implements ICommand {

	protected SimpleCommand successor;
	
	public SimpleCommand getSuccessor() {
		return successor;
	}
	
	public void setSuccessor(SimpleCommand successor) {
		this.successor = successor;
	}
	
	public void chainCommand(SimpleCommand successor) {
		if(this.successor == null) {
			this.successor = successor;
		} else {
			this.successor.chainCommand(successor);
		}
	}
	
	public abstract String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception;
	public abstract String toString();
	
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
}
