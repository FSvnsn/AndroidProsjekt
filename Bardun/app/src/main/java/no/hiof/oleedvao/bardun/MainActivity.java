package no.hiof.oleedvao.bardun;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.List;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "Batman";
    private GoogleMap mMap;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbarBruker);
        setUpNavigationDrawer();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try {
            if (mapFragment != null) {

                Log.e(TAG, "Google maps not null");
            }
            else{
                Log.d(TAG, "Google Maps Is Null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Google maps not loaded");
        }
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng remmen = new LatLng(59.1291473, 11.3506091);
        LatLng fredrikstad = new LatLng(59.21047628, 10.93994737);
        mMap.addMarker(new MarkerOptions().position(remmen).title("Marker Remmen"));
        mMap.addMarker(new MarkerOptions().position(fredrikstad).title("Marker Fredrikstad"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(remmen));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(remmen, 15, 0, 0)));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        //mMap.setOnMapLongClickListener(this);

        setUpUISettings();

    }

    private void setUpUISettings() {
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setCompassEnabled(true);
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerBruker);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutBruker);
        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
