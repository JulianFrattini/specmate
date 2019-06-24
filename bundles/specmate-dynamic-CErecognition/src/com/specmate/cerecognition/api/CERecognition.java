package com.specmate.cerecognition.api;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;

import com.specmate.common.exception.SpecmateException;
import com.specmate.emfrest.api.RestServiceBase;
import com.specmate.rest.RestResult;

@Component
public class CERecognition extends RestServiceBase  {

	@Override
	public String getServiceName() {
		return "cerec";
	}
	
	@Override
	public boolean canGet(Object object) {
		return true;
	}
	
	@Override
	public RestResult<?> get(Object object, MultivaluedMap<String, String> queryParams, String token)
			throws SpecmateException {
		
		System.out.println("cereg");
		return new RestResult<>(Response.Status.OK);
	}
}
