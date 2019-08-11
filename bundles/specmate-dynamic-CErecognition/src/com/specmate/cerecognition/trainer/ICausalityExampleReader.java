package com.specmate.cerecognition.trainer;

import java.io.File;
import java.util.ArrayList;

public interface ICausalityExampleReader {
	public void initialize(String filename);
	public void initialize(File examplefile);
	public ArrayList<CausalityExample> readExamples();
}
