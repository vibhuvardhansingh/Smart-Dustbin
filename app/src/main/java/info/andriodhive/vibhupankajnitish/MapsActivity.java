package info.andriodhive.vibhupankajnitish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    private static String TAG = MapsActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 5000;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mLocationProviderClient;
    private LatLng mCenterLatLong;

    private AddressResultReceiver mResultReceiver;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStreetOutput;

    private String Empty = "Dustbin Empty";
    private String Full = "Dustbin Full";
    private Boolean fillRamanujan/* = true*/;
    private Boolean fill;
//    private Boolean fillAryabhatta/* = false*/;
    private Boolean fillG14/* = false*/;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null)
                changeMap(location);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mLocationProviderClient = new FusedLocationProviderClient(this);
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        if (checkPlayServices()) {
            if (!AppUtils.isLocationEnabled(this)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraIdleListener(() -> {
            mCenterLatLong = mMap.getCameraPosition().target;
            Location mLocation = new Location("");
            mLocation.setLatitude(mCenterLatLong.latitude);
            mLocation.setLongitude(mCenterLatLong.longitude);

        });
        LatLng ramanujan = new LatLng(25.2630417,82.9843548);
        Marker mRamanujan = mMap.addMarker(new MarkerOptions().
                position(ramanujan).
                title("Dustbin at Ramanujan").
                snippet(Empty).
                alpha(.7f).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        LatLng aryabhatta = new LatLng(25.2641908,82.9846001);
        Marker mAryabhatta = mMap.addMarker(new MarkerOptions().
                position(aryabhatta).
                title("Dustbin at Aryabhatta").
                snippet(Empty).
                alpha(.7f).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        LatLng g14 = new LatLng(25.261720, 82.990587);
        Marker mg14 = mMap.addMarker(new MarkerOptions().
                position(g14).
                title("Dustbin at G14").
                snippet(Empty).
                alpha(.7f).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.setOnInfoWindowClickListener(this);

        fillRamanujan = getIntent().getBooleanExtra("data",true);

        if(fillRamanujan){
            mRamanujan.remove();
            mMap.addMarker(new MarkerOptions().position(ramanujan).
                    title("Dustbin at ramanujan").
                    snippet(Full).
                    alpha(.7f).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
//        else return;
        fill = getIntent().getBooleanExtra("dataOne",true);
        if (fill){
            mAryabhatta.remove();
            mMap.addMarker(new MarkerOptions().
                    position(aryabhatta).
                    title("Dustbin at aryabhatta").
                    snippet(Full).
                    alpha(.7f).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
//        else return;
        fillG14 = getIntent().getBooleanExtra("dataTwo",true);
        if(fillG14){
            mg14.remove();
            mMap.addMarker(new MarkerOptions().
                     position(g14).
                     title("Dustbin at G14").
                      snippet(Full).
                      alpha(.7f).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        else return;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
            return;
        }
        goToCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void goToCurrentLocation() {
        mMap.setMyLocationEnabled(true);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Google API Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to connect", Toast.LENGTH_SHORT).show();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            mLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "Google Play services not usable: " + resultCode);
                finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {
        Log.d(TAG, "Reaching map" + mMap);
        // check if map is created successfully or not
        if (mMap != null) {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            LatLng latLong = new LatLng(lat, lng);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong)
                    .zoom(18f)
                    .tilt(70)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
                mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
                mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
                mStreetOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
                Log.i(TAG, "Add: " + mAddressOutput + ", Area: " + mAreaOutput + ", City: " + mCityOutput + ", Street: " + mStreetOutput);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLong = place.getLatLng();
                Location location = new Location("");
                location.setLatitude(latLong.latitude);
                location.setLongitude(latLong.longitude);
                changeMap(location);
            }
        } else {
            Log.e(TAG, PlaceAutocomplete.getStatus(this, data).getStatusMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                goToCurrentLocation();
        }
    }
}
