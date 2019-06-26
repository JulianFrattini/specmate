package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.genetics.SimpleCommand;
import com.specmate.cerecognition.sentence.Fragment;

public class SimpleCauseEffectGenerator {
	
	private SimpleCommand command;
	
	public SimpleCauseEffectGenerator(SimpleCommand command) {
		this.command = command;
	}
	
	public SimpleCommand getCommand() {
		return command;
	}
	
	public String generateCEElement(Fragment fragment) {
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
}
