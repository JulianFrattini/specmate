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
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.cerecognition.util.CELogger;
import com.specmate.common.exception.SpecmateException;
import com.specmate.emfrest.api.IRestService;
import com.specmate.emfrest.api.RestServiceBase;
import com.specmate.model.base.Folder;
import com.specmate.model.base.IContentElement;
import com.specmate.rest.RestResult;
import org.eclipse.emf.common.util.EList;

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
	public RestResult<?> post(Object object2, Object object, String token) throws SpecmateException {
		if(object2 != null) {
			System.out.println("Object2 (" + object2.getClass().getSimpleName() + "): " + object2.toString());
			Folder folder = (Folder) object2;
			printFolder(folder, 0);
		} 
		else 
			System.out.println("Object2 is null");
		
		if(object != null) {
			System.out.println("Object (" + object.getClass().getSimpleName() + "): " + object.toString());
			Folder folder = (Folder) object;
			printFolder(folder, 0);
		} else 
			System.out.println("Object is null");
		
		
		System.out.println("Token: " + token);
		
		return new RestResult<>(Response.Status.OK);
		/*main.train(crop(queryParams.getFirst("sentence")),
					crop(queryParams.getFirst("cause")),
					crop(queryParams.getFirst("effect"))); */
	}
	
	private void printFolder(Folder folder, int tab) {
		EList<IContentElement> list = folder.getContents();
		for(IContentElement element: list) {
			for(int i = 0; i < tab; i ++) {
				System.out.print("  ");
			}
			System.out.println(" - " + element.getName() + " (ID: " + element.getId() + "): " + element.getDescription());
			if(element instanceof Folder) {
				printFolder((Folder) element, tab+1);
			}
			
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
			
			array.put(patternObject);
		}
		
		return array;
	}
	
	private JSONObject checkSentence(String sentence) {
		ICauseEffectGraph generatedCauseEffectGraph = main.getCEG(sentence);
		
		JSONObject response = new JSONObject();
		response.put("sentence", sentence);
		if(generatedCauseEffectGraph != null) {
			JSONObject causality = new JSONObject();
			causality.put("cause", generatedCauseEffectGraph.getCause());
			causality.put("effect", generatedCauseEffectGraph.getEffect());
			response.put("status", "causality");
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
