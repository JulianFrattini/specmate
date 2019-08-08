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
import com.specmate.cerecognition.main.Main;
import com.specmate.cerecognition.pattern.IPattern;
import com.specmate.common.exception.SpecmateException;
import com.specmate.emfrest.api.IRestService;
import com.specmate.emfrest.api.RestServiceBase;
import com.specmate.nlp.api.INLPService;
import com.specmate.rest.RestResult;

@Component(immediate=true, service = IRestService.class)
public class CERecognition extends RestServiceBase  {
	private ICauseEffectTrainer main;

	@Activate
	public void start() {
		//main = new Main();
	}
	
	@Override
	public String getServiceName() {
		return "cerec";
	}
	
	/*@Override
	public boolean canPost(Object object2, Object object) {
		return true;
	}

	@Override
	public RestResult<?> post(Object object2, Object object, String token) throws SpecmateException {
		if(queryParams.containsKey("cause") && queryParams.containsKey("effect")) {
			// train the algorithm with a sentence and a CEG
			main.train(crop(queryParams.getFirst("sentence")),
					crop(queryParams.getFirst("cause")),
					crop(queryParams.getFirst("effect")));
		} 
	}*/
	
	@Override
	public boolean canGet(Object object) {
		return true;
	}
	
	@Override
	public RestResult<?> get(Object object, MultivaluedMap<String, String> queryParams, String token)
			throws SpecmateException {
		if(queryParams.isEmpty()) {
			// standard get without query parameters: pattern overview
			return new RestResult<>(Response.Status.OK, createPatternOverview().toString());
		} else if(queryParams.containsKey("sentence")) {
			if(queryParams.containsKey("cause") && queryParams.containsKey("effect")) {
				// train a new pattern
				main.train(queryParams.getFirst("sentence"),
						queryParams.getFirst("cause"),
						queryParams.getFirst("effect"));
				return new RestResult<>(Response.Status.OK);
			} else {
				// generate the CEG of a sentence
				return new RestResult<>(
						Response.Status.OK, 
						checkSentence(queryParams.getFirst("sentence")).toString());
			}
		}
		
		StringJoiner sj = new StringJoiner(";");
		queryParams.forEach((key, value) -> sj.add(key + ":" + value)); 
		
		return new RestResult<>(Response.Status.OK, sj.toString());
	}
	
	private JSONArray createPatternOverview() {
		JSONArray array = new JSONArray();
		
		for(IPattern pattern : main.getPatterns()) {
			JSONObject o = new JSONObject();
			
			o.put("index", pattern.getIndex());
			o.put("structure", pattern.getStructure().toString());
			
			array.put(o);
		}
		
		return array;
	}
	
	private JSONObject checkSentence(String sentence) {
		ICauseEffectGraph ceg = main.getCEG(sentence);
		
		JSONObject o = new JSONObject();
		o.put("sentence", sentence);
		if(ceg != null) {
			o.put("cause", ceg.getCause());
			o.put("effect", ceg.getEffect());
		} else {
			o.put("status", "no cause effect");
		}
		
		return o;
	}
	
	private String crop(String param) {
		if(param.startsWith("[")) {
			param = param.substring(1);
		} 
		if(param.endsWith("]")) {
			param = param.substring(0, param.length()-1);
		}
		return param;
	}
	
	@Reference
	void setICauseEffectTrainer(ICauseEffectTrainer main) {
		this.main = main;
	}
}
