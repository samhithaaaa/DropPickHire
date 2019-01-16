package com.avinash.droppickhire.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.avinash.droppickhire.helper.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private static final String TAG = GooglePlacesAutocompleteAdapter.class.getSimpleName();
    private ArrayList<String> resultList;
    private Context context = null;
    ArrayList<String> placeIdList = null;
    ArrayList<String> mainTextList = null;
    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public int getCount() {
        if (resultList != null)
            return resultList.size();
        else
            return 0;
    }

    @Override
    public String getItem(int index) {
        if(getCount() >= index) {
            return resultList.get(index);
        } else {
            return "";
        }
    }


    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        ArrayList<String> descriptionList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Constants.PLACES_API_BASE + Constants.TYPE_AUTOCOMPLETE + Constants.OUT_JSON);
            sb.append("?key=" + Constants.API_KEY);
            String locale = context.getResources().getConfiguration().locale.getCountry();
            sb.append("&components=country:"+ locale);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Pl API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Pl API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            Log.d("yo", jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            descriptionList = new ArrayList(predsJsonArray.length());
            placeIdList = new ArrayList(predsJsonArray.length());
            mainTextList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).toString());
                String mainText = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text");
                String secondaryText = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text");
                descriptionList.add(mainText + "\n" + secondaryText);
                placeIdList.add(predsJsonArray.getJSONObject(i).getString("place_id"));
                mainTextList.add(mainText);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return descriptionList;
    }

    public String getPlaceId(int pos) {
        return placeIdList.get(pos);
    }

    public String getMainText(int pos) { return mainTextList.get(pos);}

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
//                    setImageVisibility();
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}