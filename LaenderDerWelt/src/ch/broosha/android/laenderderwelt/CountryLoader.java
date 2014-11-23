package ch.broosha.android.laenderderwelt;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import ch.broosha.android.laenderderwelt.utilities.NetworkUtility;
import ch.broosha.android.laenderderwelt.utilities.NetworkUtilityMessageHandler;

public class CountryLoader {
	
	private final Context context;
	
	private static final String TAG_RESPONSE = "response";
	private static final String TAG_PLACE = "place";
	private static final String TAG_NAME = "name";
	private static final String TAG_ISO = "iso";
	private static final String TAG_CONTINENT = "continent";
	private static final String TAG_PROFILE = "profile";
	private static final String TAG_CAPITAL = "capital";
	private static final String TAG_AREA_KM = "areaKM";
	private static final String TAG_POPULATION = "pop";
	private static final String TAG_NEIGHBORS = "neighbors";
	
	
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
		
		Uri uri = Uri.parse(context.getResources().getString(R.string.ham_countries_api_uri, countryDescription));
		result.add(parseJson(nwu.loadText(uri)));
		return result;
	}


	private Country parseJson(String daten) {
		Country land = new Country();
		try {
			JSONObject json = new JSONObject(daten);
			JSONObject response = json.getJSONObject(TAG_RESPONSE);
		    
		    JSONObject place = response.getJSONObject(TAG_PLACE);
		    String name = place.getString(TAG_NAME);
		    String iso = place.getString(TAG_ISO);
		    String continent = place.getString(TAG_CONTINENT);
		    
		    JSONObject profile = response.getJSONObject(TAG_PROFILE);
		    String capitalJson = profile.getString(TAG_CAPITAL);
		    String areaJson = profile.getString(TAG_AREA_KM);
		    String populationJson = profile.getString(TAG_POPULATION);
		    JSONArray neighborsJson = profile.getJSONArray(TAG_NEIGHBORS);
		    
		    // Ergebnisobjekt befüllen:
		    land.setName(name);
		    land.setDescription(iso);
		    land.setArea(areaJson);
		    land.setCapital(capitalJson);
		    land.setContinent(continent);
		    land.setPopulation(populationJson);
		    
		    ArrayList<String> neighbors = new ArrayList<String>();
		    for(int i = 0; i < neighborsJson.length(); i++) {
		    	if (!"unknown".equals(land.checkNull(neighborsJson.getString(i)))) {
		    		neighbors.add(i,neighborsJson.getString(i));
		    	}
		    }
		    land.setNeighbours(neighbors);
		    
		} catch (JSONException e) {
		    e.printStackTrace();
		} 
		return land;
		
		
		
	}
}
