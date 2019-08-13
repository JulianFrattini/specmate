package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.genetics.SimpleCommand;
import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.util.CELogger;

public class SimpleCauseEffectGenerator {
	
	private SimpleCommand command;
	
	public SimpleCauseEffectGenerator(SimpleCommand command) {
		this.command = command;
	}
	
	public SimpleCommand getCommand() {
		return command;
	}
	
	public String generateCEElement(Fragment fragment) {
		if(command == null) {
			CELogger.log().warn("Command is not defined (object is null)!");
			return null;
		}
		
		try {
			return command.generateOutput(fragment);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString() {
		return command.toString();
	}
}
