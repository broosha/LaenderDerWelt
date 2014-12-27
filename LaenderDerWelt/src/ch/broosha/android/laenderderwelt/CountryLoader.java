package ch.broosha.android.laenderderwelt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.widget.Toast;
import ch.broosha.android.laenderderwelt.utilities.NetworkUtility;
import ch.broosha.android.laenderderwelt.utilities.NetworkUtilityMessageHandler;

public class CountryLoader {
	
	private final Context context;
	
	private static final String REST_COUNTRIES_TAG_ALPHA2_CODE = "alpha2Code";
	private static final String REST_COUNTRIES_TAG_NAME = "name";
	private static final String REST_COUNTRIES_TAG_NATIVE_NAME = "nativeName";
	private static final String REST_COUNTRIES_TAG_CAPITAL = "capital";
	private static final String REST_COUNTRIES_TAG_POPULATION = "population";
	private static final String REST_COUNTRIES_TAG_REGION = "region";
	private static final String REST_COUNTRIES_TAG_SUBREGION = "subregion";
	private static final String REST_COUNTRIES_TAG_AREA = "area";
	private static final String REST_COUNTRIES_TAG_TOP_LEVEL_DOMAIN = "topLevelDomain";
	private static final String REST_COUNTRIES_TAG_CALLING_CODES = "callingCodes";
	private static final String REST_COUNTRIES_TAG_CURRENCIES = "currencies";
	private static final String REST_COUNTRIES_TAG_GINI = "gini";
	private static final String REST_COUNTRIES_TAG_BORDERS = "borders";
	private static final String REST_COUNTRIES_TAG_TIMEZONES = "timezones";
	private static final String REST_COUNTRIES_TAG_LANGUAGES = "languages";
	
