package com.specmate.cerecognition.api;

import java.util.StringJoiner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.specmate.cerecognition.causeeffectgraph.ICauseEffectGraph;
import com.specmate.cerecognition.main.CauseEffectRecognitionResult;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.trainer.CausalityExample;
import com.specmate.cerecognition.util.CELogger;
import com.specmate.common.exception.SpecmateException;
import com.specmate.emfrest.api.IRestService;
import com.specmate.emfrest.api.RestServiceBase;
import com.specmate.rest.RestResult;

@Component(immediate=true, service = IRestService.class)
public class CERecognitionRestService extends RestServiceBase  {
	private ICauseEffectRecognition main;

	@Activate
	public void start() {
		CELogger.log().initialize(System.out);
	}
	
	@Override
	public String getServiceName() {
		return "cerec";
	}
	
	@Override
	public boolean canPost(Object object2, Object object) {
		return true;
	}

	@Override
	public RestResult<?> post(Object object2, Object object, MultivaluedMap<String, String> queryParams, String token) throws SpecmateException {
		if(queryParams.containsKey("sentence") && 
				queryParams.containsKey("cause") && 
				queryParams.containsKey("effect")) {
			// training a new pattern
			String cause = queryParams.getFirst("cause");
			String effect = queryParams.getFirst("effect");
			
			CausalityExample sentence = null;
			if(!cause.isEmpty() && !effect.isEmpty()) {
				sentence = new CausalityExample(queryParams.getFirst("sentence"),
						cause,
						effect);
			} else {
				sentence = new CausalityExample(queryParams.getFirst("sentence"));
			}
			
			CauseEffectRecognitionResult result = main.train(sentence); 
	
			return new RestResult<>(Response.Status.OK, "Trained Cause-Effect-Recognition with result " + result.toString());
		} else {
			// unknown operation
			return new RestResult<>(
					Response.Status.NOT_FOUND, "Unknown POST-Operation");
		}
	}
	
	@Override
	public boolean canGet(Object object) {
		return true;
	}
	
	@Override
	public RestResult<?> get(Object object, MultivaluedMap<String, String> queryParams, String token)
			throws SpecmateException {
		if(queryParams.isEmpty()) {
			// standard get without query parameters: pattern overview
			return new RestResult<>(
					Response.Status.OK, 
					createPatternOverview().toString());
		} else if(queryParams.containsKey("sentence")) {
			// generate the Cause-Effect-Graph of a sentence
			return new RestResult<>(
					Response.Status.OK, 
					checkSentence(queryParams.getFirst("sentence")).toString());
		} else {
			// unknown operation
			StringJoiner sj = new StringJoiner(";");
			queryParams.forEach((key, value) -> sj.add(key + ":" + value)); 
			
			CELogger.log().info("Unknown key-value-pairs in the Cause-Effect REST-get: " + sj.toString());
			
			return new RestResult<>(
					Response.Status.NOT_FOUND, 
					"No operation known to the Cause-Effect-Recognition with the following key-value-pairs: " + sj.toString());
		}
	}
	
	/**
	 * Creates a list of all currently existing patterns
	 * @return List of all currently existing patterns
	 */
	private JSONArray createPatternOverview() {
		JSONArray array = new JSONArray();
		
		for(IPattern pattern : main.getPatterns()) {
			JSONObject patternObject = new JSONObject();
			
			patternObject.put("index", pattern.getIndex());
			patternObject.put("structure", pattern.getStructure().toString());
			
			JSONObject generationCommands = new JSONObject();
			generationCommands.put("cause", pattern.getGenerationPattern().getCommandString(true));
			generationCommands.put("effect", pattern.getGenerationPattern().getCommandString(false));
			
			patternObject.put("generation", generationCommands);
			
			JSONArray accepted = new JSONArray();
			pattern.getAccepted().forEach(s -> accepted.put(s.toString()));
			patternObject.put("accepted", accepted);
			
			array.put(patternObject);
		}
		
		return array;
	}
	
	/**
	 * Performs a testing operation on the system and attempts to generate a cause-effect-graph from a sentence
	 * @param sentence The sentence under test
	 * @return The evaluation of the system with a cause-effect-graph, if the sentence is causal and its patern already known
	 */
	private JSONObject checkSentence(String sentence) {
		ICauseEffectGraph generatedCauseEffectGraph = main.getCEG(sentence);
		
		JSONObject response = new JSONObject();
		response.put("sentence", sentence);
		if(generatedCauseEffectGraph != null) {
			JSONObject causality = new JSONObject();
			causality.put("cause", generatedCauseEffectGraph.getCause());
			causality.put("effect", generatedCauseEffectGraph.getEffect());
			response.put("status", causality);
		} else {
			response.put("status", "no cause effect");
		}
		
		return response;
	}
	
	@Reference
	void setICauseEffectTrainer(ICauseEffectRecognition main) {
		this.main = main;
	}
}
