package pt.aubay.testesproject.business;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.CandidateDTO;
import pt.aubay.testesproject.models.entities.Candidate;

public class CandidateBusiness {
	public Response checkIfParametersThere(Candidate candidate, boolean toEdit) {
		if(toEdit && candidate.getId()==0)
			Response.status(Status.NOT_ACCEPTABLE).entity("An ID is needed.").build();
		if( candidate.getEmail()!=null &&
			candidate.getName()!=null &&
			candidate.getRecruiter()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
	}
	
	public Response checkIfParametersThere(Candidate candidate) {
		return checkIfParametersThere(candidate, false);
	}
	
	public CandidateDTO convertEntityToDTO(Candidate candidate) {
		CandidateDTO candidateDTO=new CandidateDTO();
		candidateDTO.setEmail(candidate.getEmail());
		return null;
	}
}
