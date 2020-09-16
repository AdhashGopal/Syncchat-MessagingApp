package com.chatapp.synchat.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.PlacesRecyclerViewAdapter;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GoogleMapView extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "GoogleMapView";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private LocationManager locationManager;
    private final float DEFAULT_ZOOM = 15;
    private String provider;
    private Location mlocation;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    List<Place.Field> placeFields, placeFields1;
    String latLngString;

    LatLng latLng;
    LatLng autoLatLng;
    LatLng DynamicmidLatLng;

    boolean autoPicker = false;
    boolean dynaicValue = false;
    RecyclerView recyclerView;
    LinearLayout selectLocation;
    PlacesRecyclerViewAdapter recyclerViewAdapter;
    List<Place> placesList;
    private ProgressDialog dialog;


    /**
     * binding google map components
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
            getSupportActionBar().hide(); // hide the title bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
            setContentView(R.layout.googlemapview);
            dialog = new ProgressDialog(this);
            dialog.setMessage("Fetching the location, please wait.");
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            selectLocation = (LinearLayout) findViewById(R.id.selectLocation);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                Log.e("", "");
            }


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapView = mapFragment.getView();

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GoogleMapView.this);
            Places.initialize(GoogleMapView.this, getString(R.string.google_api_key));
            placesClient = Places.createClient(this);
            final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


            permissions.add(ACCESS_FINE_LOCATION);
            permissions.add(ACCESS_COARSE_LOCATION);

            permissionsToRequest = findUnAskedPermissions(permissions);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (permissionsToRequest.size() > 0)
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                else {
                    fetchLocation();
                }
            } else {
                fetchLocation();
            }

            recyclerView = (RecyclerView) findViewById(R.id.nearbyplc);

            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerViewAdapter = new PlacesRecyclerViewAdapter(GoogleMapView.this);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
            // Specify the fields to return.
            //List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            placeFields = new ArrayList<>();
            placeFields.add(Place.Field.NAME);
            Log.e("placeFields", placeFields.toString());
            //GetData(placeFields);


            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragment.setCountry("IN");


            selectLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            Geocoder geocoder;
                            List<Address> addresses;
                            String address = "";
                            geocoder = new Geocoder(GoogleMapView.this, Locale.getDefault());
                            try {
                                if (autoPicker) {
                                    addresses = geocoder.getFromLocation(autoLatLng.latitude, autoLatLng.longitude, 1);
                                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    LatLng latLng = new LatLng(autoLatLng.latitude, autoLatLng.longitude);
                                    GoogleMapData data = new GoogleMapData();
                                    data.setAddress(address);
                                    data.setBitmap(bitmap);
                                    data.setLatLng(latLng);
                                    data.setName(address);
                                    EventBus.getDefault().post(data);
                                    finish();
                                } else if (dynaicValue) {
                                    addresses = geocoder.getFromLocation(DynamicmidLatLng.latitude, DynamicmidLatLng.longitude, 1);
                                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    LatLng latLng = new LatLng(DynamicmidLatLng.latitude, DynamicmidLatLng.longitude);
                                    GoogleMapData data = new GoogleMapData();
                                    data.setAddress(address);
                                    data.setBitmap(bitmap);
                                    data.setLatLng(latLng);
                                    data.setName(address);
                                    EventBus.getDefault().post(data);
                                    finish();
                                } else {
                                    if (mlocation != null) {
                                        assert location != null;
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        address = addresses.get(0).getAddressLine(0);
                                        LatLng latLng = new LatLng(latitude, longitude);
                                        GoogleMapData data = new GoogleMapData();
                                        data.setAddress(address);
                                        data.setBitmap(bitmap);
                                        data.setLatLng(latLng);
                                        data.setName(address);
                                        EventBus.getDefault().post(data);
                                        finish();
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

            });
            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NotNull final Place place) {
                    // TODO: Get info about the selected place.
                    //List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);
                    //List<Place.Field> plc = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                    placeFields1 = new ArrayList<>();
                    placeFields1.add(Place.Field.NAME);
                    // GetData(placeFields1);
                    Log.e("placeFields1", placeFields.toString());
                    Log.i(TAG, "Place0: " + place.getName() + ", " + place.getLatLng());
                    Geocoder geocoder;
                    try {
                        geocoder = new Geocoder(GoogleMapView.this, Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocationName(place.getName(), 1);
                        if (addressList != null && addressList.size() > 0) {
                            double lat = addressList.get(0).getLatitude();
                            double lng = addressList.get(0).getLongitude();
                            Log.e("LATLNG", lat + "," + lng);
                            String latlng = String.valueOf(lat + "," + lng);
                            locationCall(latlng);
                            autoPicker = true;
                            autoLatLng = new LatLng(lat, lng);
                            LatLng sydney = new LatLng(lat, lng);
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions()
                                    .position(sydney));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), DEFAULT_ZOOM));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull Status status) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @SuppressLint("MissingPermission")
    private void GetData(final List<Place.Field> placeFields) {
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener((new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse response) {
                    placesList = new ArrayList<Place>();
                    placesList.clear();
                    for (com.google.android.libraries.places.api.model.PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        placesList.add((Place) placeLikelihood.getPlace());
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getAddress(),
                                placeLikelihood.getLikelihood()));
                    }
                    if (placesList.size() != 0) {

                    }
                }
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            //getLocationPermission();
        }


/*// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            //getLocationPermission();
        }*/
    }

    /**
     * Start eventbus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop eventbus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Get the eventbus value
     *
     * @param event based on latlng shown popup
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(final LatLng event) {
        try {
            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    LatLng location = new LatLng(event.latitude, event.longitude);

                    Geocoder geocoder;
                    List<Address> addresses;
                    String address = "";
                    geocoder = new Geocoder(GoogleMapView.this, Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(event.latitude, event.longitude, 1);
                        address = addresses.get(0).getAddressLine(0);
                        GoogleMapData data = new GoogleMapData();
                        data.setAddress(address);
                        data.setBitmap(bitmap);
                        data.setLatLng(location);
                        data.setName(address);
                        EventBus.getDefault().post(data);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * fetch the current location & view static marker
     */
    private void fetchLocation() {

        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        latLngString = location.getLatitude() + "," + location.getLongitude();
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.e("latLngString", latLngString);
                        if (latLng != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng));
                        }
                        locationCall(latLngString);
                        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            public void onCameraChange(CameraPosition cameraPosition) {
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(cameraPosition.target));
                            }
                        });

                        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                //get latlng at the center by calling
                                DynamicmidLatLng = mMap.getCameraPosition().target;
                                dynaicValue = true;
                                String dynamicLatLngValue = DynamicmidLatLng.latitude + "," + DynamicmidLatLng.longitude;
                                Log.e("midLatLng", latLngString + "");
                                if (!latLngString.equalsIgnoreCase(dynamicLatLngValue)) {
                                    locationCall(dynamicLatLngValue);
                                }

                            }
                        });
                    }
                });
    }


    /**
     * add permission list
     *
     * @param wanted list of array permission
     * @return value
     */
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * check the permission grant or not
     *
     * @param permission type of permission
     * @return value
     */
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    /**
     * check permission
     *
     * @return value
     */
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**
     * complete flow of google map
     *
     * @param googleMap initialization of google map
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(GoogleMapView.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(GoogleMapView.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(GoogleMapView.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(GoogleMapView.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * get action of activity result
     *
     * @param requestCode action requestCode
     * @param resultCode  action resultCode
     * @param data        value
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    /**
     * get device location
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                Log.e("mLastKnownLocation", mLastKnownLocation + "");
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        Log.e("mLastKnownLocation1", mLastKnownLocation + "");
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(GoogleMapView.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * updated location
     *
     * @param location latlng
     */
    @Override
    public void onLocationChanged(Location location) {
        mlocation = location;

    }

    /**
     * download for location url
     *
     * @param strUrl pass string value
     * @return value
     * @throws IOException error
     */
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Call location API
     *
     * @param latLngString latlng value in string
     */
    private void locationCall(String latLngString) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + latLngString);
        sb.append("&radius=4000");
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyCxCprNGQpecOfQN0pCu2nxzUmBoMSA8no");
        // Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();
        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    /**
     * A class, to download Google Places in AsyncTask
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format in AsyncTask
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                recyclerViewAdapter.updateData(list);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}