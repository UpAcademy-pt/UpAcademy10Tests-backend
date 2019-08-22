package pt.aubay.testesproject.execptionHandling;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<AppException>{

	@Override
	public Response toResponse(AppException exception) {
		return Response.status(exception.getStatusCode()).entity(exception.getMessage()).build();
	}

}