	// DEPRECATED API:
	private static final String HAM_TAG_RESPONSE = "response";
	private static final String HAM_TAG_PLACE = "place";
	private static final String HAM_TAG_NAME = "name";
	private static final String HAM_TAG_ISO = "iso";
	private static final String HAM_TAG_CONTINENT = "continent";
	private static final String HAM_TAG_PROFILE = "profile";
	private static final String HAM_TAG_CAPITAL = "capital";
	private static final String HAM_TAG_AREA_KM = "areaKM";
	private static final String HAM_TAG_POPULATION = "pop";
	private static final String HAM_TAG_NEIGHBORS = "neighbors";
	
	
	public CountryLoader(Context context) {
		this.context = context;
	}

	
	public List<Country> loadCountries (String countryDescription) {
		List<Country> result = new ArrayList<Country>();
		
		final NetworkUtilityMessageHandler handler = new NetworkUtilityMessageHandler()
		{
			@Override
			public void onException(final Throwable exception) {
				
				((Activity)context).runOnUiThread(new Runnable()
				{
					@Override
					public void run() {
						Toast.makeText(context,exception.getLocalizedMessage() + " " + exception.toString(),Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void onError(final StatusLine statusLine) {

				((Activity)context).runOnUiThread(new Runnable()
				{
					@Override
					public void run() {
						Toast.makeText(context,statusLine.getReasonPhrase(),Toast.LENGTH_LONG).show();
					}
				});
			}
		};
		
		final NetworkUtility nwu = new NetworkUtility(handler);
		
//		Uri uri = Uri.parse(context.getResources().getString(R.string.ham_countries_api_uri, countryDescription));
		Uri uri = Uri.parse(context.getResources().getString(R.string.restcountries_alpha_code) + countryDescription);
		result.add(parseRestCountriesJson(nwu.loadText(uri)));
		return result;
	}

	
	/**
	 * 
	 * @param daten
	 * @return
	 */
	private Country parseRestCountriesJson(String daten) {
		Country land = new Country();
		try {
			JSONObject json = new JSONObject(daten);
			String nameJson = json.getString(REST_COUNTRIES_TAG_NAME);
		    String alphaCodeJson = json.getString(REST_COUNTRIES_TAG_ALPHA2_CODE).toLowerCase();
		    String continentJson = json.getString(REST_COUNTRIES_TAG_REGION);
		    if (json.getString(REST_COUNTRIES_TAG_SUBREGION) != null && json.getString(REST_COUNTRIES_TAG_SUBREGION).trim().length() > 0) {
		    	continentJson =  continentJson + " (" + json.getString(REST_COUNTRIES_TAG_SUBREGION) + ")";
		    }
		    String capitalJson = json.getString(REST_COUNTRIES_TAG_CAPITAL);
		    String areaJson = json.getString(REST_COUNTRIES_TAG_AREA);
		    String populationJson = json.getString(REST_COUNTRIES_TAG_POPULATION);
		    String giniJson = json.getString(REST_COUNTRIES_TAG_GINI);
		    String nativeNameJson = json.getString(REST_COUNTRIES_TAG_NATIVE_NAME);
		    
		    JSONArray topLevelDomainJson = null;
		    if (!json.isNull(REST_COUNTRIES_TAG_TOP_LEVEL_DOMAIN)) {
		    	topLevelDomainJson = json.getJSONArray(REST_COUNTRIES_TAG_TOP_LEVEL_DOMAIN); 
		    }
		    
		    JSONArray currenciesJson = null;
		    if (!json.isNull(REST_COUNTRIES_TAG_CURRENCIES)) {
		    	currenciesJson = json.getJSONArray(REST_COUNTRIES_TAG_CURRENCIES); 
		    }
		    
		    JSONArray callingCodesJson = null;
		    if (!json.isNull(REST_COUNTRIES_TAG_CALLING_CODES)) {
		    	callingCodesJson = json.getJSONArray(REST_COUNTRIES_TAG_CALLING_CODES); 
		    }
		    
		    JSONArray languagesJson = null;
		    if (!json.isNull(REST_COUNTRIES_TAG_LANGUAGES)) {
		    	languagesJson = json.getJSONArray(REST_COUNTRIES_TAG_LANGUAGES);
		    }
		    
		    JSONArray timezonesJson = null;
		    if (!json.isNull(REST_COUNTRIES_TAG_TIMEZONES)) {
		    	timezonesJson = json.getJSONArray(REST_COUNTRIES_TAG_TIMEZONES);
		    }
		    
		    JSONArray bordersJson = null;
		    if (!json.isNull(REST_COUNTRIES_TAG_BORDERS)) {
		    	bordersJson = json.getJSONArray(REST_COUNTRIES_TAG_BORDERS);
		    }
		    
		    // Ergebnisobjekt befuellen:
		    land.setName(nameJson);
		    land.setDescription(alphaCodeJson);
		    land.setArea(areaJson);
		    land.setCapital(capitalJson);
		    land.setContinent(continentJson);
		    land.setPopulation(populationJson);
		    land.setNativeName(nativeNameJson);
		    land.setGiniIndex(giniJson);
		    
		    String topLevelDomain = "";
		    if (topLevelDomainJson != null && topLevelDomainJson.length() > 0) {
		    	for(int i = 0; i < topLevelDomainJson.length(); i++) {
		    		if (i > 0) {
		    			topLevelDomain = topLevelDomain + ", ";
		    		}
		    		topLevelDomain = topLevelDomain + topLevelDomainJson.getString(i);
		    	}
		    }
		    land.setTopLevelDomain(topLevelDomain);
		    
		    String currency = "";
		    if (currenciesJson != null && currenciesJson.length() > 0) {
		    	for(int i = 0; i < currenciesJson.length(); i++) {
		    		if (i > 0) {
		    			currency = currency + ", ";
		    		}
		    		currency = currency + getCurrencyText(currenciesJson.getString(i));
		    	}
		    }
		    land.setCurrency(currency);
		    
		    String callingCode = "";
		    if (callingCodesJson != null && callingCodesJson.length() > 0) {
		    	for(int i = 0; i < callingCodesJson.length(); i++) {
		    		if (i > 0) {
		    			callingCode = callingCode + ", ";
		    		}
		    		callingCode = callingCode + "+" + callingCodesJson.getString(i);
		    	}
		    }
		    land.setCallingCode(callingCode);
		    
		    
		    ArrayList<String> neighbors = new ArrayList<String>();
		    for(int i = 0; i < bordersJson.length(); i++) {
		    	if (!"unknown".equals(land.checkNull(bordersJson.getString(i)))) {
		    		neighbors.add(i,bordersJson.getString(i));
		    	}
		    }
		    land.setNeighbours(neighbors);
		    
		} catch (JSONException e) {
		    e.printStackTrace();
		} 
		return land;
	}
	
	
	
	/**
	 * 
	 * @param currencyCode
	 * @return
	 */
	public String getCurrencyText (String currencyCode) {
		String result = "-";
		if (currencyCode != null && currencyCode.trim().length() > 0) {
			XmlResourceParser xrpCurrenices = this.context.getResources().getXml(R.xml.iso_4217);
			try {
				int eventType = xrpCurrenices.getEventType();
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	if(eventType == XmlPullParser.START_TAG) {
		        		if ("iso_4217_entry".equals(xrpCurrenices.getName())) {
		        			if (currencyCode.toUpperCase().trim().equals(xrpCurrenices.getAttributeValue(0))) {
		        				result =  xrpCurrenices.getAttributeValue(2);
		        				break;
		        			}
		        		}
		        	}
		        	eventType = xrpCurrenices.next();
		        }
			}
			catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				xrpCurrenices.close();
			}
		}
		return result;
	
	}
	
	
}
