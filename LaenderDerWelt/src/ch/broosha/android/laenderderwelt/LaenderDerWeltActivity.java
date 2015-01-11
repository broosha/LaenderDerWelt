package ch.broosha.android.laenderderwelt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class LaenderDerWeltActivity extends ListActivity implements OnItemSelectedListener, OnClickListener, LocationListener   {
	
	private static final String REST_COUNTRIES_TAG_ALPHA2_CODE = "alpha2Code";
	private static final String REST_COUNTRIES_TAG_NAME = "name";
	
	private static final String GOOGLE_API_GEOCODE_TAG_RESULTS = "results";
	private static final String GOOGLE_API_GEOCODE_TAG_ADDRESS_COMPONENTS = "address_components";
	private static final String GOOGLE_API_GEOCODE_TAG_TYPES = "types";
	private static final String GOOGLE_API_GEOCODE_TAG_COUNTRY = "country";
	private static final String GOOGLE_API_GEOCODE_TAG_SHORT_NAME = "short_name";
	private static final String GOOGLE_API_GEOCODE_TAG_STATUS = "status";
	private static final String GOOGLE_API_GEOCODE_TAG_STATUS_OK = "OK";
	
	private Spinner spinnerCountryNames;
	private CountriesAdapter countriesAdapterOnCreate;
	private CountriesAdapter countriesAdapterItem;
	
	private String allCountriesJson = "";
	private String residentCountryAlphaCode = "ch";
	private String residentCountryJson = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Layout für die Eingabemaske holen:
		View input = getLayoutInflater().inflate(R.layout.countries_overview_input_layout, null);
		getListView().addHeaderView(input);
		
		// Default ArrayAdapter wird durch ein eigenes Adapter ersetzt:
		countriesAdapterOnCreate = new CountriesAdapter(this, new ArrayList<Country>());
		setListAdapter(countriesAdapterOnCreate);
		
		// aktuelle Lokation ermitteln (falls erlaubt):
		Location location = getResidentLocation();
	    
	    // alle Laender mit der AsyncTask holen:
		try {
			residentCountryAlphaCode = getResidentCountry(location);
			residentCountryJson = new AsyncroniousTask().execute(getResources().getString(R.string.restcountries_alpha_code, residentCountryAlphaCode)).get();
			
			allCountriesJson = new AsyncroniousTask().execute(getResources().getString(R.string.restcountries_all_countries)).get();
		
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	    
		// Selectbox mit allen Laendern befüllen:
		createCountriesSpinnerDropDown(input);
		
		// spinnerCountryName: Land ausgesucht
		spinnerCountryNames.setOnItemSelectedListener(this);
	}

	/**
	 * Async Task
	 * 
	 * @author Mikhail
	 *
	 */
	private class AsyncroniousTask extends AsyncTask<String, Integer, String> {
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();
//		      displayProgressBar("Downloading...");
		   }
		 
		   @Override
		   protected String doInBackground(String... params) {
		      String url=params[0];
		      HttpClient httpclient = new DefaultHttpClient();
		      HttpResponse response;
		      String responseString = null;
		      try {
		    	  response = httpclient.execute(new HttpGet(url));
		          StatusLine statusLine = response.getStatusLine();
		          if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
		        	  ByteArrayOutputStream out = new ByteArrayOutputStream();
		              response.getEntity().writeTo(out);
		              out.close();
		              responseString = out.toString();
		          
		          } else {
		              // Connection schliessen:
		              response.getEntity().getContent().close();
		              throw new IOException(statusLine.getReasonPhrase());
		          }
		      } catch (ClientProtocolException e) {
		    	  e.printStackTrace();
		      } catch (IOException e) {
		    	  e.printStackTrace();
		      }
		      return responseString;
		   }
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) {
		      super.onProgressUpdate(values);
//		      updateProgressBar(values[0]);
		   }
		 
		   @Override
		   protected void onPostExecute(String result) {
		      super.onPostExecute(result);
//		      dismissProgressBar();
		   }
	}
	
	
	
	/**
	 * 
	 * @param header
	 */
	private void createCountriesSpinnerDropDown(View header) {
		spinnerCountryNames = (Spinner) header.findViewById(R.id.spinnerCountryName);
        List<String> countriesForSpinner = new ArrayList<String>();
        HashMap<String, String> allCountries = new HashMap<String, String>();
        String nameJson = "";
        String alphaCodeJson = "";
        
        try {
	        // **** Residenzland ****
        	JSONObject rootFirstCountryJson = new JSONObject(residentCountryJson);
	        nameJson = rootFirstCountryJson.getString(REST_COUNTRIES_TAG_NAME);
		    alphaCodeJson = rootFirstCountryJson.getString(REST_COUNTRIES_TAG_ALPHA2_CODE).toLowerCase();
		    nameJson = rootFirstCountryJson.getString(REST_COUNTRIES_TAG_NAME);
		    alphaCodeJson = rootFirstCountryJson.getString(REST_COUNTRIES_TAG_ALPHA2_CODE).toLowerCase();
		    
		    allCountries.put(alphaCodeJson, nameJson);
		    countriesForSpinner.add(nameJson);
        	
		    // **** resltiche Laender ****
        	JSONArray rootAllCountriesJson =  new JSONArray(allCountriesJson);
	        for(int i = 0; i < rootAllCountriesJson.length(); i++) {                                     
	            JSONObject countryJson = rootAllCountriesJson.getJSONObject(i);
	            nameJson = countryJson.getString(REST_COUNTRIES_TAG_NAME);
			    alphaCodeJson = countryJson.getString(REST_COUNTRIES_TAG_ALPHA2_CODE).toLowerCase();
			    
			    allCountries.put(alphaCodeJson, nameJson);
			    countriesForSpinner.add(nameJson);
	        }
	        countriesAdapterOnCreate.setFullListCountries(allCountries);
	        
        } catch (JSONException e) {
        	e.printStackTrace();
        }
        
        // ArrayAdapter fuer den Selectbox String Array vorbereiten
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countriesForSpinner);
        
        // view fuer die Drop down Liste
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // ArrayAdapter fuer Selectbox setzen
        spinnerCountryNames.setAdapter(arrayAdapter);
        
        // vorselektiertes Land direkt laden
        loadCountryData();
     }
 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_country_overview, menu);
		return true;
	}

	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//		Toast.makeText(parent.getContext(), 
