package com.piyushmaheswari.nearbyplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Location lastLocation;
    private Marker currentLocationMarker;

    private int ProximityRadius=10000;

    private double latitude,longitude;

    private static final int Request_User_locationCode=99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkUserPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }

    public boolean checkUserPermission()
    {

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_locationCode);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_locationCode);

            }

            return false;
        }
        else
        {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        switch (requestCode)
        {
            case Request_User_locationCode:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                     if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                     {
                         if(googleApiClient==null)
                         {
                             buildGoogleApiClient();
                         }
                         mMap.setMyLocationEnabled(true);
                     }
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        lastLocation=location;

        if(currentLocationMarker!=null)
        {
            currentLocationMarker.remove();
        }

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);

        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        currentLocationMarker=mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));

        if(googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);

        }


    }

    public void onClick(View v)
    {
        String hospital="hospital",school="school",restaurant="restaurant";

        Object transferData[]=new Object[2];
        GetNearbyPlaces getNearbyPlaces=new GetNearbyPlaces();


        switch (v.getId())
        {
            case R.id.search:
                EditText addressField=findViewById(R.id.location_search);
                String address=addressField.getText().toString();

                List<Address> addressList=null;
                MarkerOptions userMarkerOptions=new MarkerOptions();

                if(!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder=new Geocoder(this);
                    try {
                        addressList=geocoder.getFromLocationName(address,6);

                        if(addressList!=null)
                        {

                            for(int i=0;i<addressList.size();i++)
                            {
                                Address addressuser=addressList.get(i);
                                LatLng latLng=new LatLng(addressuser.getLatitude(),addressuser.getLongitude());

                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                mMap.addMarker(userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                        else {
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        }


                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                else {
                    Toast.makeText(this, "Please enter locatoion", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.hospitals:
                mMap.clear();
                String url=getUrl(latitude,longitude,hospital);
                transferData[0]=mMap;
                transferData[1]=url;
                getNearbyPlaces.execute(transferData);

                Toast.makeText(this, "Searching for nearby hospitals..", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearby hospitals..", Toast.LENGTH_SHORT).show();

                break;


            case R.id.schools:
                mMap.clear();
                url=getUrl(latitude,longitude,school);
                transferData[0]=mMap;
                transferData[1]=url;
                getNearbyPlaces.execute(transferData);

                Toast.makeText(this, "Searching for nearby schools..", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearby schools..", Toast.LENGTH_SHORT).show();

                break;

            case R.id.restaurants:
                mMap.clear();
                url=getUrl(latitude,longitude,hospital);
                transferData[0]=mMap;
                transferData[1]=url;
                getNearbyPlaces.execute(transferData);

                Toast.makeText(this, "Searching for nearby Restaurants..", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearby restaurants..", Toast.LENGTH_SHORT).show();

                break;

        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googleURL=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");

        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyA03Bx-otd6PEM-p29ZzXNGhVrY6PbOny8");

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);


        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
