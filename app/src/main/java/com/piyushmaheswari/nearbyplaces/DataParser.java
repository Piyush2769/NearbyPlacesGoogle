package com.piyushmaheswari.nearbyplaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJson)
    {
        HashMap<String,String> googlePlaceMap =new HashMap<>();
        String NameofPlace="-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String reference="";

        try
        {
            if(!googlePlaceJson.isNull("name"))
            {
                NameofPlace=googlePlaceJson.getString("name");
            }

            if(!googlePlaceJson.isNull("vicinity"))
            {
                vicinity=googlePlaceJson.getString("vicinity");
            }

            latitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference=googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",NameofPlace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);


        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }


    private List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter=jsonArray.length();

        List<HashMap<String,String>> nearbyPlacesList=new ArrayList<>();
        HashMap<String,String> nearbyPlaceMap=null;

        for(int i=0;i<counter;i++)
        {
            try {
                nearbyPlaceMap=getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                nearbyPlacesList.add(nearbyPlaceMap);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return nearbyPlacesList;
    }



}
