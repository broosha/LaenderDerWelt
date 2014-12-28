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
	private String giniIndex;
	private String topLevelDomains;
	private String callingCodes;
	private String currencies;
	private String languages;
	private String timezones;
	private String neighbours;
	private ArrayList<String> neighboursList;
	
	public Country () {
		name = "";
		fullName = "";
		nativeName = "";
		description = "";
		continent = "";
		capital = "";
		area = "";
		population = "";
		giniIndex = "";
		topLevelDomains = "";
		callingCodes = "";
		currencies = "";
		neighbours = "";
		languages = "";
		timezones = "";
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

	public String getTopLevelDomains() {
		return topLevelDomains;
	}

	public void setTopLevelDomains(String topLevelDomains) {
		this.topLevelDomains = topLevelDomains;
	}

	public String getCallingCodes() {
		return callingCodes;
	}

	public void setCallingCodes(String callingCodes) {
		this.callingCodes = callingCodes;
	}

	public String getCurrencies() {
		return currencies;
	}

	public void setCurrencies(String currencies) {
		this.currencies = currencies;
	}

	public String getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(String neighbours) {
		this.neighbours = neighbours;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getTimezones() {
		return timezones;
	}

	public void setTimezones(String timezones) {
		this.timezones = timezones;
	}

	public ArrayList<String> getNeighboursList() {
		return neighboursList;
	}

	public void setNeighboursList(ArrayList<String> neighboursList) {
		this.neighboursList = neighboursList;
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
