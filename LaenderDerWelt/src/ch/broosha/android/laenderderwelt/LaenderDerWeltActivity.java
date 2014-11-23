package ch.broosha.android.laenderderwelt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import ch.broosha.android.laenderderwelt.R;

public class LaenderDerWeltActivity extends ListActivity implements OnClickListener {
	
	private Button buttonGo;
	private Spinner spinnerCountryName;
	private CountriesAdapter countriesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Layout für die Eingabemaske holen:
		View input = getLayoutInflater().inflate(R.layout.countries_overview_input_layout, null);
		getListView().addHeaderView(input);
		
		// Default ArrayAdapter wird durch ein eigenes Adapter ersetzt:
		// LaenderAdapter definieren:
		//ArrayAdapter<Land> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Land>());
		countriesAdapter = new CountriesAdapter(this, new ArrayList<Country>());
		setListAdapter(countriesAdapter);
		
		// Elemente der Eingabe rauslesen:
		buttonGo = (Button) input.findViewById(R.id.buttonGo);
		
		//Dynamically generate a spinner data:
        createCountriesSpinnerDropDown(input);
		
		// buttonGo angeklickt:
		buttonGo.setOnClickListener(this);
	}

	
	
	private void createCountriesSpinnerDropDown(View header) {
		 
        //get reference to the spinner from the XML layout
		spinnerCountryName = (Spinner) header.findViewById(R.id.spinnerCountryName);
        List<String> countriesForSpinner = new ArrayList<String>();
       
        // XML-Datei mit der Liste aller Länder für den Eingabespinner auslesen:
 		XmlResourceParser xrpLand = this.getResources().getXml(R.xml.iso_3166);
 		HashMap<String, String> allCountries = new HashMap<String, String>();
		
		try {
 			int eventType = xrpLand.getEventType();
 	        while (eventType != XmlPullParser.END_DOCUMENT) {
 	        	if(eventType == XmlPullParser.START_DOCUMENT) {
 	        		System.out.println("Start document");
 	        	} else if(eventType == XmlPullParser.END_DOCUMENT) {
 	        		System.out.println("End document");
 	        	} else if(eventType == XmlPullParser.START_TAG) {
 	        		System.out.println("Start tag " + xrpLand.getName());
 	        		if ("iso_3166_entry".equals(xrpLand.getName())) {
 	        			if (xrpLand.getAttributeCount() >= 4) {
 	        				allCountries.put(xrpLand.getAttributeValue(0), xrpLand.getAttributeValue(3));
 	        				countriesForSpinner.add(xrpLand.getAttributeValue(3));
 	        			}
 	        		}
 	        	} else if(eventType == XmlPullParser.END_TAG) {
 	        		System.out.println("End tag " + xrpLand.getName());
 	        	} else if(eventType == XmlPullParser.TEXT) {
 	        		System.out.println("Text " + xrpLand.getText());
 	        	}
 	        	eventType = xrpLand.next();
 	        }
 	        countriesAdapter.setFullListCountries(allCountries);
 		}
 		catch (XmlPullParserException e) {
 			e.printStackTrace();
 		}
 		catch (IOException e) {
 			e.printStackTrace();
 		}
 		finally {
 			xrpLand.close();
 		}
     		
     	//Array list of countries to display in the spinner:
 		//create an ArrayAdapter from the String Array
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countriesForSpinner);
        //set the view for the Drop down list
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        spinnerCountryName.setAdapter(arrayAdapter);
     }
 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_country_overview, menu);
		return true;
	}

	
	@Override
	public void onClick(View view) {
		
		if (view.getId() == R.id.buttonGo) {
			loadCountryData();
		}
	}

	
	private void loadCountryData() {
		
		final CountryLoader countryLoader = new CountryLoader(this);
		
		// Progressdialog für das Laden von Daten aus dem Internet wird aufbereitet:
		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getResources().getString(R.string.country_data_still_loading));
		dlg.show();
		
		// Thread für das Laden von Daten aus dem Internet wird abgespalten:
		Thread worker = new Thread() {
			public void run () {
				
				String isoKey = CountriesAdapter.getKeyByValue(countriesAdapter.getFullListCountries(), spinnerCountryName.getSelectedItem().toString());
				final List<Country> countries = countryLoader.loadCountries(isoKey);
				
				runOnUiThread(new Runnable() {
					public void run() {
						// Progressdialog kann geschlossen werden:
						dlg.dismiss();
						
						// Default ArrayAdapter wird durch ein eigenes Adapter ersetzt:
						// LaenderAdapter definieren:
						//ArrayAdapter<Land> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Land>());
						CountriesAdapter countriesAdapter = new CountriesAdapter(LaenderDerWeltActivity.this, countries);
						setListAdapter(countriesAdapter);
					}
				});
					
			}
		};
		
		worker.start();
		
	}

}
