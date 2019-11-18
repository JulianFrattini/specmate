package com.specmate.cerecognition.trainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 
 * @author Julian Frattini
 * 
 * Reader class capable of reading JSON-Files. The file must have the following structure:
 * 	[
		{
			"sentence": "This is a causal sentence example, because it contains a causality.",
			"causality": {
				"cause": "it contains a causality",
				"effect": "This is a causal sentence example"
			}
		}, {
			"sentence": "This is a non-causal sentence example"
		}, ...
	]
 */

public class JSONCausalityExampleReader implements ICausalityExampleReader {

	private JSONArray examples;
	private boolean initialized;
	
	public JSONCausalityExampleReader() {
		initialized = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(String filename) {
		File file = new File(filename);
		
		initialize(file);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(File examplefile) {
		try {
			FileReader reader = new FileReader(examplefile);
			JSONTokener tokener = new JSONTokener(reader);
			examples = new JSONArray(tokener);
			initialized = true;
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: no CauseEffectExamples found at '" + examplefile.getAbsolutePath() + "'");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<CausalityExample> readExamples() {
		ArrayList<CausalityExample> result = new ArrayList<CausalityExample>();
		
		if(initialized) {
			for(int i = 0; i < examples.length(); i++) {
				JSONObject exampleJSON = examples.getJSONObject(i);
				
				String sentence = exampleJSON.getString("sentence");
				
				CausalityExample example = null;
				if(exampleJSON.has("causality")) {
					String cause = exampleJSON.getJSONObject("causality").getString("cause");
					String effect = exampleJSON.getJSONObject("causality").getString("effect");
					
					example = new CausalityExample(sentence, cause, effect);
				} else {
					example = new CausalityExample(sentence);
				}
				
				result.add(example);
			}
		}
		return result;
	}

}
