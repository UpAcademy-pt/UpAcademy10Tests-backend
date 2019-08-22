package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.execptionHandling.AppException;
import pt.aubay.testesproject.models.dto.CandidateDTO;
import pt.aubay.testesproject.models.entities.Candidate;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;

public class CandidateBusiness {
	
	@Inject
	RegisteredUserRepository userRepository;
	
//	@Inject
//	RegisteredUserBusiness userBusiness;
	
	
	public void checkIfParametersThere(CandidateDTO candidate, boolean toEdit) throws AppException {
		if(toEdit && candidate.getId()==0)
			throw new AppException("An ID is needed.", Status.NOT_ACCEPTABLE.getStatusCode());
		if(!(candidate.getEmail()!=null &&
			candidate.getName()!=null &&
			candidate.getEmailRecruiter()!=null))
			throw new AppException("Fields must be all present, including ID.", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	public void checkIfParametersThere(CandidateDTO candidate) throws AppException {
		checkIfParametersThere(candidate, false);
	}
	
	public CandidateDTO convertEntityToDTO(Candidate candidate) {
		CandidateDTO candidateDTO=new CandidateDTO();
		candidateDTO.setEmail(candidate.getEmail());
		candidateDTO.setName(candidate.getName());
		candidateDTO.setId(candidate.getId());
		
		//There should be some confirmation as to the existence of said e-mail in the database
		candidateDTO.setEmailRecruiter(candidate.getRecruiter().getEmail());
		return candidateDTO;
	}
	
	public Candidate addDTOAsEntity(CandidateDTO candidateDTO) {
		Candidate candidate=new Candidate();
		candidate.setEmail(candidateDTO.getEmail());
		candidate.setName(candidateDTO.getName());
		String recruiterName=userRepository.getUsernameByEmail(candidateDTO.getEmailRecruiter());
		candidate.setRecruiter(userRepository.getUser(recruiterName));
		return candidate;
	}
}
