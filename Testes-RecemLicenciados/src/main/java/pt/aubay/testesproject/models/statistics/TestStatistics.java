package pt.aubay.testesproject.models.statistics;

import java.util.Set;

import pt.aubay.testesproject.models.dto.TestDTO;

public class TestStatistics {
	private TestDTO test;
	private Set<String> categories;
	public TestDTO getTest() {
		return test;
	}
	public void setTest(TestDTO test) {
		this.test = test;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

}
