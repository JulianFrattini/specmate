package com.specmate.cerecognition.trainer;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author Julian Frattini
 * 
 * Interface for reader classes, which read causality examples.
 */

public interface ICausalityExampleReader {
	
	/**
	 * Initializes the reader with a filename
	 * @param filename Location of the file containing the causality examples
	 */
	public void initialize(String filename);
	
	/**
	 * Initializes the reader with a file
	 * @param examplefile File containing the causality examples
	 */
	public void initialize(File examplefile);
	
	/**
	 * Reads the causality examples, if the reader was correctly initialized, and returns them
	 * @return Set of causality examples
	 */
	public ArrayList<CausalityExample> readExamples();
}
