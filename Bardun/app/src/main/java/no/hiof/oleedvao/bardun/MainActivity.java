package no.hiof.oleedvao.bardun;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.List;
import java.util.zip.Inflater;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        TeltplassQuickviewBottomSheetDialog.BottomSheetListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "Batman";
    private GoogleMap mMap;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView mTextView;
    private ConstraintLayout nyTeltplassHer;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    LocationListener locationListener;
    private Marker marker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nyTeltplassHer = findViewById(R.id.nyTeltplassHer);
        nyTeltplassHer.setVisibility(View.GONE);

        toolbar = findViewById(R.id.toolbarMain);
        setUpNavigationDrawer();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        /*
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality() + ":";
                    result += addresses.get(0).getCountryName();
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (marker != null) {
                        marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        mMap.setMaxZoomPreference(20);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                    } else {
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        mMap.setMaxZoomPreference(20);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21.0f));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
*/
    }

    // region mapSetup
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add static markers for testing
        LatLng remmen = new LatLng(59.1291473, 11.3506091);
        LatLng fredrikstad = new LatLng(59.21047628, 10.93994737);

        Marker remmenT = mMap.addMarker(new MarkerOptions()
                .position(remmen)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_teltplass_marker_green))
        );
        //Tag kan inneholde et objekt, f.eks et teltplassobjekt?
        remmenT.setTag("remmen");

        Marker fredT = mMap.addMarker(new MarkerOptions()
                .position(fredrikstad)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_teltplass_marker_green))
        );
        fredT.setTag("fred");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(remmen));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(remmen, 15, 0, 0)));

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        setUpUISettings();

    }


    private void setUpUISettings() {
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        // TODO: Lage bottomsheet(?) for registrering av teltplass steg 1. "Vil du lage ny teltplass her?"
        // TODO: Vis koordinater, hent stedsnavn
        // TODO: Legg til registrer-teltplass-ikon

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Ny teltplass")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_teltplass_marker_green))
        );
        nyTeltplassHer.setVisibility(View.VISIBLE);
        TextView nyTeltplassLatLong = findViewById(R.id.latlongTextview);
        nyTeltplassLatLong.setText(latLng.toString());

    }
    // endregion

    // region markerEvents
    @Override
    public boolean onMarkerClick(Marker marker) {
        //Åpner Bottom Sheet med Teltplass Quickview
        //TODO: Håndtere Teltplass-info om hver marker her
            Log.d(TAG, "onMarkerClick runs + " + marker.getTag());

        TeltplassQuickviewBottomSheetDialog bottomSheet = new TeltplassQuickviewBottomSheetDialog();
        bottomSheet.show(getSupportFragmentManager(), "teltplassBottomSheet");
        return false;
    }
    @Override
    public void onButtonClicked(String text) {
        // Når visTeltplass-knapp inni bottom sheet er trykket skjer dette:
        //TODO: Åpne teltplass-activity her og send med teltplass-ID
        mTextView = findViewById(R.id.visTeltplassKlikk);
        mTextView.setText(text);
    }
    // endregion

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerMain);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMain);
        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
