package com.specmate.cerecognition.genetics;

import com.specmate.cerecognition.sentence.Fragment;

public interface ICommand {
	public String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception;
	public String toString();
}
