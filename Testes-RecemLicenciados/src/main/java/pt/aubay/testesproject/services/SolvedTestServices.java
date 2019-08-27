package pt.aubay.testesproject.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import pt.aubay.testesproject.business.SolvedTestBusiness;
import pt.aubay.testesproject.business.TestBusiness;
import pt.aubay.testesproject.models.dto.QuestionDTO;
import pt.aubay.testesproject.models.dto.SolvedTestDTO;
import pt.aubay.testesproject.models.entities.SolvedTest;
import pt.aubay.testesproject.models.statistics.SolvedTestStatistics;

@Path("solved")
public class SolvedTestServices {
	@Inject
	SolvedTestBusiness solvedBusiness;
	
	@Inject
	TestBusiness testBusiness;
	
	@Context
	protected UriInfo context;
	
	@GET
	@Path("status")
	@Produces (MediaType.TEXT_PLAIN)
	public String healthCheck() {
		return "URI " + context.getRequestUri().toString() + " is OK!";
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addSolvedTest(SolvedTestDTO solvedTest) {
		solvedBusiness.add(solvedTest);
		return Response.ok().entity("Success").build();
	}
	
	@POST
	@Path("{sessionID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addSolvedTest(SolvedTestDTO solvedTest, @PathParam("sessionID") long sessionID){
		solvedBusiness.add(solvedTest, sessionID);
		return Response.ok().entity("Success").build();
	}
	
	@GET
	@Path("allData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSolvedTests() {
		return Response.ok(solvedBusiness.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSolvedTestsSimplified() {
		return Response.ok(solvedBusiness.getAll(true), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSolvedTest(@PathParam("id") long id) {
		return Response.ok(solvedBusiness.get(id), MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteSolvedTest(@PathParam("id") long id) {
		solvedBusiness.remove(id);
		return Response.ok().entity("Success").build();
	}
	
	@GET
	@Path("filter")
	@Produces({MediaType.APPLICATION_JSON})
	@Transactional
	public List<SolvedTestStatistics> getFilteredTests(
				@DefaultValue("0") @Min(0) @Max(100) @QueryParam("gradeMoreThan") long gradeMoreThan,
				@DefaultValue("100") @Min(0) @Max(100) @QueryParam("gradeLessThan") long gradeLessThan,
				@QueryParam("category") String category,
				@QueryParam("daysLessThan") long numberOfDays,
				@QueryParam("page") int page,
				@QueryParam("pageSize") int pageSize,
				@DefaultValue("name") @QueryParam("sortType") String sortType
				)
			{
		
		///////////////////////////////////////////////////////////PREDICATES////////////////////////////////////////////////////////////////////////////////////////
		Predicate<SolvedTest> gradeMoreThanPredicate = solvedTest -> solvedTest.getScore() >= gradeMoreThan;
		Predicate<SolvedTest> gradeLessThanPredicate = solvedTest -> solvedTest.getScore() <= gradeLessThan;
		Predicate<SolvedTest> ofCategoryPredicate = solvedTest -> StringUtils.isBlank(category) || testBusiness.getCategories(solvedTest.getId()).contains(category);
		Predicate<SolvedTest> lessThanNumberOfDaysPredicate = solvedTest -> (numberOfDays==0||solvedBusiness.lessOrEqualsNumberOfDays(numberOfDays, solvedTest));
		
		///////////////////////////////////////////////////////////FILTERED LIST/////////////////////////////////////////////////////////////////////////////////////
		List<SolvedTest> solvedTests=solvedBusiness.getAllEntities().stream().filter(
				gradeMoreThanPredicate.and(
				gradeLessThanPredicate).and(
				ofCategoryPredicate).and(
				lessThanNumberOfDaysPredicate
				)
				
				).collect(Collectors.toList());
		
		///////////////////////////////////////////////////////////////SORTING////////////////////////////////////////////////////////////////////////////////////////
		solvedTests.sort(solvedBusiness.comparator(sortType));
		
		List<SolvedTestStatistics> solvedTestsStatistics= new ArrayList<SolvedTestStatistics>();
		for(SolvedTest elem:solvedTests)
			solvedTestsStatistics.add(solvedBusiness.convertEntityToStatistics(elem, true));
		
		///////////////////////////////////////////////////////////////PAGINATION/////////////////////////////////////////////////////////////////////////////////////
		if(pageSize!=0) {
			int fromIndex = page * pageSize;
			int toIndex = fromIndex + pageSize;
			int resultSize = solvedTestsStatistics.size();
			
			if(fromIndex >= resultSize) {
				solvedTestsStatistics = Collections.emptyList();
			}
			else if(toIndex > resultSize) {
				solvedTestsStatistics = solvedTestsStatistics.subList(fromIndex, resultSize);
			}
			else {
				solvedTestsStatistics = solvedTestsStatistics.subList(fromIndex, toIndex);
			}
		}
		return solvedTestsStatistics;
	}
}
