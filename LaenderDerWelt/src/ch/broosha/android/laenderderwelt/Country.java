package ch.broosha.android.laenderderwelt;

import java.util.ArrayList;
import java.util.Locale;

public class Country {
	private String name;
	private String fullName;
	private String description;
	private String continent;
	private String capital;
	private String area;
	private String population;
	private ArrayList<String> neighbours;
	
	public Country () {
		name = "";
		fullName = "";
		description = "";
		continent = "";
		capital = "";
		area = "";
		population = "";
		neighbours = new ArrayList<String>();
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = checkNull(name);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = checkNull(description);
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = checkNull(continent);
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = checkNull(capital);
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = checkNull(area);
	}

	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = checkNull(population);
	}

	public ArrayList<String> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(ArrayList<String> neighbours) {
		this.neighbours = neighbours;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = checkNull(fullName);
	}
	
	
	protected String checkNull (String string) {
		if (string.equals(null) || 
				string.trim().length() == 0 || 
					string.trim().toLowerCase(Locale.ENGLISH).equals("null")) {
			
			return "unknown";
		}
		return string;
	}
	
}
