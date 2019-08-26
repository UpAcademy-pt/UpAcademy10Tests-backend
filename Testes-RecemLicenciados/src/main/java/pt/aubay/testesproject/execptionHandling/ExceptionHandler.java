package pt.aubay.testesproject.execptionHandling;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<ClientErrorException> {

	@Override
	public Response toResponse(ClientErrorException exception) {
		return Response.status(exception.getResponse().getStatusInfo()).entity(exception.getMessage()).build();
	}

}
