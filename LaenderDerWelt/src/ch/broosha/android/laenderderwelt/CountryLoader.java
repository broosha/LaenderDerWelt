package ch.broosha.android.laenderderwelt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import android.graphics.BitmapFactory;
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
	
	private static final String LANGUAGES_JSON_TAG_NAME = "name";
	private static final String LANGUAGES_JSON_TAG_NATIVE_NAME = "nativeName";
	
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
		Uri uri = Uri.parse(context.getResources().getString(R.string.restcountries_alpha_code) + countryDescription.toLowerCase());
		Country country = parseRestCountriesJson(nwu.loadText(uri));
		result.add(country);
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
		    
		    String topLevelDomains = "";
		    if (topLevelDomainJson != null && topLevelDomainJson.length() > 0) {
		    	for(int i = 0; i < topLevelDomainJson.length(); i++) {
		    		if (i > 0) {
		    			topLevelDomains = topLevelDomains + ", ";
		    		}
		    		topLevelDomains = topLevelDomains + topLevelDomainJson.getString(i);
		    	}
		    }
		    land.setTopLevelDomains(topLevelDomains);
		    
		    String currencies = "";
		    if (currenciesJson != null && currenciesJson.length() > 0) {
		    	for(int i = 0; i < currenciesJson.length(); i++) {
		    		if (i > 0) {
		    			currencies = currencies + ", ";
		    		}
		    		currencies = currencies + getCurrencyText(currenciesJson.getString(i));
		    	}
		    }
		    land.setCurrencies(currencies);
		    
		    String callingCodes = "";
		    if (callingCodesJson != null && callingCodesJson.length() > 0) {
		    	for(int i = 0; i < callingCodesJson.length(); i++) {
		    		if (callingCodesJson.getString(i).trim().length() > 0) {
			    		if (i > 0) {
			    			callingCodes = callingCodes + ", ";
			    		}
			    		callingCodes = callingCodes + "+" + callingCodesJson.getString(i);
		    		}
		    	}
		    }
		    if (callingCodes.trim().length() > 0) {
		    	land.setCallingCodes(callingCodes);
		    } else {
		    	land.setCallingCodes("-");
		    }
		    
		    String languages = "";
		    if (languagesJson != null && languagesJson.length() > 0) {
		    	if (languagesJson.length() == 1) {
		    		// do nothing
		    	} else {
		    		languages = "<i>" + languagesJson.length() + " languages </i> <br />";
		    	}
		    	for(int i = 0; i < languagesJson.length(); i++) {
		    		languages = languages + getLanguageText(languagesJson.getString(i));
		    		if (languagesJson.length() > 1 && i < (languagesJson.length()-1)) {
		    			languages = languages + ", <br />";
			    	}
		    	}
		    }
		    land.setLanguages(languages);
		    
		    String neighbours = "";
		    ArrayList<String> neighboursList = new ArrayList<String>();
		    if (bordersJson != null && bordersJson.length() > 0) {
		    	if (bordersJson.length() == 1) {
		    		// do nothing
		    	} else {
		    		neighbours = "<i>" + bordersJson.length() + " neighbours </i> <br />" ;
		    	}
		    	for(int i = 0; i < bordersJson.length(); i++) {
		    		if (i > 0) {
		    			neighbours = neighbours + ", ";
		    		}
		    		neighbours = neighbours + bordersJson.getString(i);
		    		neighboursList.add(bordersJson.getString(i));
		    	}
		    }
		    land.setNeighbours(neighbours);
		    land.setNeighboursList(neighboursList);
		   
		    String timezones = "";
		    if (timezonesJson != null && timezonesJson.length() > 0) {
		    	if (timezonesJson.length() == 1) {
		    		timezones = timezonesJson.getString(0);
		    	} else if (timezonesJson.length() == 2) {
		    		timezones = timezonesJson.length() + " Timezones - " + timezonesJson.getString(0) + " - " + timezonesJson.getString(1);
		    	} else {
		    		timezones = timezonesJson.length() + " Timezones between " + timezonesJson.getString(0) + " and " + timezonesJson.getString(timezonesJson.length()-1);
		    	}
		    	
		    }
		    land.setTimezones(timezones);
		    
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
		String result = currencyCode.toUpperCase();
		if (currencyCode != null && currencyCode.trim().length() > 0) {
			XmlResourceParser xrpCurrencies = this.context.getResources().getXml(R.xml.iso_4217);
			try {
				int eventType = xrpCurrencies.getEventType();
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	if(eventType == XmlPullParser.START_TAG) {
		        		if ("iso_4217_entry".equals(xrpCurrencies.getName())) {
		        			if (currencyCode.toUpperCase().trim().equals(xrpCurrencies.getAttributeValue(0))) {
		        				result =  xrpCurrencies.getAttributeValue(2);
		        				break;
		        			}
		        		}
		        	}
		        	eventType = xrpCurrencies.next();
		        }
			}
			catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				xrpCurrencies.close();
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param languageCode
	 * @return
	 */
	public String getLanguageText (String languageCode) {
		String result = languageCode.toUpperCase();
		String languagesJson = null;
	    try {
	    	InputStream is = this.context.getAssets().open("__iso639.json");
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        languagesJson = new String(buffer, "UTF-8");
	    
	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	   
	    try {
			JSONObject json = new JSONObject(languagesJson);
			JSONObject languageJson = json.getJSONObject(languageCode.toLowerCase());
			String nameJson = languageJson.getString(LANGUAGES_JSON_TAG_NAME);
			String nativeNameJson = languageJson.getString(LANGUAGES_JSON_TAG_NATIVE_NAME);
			
			if (nameJson != null && nameJson.trim().length() > 0) {
				result = nameJson;
				
				if (nativeNameJson != null && nativeNameJson.trim().length() > 0 && !nameJson.equals(nativeNameJson)) {
					result = result + " (" + nativeNameJson + ")";
				}
			}
			
	    } catch (JSONException e) {
		    e.printStackTrace();
		} 
	    
		return result;
	}
	
}
