package ch.broosha.android.laenderderwelt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CountriesAdapter extends BaseAdapter {

	private Context context;
	private List<Country> countries;
	
	private HashMap<String, String> fullListCountries = new HashMap<String, String>();
	
	private ArrayList<String> neighboursList;
	   
	int imageClickCounter = 0;
	
	
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
		TextView textViewCurrencies = (TextView) result.findViewById(R.id.textViewCurrencies);
		TextView textViewCallingCodes = (TextView) result.findViewById(R.id.textViewCallingCodes);
		TextView textViewTopLevelDomains = (TextView) result.findViewById(R.id.textViewTopLevelDomains);
		TextView textViewGiniIndex = (TextView) result.findViewById(R.id.textViewGiniIndex);
		TextView textViewLanguages = (TextView) result.findViewById(R.id.textViewLanguages);
		TextView textViewNeighbours = (TextView) result.findViewById(R.id.textViewNeighbours);
		TextView textViewTimezones = (TextView) result.findViewById(R.id.textViewTimezones);
		ImageView imageViewIcon = (ImageView) result.findViewById(R.id.imageViewIcon);
		ImageView restCountriesViewIcon = (ImageView) result.findViewById(R.id.restCountriesViewIcon);
		TextView textViewRestCountriesApi = (TextView) result.findViewById(R.id.textViewRestCountriesApi);
		
		textViewCountryOverviewTitle.setText(country.getName().toUpperCase(Locale.ENGLISH) + " (" + country.getDescription().toUpperCase() + ")");
		if (!"unknown".equals(country.getCapital())) {
			textViewCapital.setText(country.getCapital().toUpperCase(Locale.ENGLISH));
		} else {
			textViewCapital.setText("-");
		}
		
		if (country.getPopulation() != null && country.getPopulation().trim().length() > 0 && !"unknown".equals(country.getPopulation()) && Long.valueOf(country.getPopulation()) >  0) {
			textViewPopulation.setText(DecimalFormat.getInstance(new Locale("de", "CH")).format(Long.valueOf(country.getPopulation())));
		} else {
			textViewPopulation.setText("-");
		}
		
		if (country.getArea() != null && country.getArea().trim().length() > 0 && !"unknown".equals(country.getArea()) && Double.valueOf(country.getArea()) >  0) {
			String areaFormatted = DecimalFormat.getInstance(new Locale("de", "CH")).format(Double.valueOf(country.getArea()));
			textViewArea.setText(Html.fromHtml(areaFormatted + " km<sup><small>2</small></sup>"));
		} else {
			textViewArea.setText("-");
		}
		
		if (country.getContinent() != null && country.getContinent().trim().length() > 0) {
			textViewContinent.setText(country.getContinent());
		}  else {
			textViewNativeName.setText("-");
		}
		
		if (country.getNativeName() != null && country.getNativeName().trim().length() > 0) {
			textViewNativeName.setText(country.getNativeName());
		} else {
			textViewNativeName.setText("-");
		}
		
		if (country.getCurrencies() != null && country.getCurrencies().trim().length() > 0) {
			textViewCurrencies.setText(country.getCurrencies());
		} else {
			textViewCurrencies.setText("-");
		}
		
		if (country.getCallingCodes() != null && country.getCallingCodes().trim().length() > 0) {
			textViewCallingCodes.setText(country.getCallingCodes());
		} else {
			textViewCallingCodes.setText("-");
		}
		
		if (country.getTopLevelDomains() != null && country.getTopLevelDomains().trim().length() > 0) {
			textViewTopLevelDomains.setText(country.getTopLevelDomains());
		} else {
			textViewTopLevelDomains.setText("-");
		}
		
		if (country.getTimezones() != null && country.getTimezones().trim().length() > 0) {
			textViewTimezones.setText(country.getTimezones());
		} else {
			textViewTimezones.setText("-");
		}
		
		if (country.getGiniIndex() != null && country.getGiniIndex().trim().length() > 0 && !"null".equals(country.getGiniIndex())) {
			textViewGiniIndex.setText(country.getGiniIndex());
		} else {
			textViewGiniIndex.setText("-");
		}
		
		if (country.getLanguages() != null && country.getLanguages().trim().length() > 0) {
			textViewLanguages.setText(Html.fromHtml(country.getLanguages()));
		} else {
			textViewLanguages.setText("-");
		}
		
		if (country.getNeighbours() != null && country.getNeighbours().trim().length() > 0) {
			textViewNeighbours.setText(Html.fromHtml(country.getNeighbours()));
		} else {
			textViewNeighbours.setText("-");
		}
		if (country.getNeighboursList() != null && country.getNeighboursList().size() > 0) {
			this.setNeighboursList(country.getNeighboursList());
		}
				
		try {
			String fileName = country.getDescription() + ".png";
			InputStream is = this.context.getAssets().open(fileName);
			BufferedInputStream buf = new BufferedInputStream(is);
			imageViewIcon.setImageBitmap(BitmapFactory.decodeStream(buf));
			is.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		imageViewIcon.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				imageClickCounter++;
				if ((imageClickCounter % 2) == 1) {
					view.setScaleX(view.getScaleX()*2);
					view.setScaleY(view.getScaleY()*2);
					view.setLeft(view.getLeft()+60);
					view.setRight(view.getRight()+60);
					view.setTop(view.getTop()+40);
					view.setBottom(view.getBottom()+40);
					view.bringToFront();
				} else {
					view.setScaleX(view.getScaleX()/2);
					view.setScaleY(view.getScaleY()/2);
					view.setLeft(view.getLeft()-60);
					view.setRight(view.getRight()-60);
					view.setTop(view.getTop()+-40);
					view.setBottom(view.getBottom()-40);
				}
			}
		});
		
		try {
			String apiIconName = "_restCountries.png";
			InputStream is = this.context.getAssets().open(apiIconName);
			BufferedInputStream buf = new BufferedInputStream(is);
			restCountriesViewIcon.setImageBitmap(BitmapFactory.decodeStream(buf));
			is.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		textViewRestCountriesApi.setMovementMethod(LinkMovementMethod.getInstance()); 
		textViewRestCountriesApi.setText(Html.fromHtml("<i>Powered by <a href=\"http://restcountries.eu/\">REST Countries</a> API</i>"));
		
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

	public ArrayList<String> getNeighboursList() {
		return neighboursList;
	}

	public void setNeighboursList(ArrayList<String> neighboursList) {
		this.neighboursList = neighboursList;
	}

}
