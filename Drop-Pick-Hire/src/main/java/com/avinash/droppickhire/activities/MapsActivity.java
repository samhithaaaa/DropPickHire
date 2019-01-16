package com.avinash.droppickhire.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.adapters.GooglePlacesAutocompleteAdapter;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.Preferences;
import com.avinash.droppickhire.pojo.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private GoogleMap mMap;

    private Boolean mLocationPermissionsGranted = false;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @BindView(R.id.src_edt) MaterialEditText srcEdt;

    @BindView(R.id.dst_lst) ListView dstLst;

    private LatLng srcLatLon;

    private LatLng dstLatLon;

    private GoogleApiClient mGoogleApiClient;

    private GooglePlacesAutocompleteAdapter srcAdapter;

    private String placeId = "";

    private boolean isRecruiter = false;
    private String locationName= "";

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, 0, this)
                .build();

        Intent intent = getIntent();
        isRecruiter = intent.getBooleanExtra(Constants.IS_RECRUITER, false);

        srcEdt.requestFocus();
        setSrcAdapter(srcAdapter, dstLst, srcEdt);

        getLocationPermission();
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_maps;
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                mapFragment.getMapAsync(this);
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));

        mMap.getUiSettings().setCompassEnabled(false);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            srcLatLon = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            CameraPosition myLocation = new CameraPosition.Builder()
                                    .target(srcLatLon)
                                    .zoom(Constants.MAP_ZOOM_LEVEL).build();

                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(myLocation));

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void setSrcAdapter(GooglePlacesAutocompleteAdapter adapter, final ListView listView, final MaterialEditText edt) {
        adapter = new GooglePlacesAutocompleteAdapter(MapsActivity.this, R.layout.item_lst);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        final GooglePlacesAutocompleteAdapter finalAdapter = adapter;
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                finalAdapter.getFilter().filter(s.toString());
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final GooglePlacesAutocompleteAdapter finalAdapter1 = adapter;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                placeId = finalAdapter1.getPlaceId(position);
                setDstLatLon(placeId);
                edt.setText(finalAdapter1.getMainText(position));
                edt.setSelection(edt.getText().length());
                listView.setVisibility(View.GONE);
                locationName = finalAdapter1.getMainText(position);
            }
        });
    }

    private void setDstLatLon(String placeId) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            dstLatLon = myPlace.getLatLng();

                            CameraPosition myLocation = new CameraPosition.Builder()
                                    .target(dstLatLon)
                                    .zoom(Constants.MAP_ZOOM_LEVEL).build();

                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(myLocation));
                            mMap.addMarker(new MarkerOptions()
                                    .position(dstLatLon));

                            mMap.setOnMarkerClickListener(MapsActivity.this);
                        }
                        places.release();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // ToDo add failed cases here
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent();
        if(isRecruiter) {
            if(dstLatLon != null) {
                intent.putExtra(Constants.DST_LAT_LON, dstLatLon);
                intent.putExtra(Constants.NAME, locationName);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        } else {
            if(dstLatLon != null && srcLatLon != null) {
                intent.putExtra(Constants.DST_LAT_LON, dstLatLon);
                intent.putExtra(Constants.SRC_LAT_LON, srcLatLon);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        }
        setResult(RESULT_OK, intent);
        finish();
        return false;
    }
}