//		        "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//		        Toast.LENGTH_SHORT).show();
		loadCountryData();
	}
	
	
	@Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nix tun
    }
    
	
	/**
	 * 
	 */
	public void onClick(View view) {
		
		if (view.getId() == R.id.textViewNeighbours) {
			String neighboursToastText = " no neighbours ";
			if (countriesAdapterItem.getNeighboursList() != null && countriesAdapterItem.getNeighboursList().size() > 0) {
				neighboursToastText = getNeighboursToastText(countriesAdapterItem.getNeighboursList());
				
			}
			Toast.makeText(getApplicationContext(), neighboursToastText, Toast.LENGTH_LONG).show();
		}
	}

	
	/**
	 * 
	 * @param neighboursCodes
	 * @param defaultText
	 * @return
	 */
	private String getNeighboursToastText (ArrayList<String> neighboursCodes) {
		String result = "";
		int i = 0;
		for (String neighboursCode : neighboursCodes) {
			if (i > 0) {
				result = result + "; ";
			}
			XmlResourceParser xrpCountries = this.getResources().getXml(R.xml.iso_3166);
			try {
				int eventType = xrpCountries.getEventType();
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	if(eventType == XmlPullParser.START_TAG) {
		        		if ("iso_3166_entry".equals(xrpCountries.getName())) {
		        			if (neighboursCode.toUpperCase().trim().equals(xrpCountries.getAttributeValue(1))) {
		        				result = result + xrpCountries.getAttributeValue(3);
		        				i++;
		        				break;
		        			}
		        		}
		        	}
		        	eventType = xrpCountries.next();
		        }
		    }
			catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				xrpCountries.close();
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	private void loadCountryData() {
		final CountryLoader countryLoader = new CountryLoader(this);
		
		// Progressdialog für das Laden von Daten aus dem Internet wird aufbereitet:
		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getResources().getString(R.string.country_data_still_loading));
		dlg.show();
		
		// Thread für das Laden von Daten aus dem Internet wird (ohne Async Task) abgespalten:
		Thread worker = new Thread() {
			public void run () {
				
				String isoKey = CountriesAdapter.getKeyByValue(countriesAdapterOnCreate.getFullListCountries(), spinnerCountryNames.getSelectedItem().toString());
				final List<Country> countries = countryLoader.loadCountries(isoKey);
				
				runOnUiThread(new Runnable() {
					public void run() {
						// Progressdialog kann geschlossen werden:
						dlg.dismiss();
						
						// Default ArrayAdapter wird durch ein eigenes Adapter ersetzt:
						CountriesAdapter countriesAdapter = new CountriesAdapter(LaenderDerWeltActivity.this, countries);
						setListAdapter(countriesAdapter);
						countriesAdapterItem = countriesAdapter;
					}
				});
					
			}
		};
		
		worker.start();
		
	}
	
	
	
	private Location getResidentLocation () {
		Location location = null;
		try {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    
			// GPS Provider enabled?
		    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		    // Network Provider enabled?
		    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		    if (!isGPSEnabled && !isNetworkEnabled) {
		        Log.i(this.getClass().getName(), "KEIN GEOLOCATION PROVIDER ENABLED!");
		    } else {
		    	if (isNetworkEnabled) {
		    		Log.i(this.getClass().getName(), "isNetworkEnabled");
		    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		            if (locationManager != null) {
		            	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		            	if (location != null) {
		            		Log.i("Location found:", location.toString());
			            	return location;
		                }
		            }
		        } else if (isGPSEnabled) {
		        	Log.i(this.getClass().getName(), "isGPSEnabled");
		        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	                if (locationManager != null) {
	                	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                	if (location != null) {
		            		Log.i("Location found:", location.toString());
		            		return location;
	                    }
	                }
		        }
		    }
		} catch (Exception e) {
	        e.printStackTrace();
	    }

		return location;
	}
	
	
	/**
	 * 
	 * @param location
	 * @return
	 */
	private String getResidentCountry(Location location) {
		String country = "ch";
		try {
			if (location != null) {
				String latitude = Double.toString(location.getLatitude());
				String longitude = Double.toString(location.getLongitude());
				Log.i(this.getClass().getName(), "latitude; longitude: " + latitude + ";" + longitude + ".");
				
				String googleApiResult = new AsyncroniousTask().execute(getResources().getString(R.string.google_api_geocode, latitude, longitude)).get();
				
				JSONObject rootJson =  new JSONObject(googleApiResult);
				JSONArray resultsJson = null;
				resultsJson = rootJson.getJSONArray(GOOGLE_API_GEOCODE_TAG_RESULTS); 
				if (GOOGLE_API_GEOCODE_TAG_STATUS_OK.equals(rootJson.getString(GOOGLE_API_GEOCODE_TAG_STATUS))) {
					JSONObject resultJson = resultsJson.getJSONObject(0);
					JSONArray addressComponentsJson = null;
					if (!resultJson.isNull(GOOGLE_API_GEOCODE_TAG_ADDRESS_COMPONENTS)) {
						addressComponentsJson = resultJson.getJSONArray(GOOGLE_API_GEOCODE_TAG_ADDRESS_COMPONENTS); 
						for(int i = 0; i < addressComponentsJson.length(); i++) { 
							JSONObject addressComponentJson = addressComponentsJson.getJSONObject(i);
							JSONArray typesJson = null;
							if (!addressComponentJson.isNull(GOOGLE_API_GEOCODE_TAG_TYPES)) {
								typesJson = addressComponentJson.getJSONArray(GOOGLE_API_GEOCODE_TAG_TYPES); 
								for(int j = 0; j < typesJson.length(); j++) {  
									if (GOOGLE_API_GEOCODE_TAG_COUNTRY.equals(typesJson.getString(j))) {
										String shortNameJson = addressComponentJson.getString(GOOGLE_API_GEOCODE_TAG_SHORT_NAME).toLowerCase();
										country = shortNameJson;
										break;
									}
								}
							}
						}
						
					}
				} else {
					Log.i(this.getClass().getName(), "KEIN LAND ZU GEOLOCATION GEFUNDEN!");
				}
			} else {
				Log.i(this.getClass().getName(), "KEIN LAND ZU GEOLOCATION UEBERMITTELT!");
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
        }
		return country;
	}

	
	@Override
	public void onLocationChanged(Location location) {
		 if (location != null) { 
			 Log.i("***** onLocationChanged", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude()); 
		 }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		 // nix tun

	}

	@Override
	public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new geolocation provider " + provider, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled geolocation provider " + provider, Toast.LENGTH_SHORT).show();
	}
}
