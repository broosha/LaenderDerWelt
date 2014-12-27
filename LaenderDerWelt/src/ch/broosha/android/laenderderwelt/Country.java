package ch.broosha.android.laenderderwelt;

import java.util.ArrayList;
import java.util.Locale;

public class Country {
	private String name;
	private String fullName;
	private String nativeName;
	private String description;
	private String continent;
	private String capital;
	private String area;
	private String population;
	private String topLevelDomain;
	private String callingCode;
	private String currency;
	private String giniIndex;
	private ArrayList<String> neighbours;
	private ArrayList<String> languages;
	private ArrayList<String> timezones;
	
	public Country () {
		name = "";
		fullName = "";
		nativeName = "";
		description = "";
		continent = "";
		capital = "";
		area = "";
		population = "";
		topLevelDomain = "";
		callingCode = "";
		currency = "";
		giniIndex = "";
		neighbours = new ArrayList<String>();
		languages = new ArrayList<String>();
		timezones = new ArrayList<String>();
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
	
	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	public String getGiniIndex() {
		return giniIndex;
	}

	public void setGiniIndex(String giniIndex) {
		this.giniIndex = giniIndex;
	}

	public String getTopLevelDomain() {
		return topLevelDomain;
	}

	public void setTopLevelDomain(String topLevelDomain) {
		this.topLevelDomain = topLevelDomain;
	}

	public String getCallingCode() {
		return callingCode;
	}

	public void setCallingCode(String callingCode) {
		this.callingCode = callingCode;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}

	public ArrayList<String> getTimezones() {
		return timezones;
	}

	public void setTimezones(ArrayList<String> timezones) {
		this.timezones = timezones;
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
