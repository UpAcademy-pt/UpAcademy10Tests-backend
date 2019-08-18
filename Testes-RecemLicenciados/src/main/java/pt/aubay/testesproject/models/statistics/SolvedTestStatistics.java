package pt.aubay.testesproject.models.statistics;

import java.util.Set;

import pt.aubay.testesproject.models.dto.SolvedTestDTO;

public class SolvedTestStatistics {
	
	private SolvedTestDTO solvedTestDTO;
	private Set<CategoryStatistics> categoryStatistics;
	public SolvedTestDTO getSolvedTest() {
		return solvedTestDTO;
	}
	public void setSolvedTest(SolvedTestDTO solvedTestDTO) {
		this.solvedTestDTO = solvedTestDTO;
	}
	public Set<CategoryStatistics> getCategoryStatistics() {
		return categoryStatistics;
	}
	public void setCategoryStatistics(Set<CategoryStatistics> categoryStatistics) {
		this.categoryStatistics = categoryStatistics;
	}
	
	
}
