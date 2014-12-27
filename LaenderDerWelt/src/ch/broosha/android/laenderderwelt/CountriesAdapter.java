package ch.broosha.android.laenderderwelt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CountriesAdapter extends BaseAdapter {

	private Context context;
	private List<Country> countries;
	
	private HashMap<String, String> fullListCountries = new HashMap<String, String>();
	   
	
	public CountriesAdapter(Context context, List<Country> countries) {
		this.context = context;
		this.countries = countries;
	}

	@Override
	public int getCount() {
		return this.countries.size();
	}

	@Override
	public Object getItem(int position) {
		return this.countries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View result = convertView;
		if (result == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			result = inflater.inflate(R.layout.countries_overview_entry_layout, null);
		}
		Country country = (Country) this.getItem(position); 
		
		TextView textViewCountryOverviewTitle = (TextView) result.findViewById(R.id.textViewCountriesOverviewTitle);
		TextView textViewCapital = (TextView) result.findViewById(R.id.textViewCapital);
		TextView textViewPopulation = (TextView) result.findViewById(R.id.textViewPopulation);
		TextView textViewArea = (TextView) result.findViewById(R.id.textViewArea);
		TextView textViewContinent = (TextView) result.findViewById(R.id.textViewContinent);
		TextView textViewNativeName = (TextView) result.findViewById(R.id.textViewNativeName);
		TextView textViewCurrency = (TextView) result.findViewById(R.id.textViewCurrency);
		TextView textViewCallingCode = (TextView) result.findViewById(R.id.textViewCallingCode);
		TextView textViewTopLevelDomain = (TextView) result.findViewById(R.id.textViewTopLevelDomain);
		TextView textViewGiniIndex = (TextView) result.findViewById(R.id.textViewGiniIndex);
//		ListView listViewNeighbours = (ListView) result.findViewById(R.id.listViewNeighbours);
//		TextView listViewNeighbours = (TextView) result.findViewById(R.id.textViewNeighbours);
		ImageView imageViewIcon = (ImageView) result.findViewById(R.id.imageViewIcon);
		
		
		
		textViewCountryOverviewTitle.setText(country.getName().toUpperCase(Locale.ENGLISH) + " (" + country.getDescription().toUpperCase() + ")");
		if (!"unknown".equals(country.getCapital())) {
			textViewCapital.setText("Capital: " + country.getCapital().toUpperCase(Locale.ENGLISH));
		} else {
			textViewCapital.setText("Capital: -");
		}
		
		if (country.getPopulation() != null && country.getPopulation().trim().length() > 0 && !"unknown".equals(country.getPopulation()) && Long.valueOf(country.getPopulation()) >  0) {
			textViewPopulation.setText("Population: " + DecimalFormat.getInstance(new Locale("de", "CH")).format(Long.valueOf(country.getPopulation())));
		} else {
			textViewPopulation.setText("Population: -");
		}
		
		if (country.getArea() != null && country.getArea().trim().length() > 0 && !"unknown".equals(country.getArea()) && Double.valueOf(country.getArea()) >  0) {
			textViewArea.setText("Area: " + DecimalFormat.getInstance(new Locale("de", "CH")).format(Double.valueOf(country.getArea())) + " km");
		} else {
			textViewArea.setText("Area: -");
		}
		
		if (country.getContinent() != null && country.getContinent().trim().length() > 0) {
			textViewContinent.setText("Region: " + country.getContinent());
		}  else {
			textViewNativeName.setText("Region: -");
		}
		
		if (country.getNativeName() != null && country.getNativeName().trim().length() > 0) {
			textViewNativeName.setText("Origin name: " + country.getNativeName());
		} else {
			textViewNativeName.setText("Origin name: -");
		}
		
		if (country.getCurrency() != null && country.getCurrency().trim().length() > 0) {
			textViewCurrency.setText("Currency: " + country.getCurrency());
		} else {
			textViewCurrency.setText("Currency: -");
		}
		
		if (country.getCallingCode() != null && country.getCallingCode().trim().length() > 0) {
			textViewCallingCode.setText("Calling code: " + country.getCallingCode());
		} else {
			textViewCallingCode.setText("Calling code: -");
		}
		
		if (country.getTopLevelDomain() != null && country.getTopLevelDomain().trim().length() > 0) {
			textViewTopLevelDomain.setText("Top level domain: " + country.getTopLevelDomain());
		} else {
			textViewTopLevelDomain.setText("Top level domain: -");
		}
		
		if (country.getGiniIndex() != null && country.getGiniIndex().trim().length() > 0 && !"null".equals(country.getGiniIndex())) {
			textViewGiniIndex.setText("GINI Index: " + country.getGiniIndex());
		} else {
			textViewGiniIndex.setText("GINI Index: -");
		}
		
		
		if (country.getNeighbours() != null && country.getNeighbours().size() > 0) {
			String stringNeighbors = "";
			for(int i = 0; i < country.getNeighbours().size(); i++) {
		    	String currentNeighbor = this.getFullListCountries().get(country.getNeighbours().get(i));
		    	
		    	stringNeighbors = stringNeighbors + currentNeighbor;
		    }
			//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_list_item_1, land.getNachbarn());
			//listViewNachbarn.setAdapter(arrayAdapter); 
			
		}
		
		try {
			String fileName = country.getDescription() + ".png";
			InputStream is = this.context.getAssets().open(fileName);
			BufferedInputStream buf = new BufferedInputStream(is);
			imageViewIcon.setImageBitmap(BitmapFactory.decodeStream(buf));
		
		} catch (IOException e) {
			e.printStackTrace(); 
		}

		return result;
	}
	
	/**
	 * 
	 * @param map
	 * @param value
	 * @return
	 */
	public static <T, E> T getKeyByValue(Map <T, E> map, E value) {
	    for (Entry <T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}


	public HashMap<String, String> getFullListCountries() {
		return fullListCountries;
	}

	public void setFullListCountries(HashMap<String, String> fullListCountries) {
		this.fullListCountries = fullListCountries;
	}

}
