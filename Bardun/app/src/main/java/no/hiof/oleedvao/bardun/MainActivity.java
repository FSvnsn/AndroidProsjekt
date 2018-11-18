package no.hiof.oleedvao.bardun;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import no.hiof.oleedvao.bardun.adapter.PlaceAutoCompleteAdapter;
import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        TeltplassQuickviewBottomSheetDialog.BottomSheetListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Batman";
    private GoogleMap mMap;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView mTextView;
    private ConstraintLayout nyTeltplassHer;

    private AutoCompleteTextView mSearchInput;
    private PlaceAutoCompleteAdapter mplaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private ImageButton imageButtonFilter;

    //Location and permissions vars
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    private Marker geomarker;

    //filter vars
    private ArrayList mSelectedItems = new ArrayList();
    private String[] filterItems;
    boolean[] checkedItems;
    private boolean skog;
    private boolean fjell;
    private boolean fiske;

    List<Marker> markers = new ArrayList<Marker>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    private ImageButton imageBtnMyLoc;
    private String CHANNEL_ID = "default";
    private static Marker mNyTeltplass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Views
        setContentView(R.layout.activity_main);
        nyTeltplassHer = findViewById(R.id.nyTeltplassHer);
        nyTeltplassHer.setVisibility(View.GONE);
        mSearchInput = findViewById(R.id.searchInput);
        toolbar = findViewById(R.id.toolbarMain);
        imageButtonFilter = findViewById(R.id.imageBtnFilter);
        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
        imageBtnMyLoc = findViewById(R.id.imageBtnMyLoc);

        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();

        try{
            UID = CUser.getUid();
        }
        catch(NullPointerException e){
            Toast.makeText(this, "Du er ikke logget inn!", Toast.LENGTH_LONG).show();
        }

        createNotificationChannel();
        setUpNavigationDrawer();

        //Innhenter maps og sier fra når det er klart
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Sjekker og håndterer tilgang til location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates
                    (LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            final LatLng geolatLng = new LatLng(latitude, longitude);
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
                                String geoStedsnavn = list.get(0).getLocality();
                                Log.d(TAG, "Network provider before 1.geomarker = " + geomarker);
                                if (geomarker != null) {
                                    geomarker.remove();
                                }
                                geomarker = mMap.addMarker(new MarkerOptions()
                                                .position(geolatLng)
                                                .title(geoStedsnavn)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_geo_location))
                                );
                                Log.d(TAG, "Network provider after 1.geomarker = " + geomarker);

                                imageBtnMyLoc.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(geolatLng));
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(geolatLng, 15, 0, 0)));
                                    }
                                });
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
                    });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    final LatLng geolatLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
                        String geoStedsnavn = list.get(0).getLocality();
                        Log.d(TAG, "GPS provider before 1.geomarker = " + geomarker);
                        if (geomarker != null) {
                            geomarker.remove();
                        }
                        geomarker = mMap.addMarker(new MarkerOptions()
                                        .position(geolatLng)
                                        .title(geoStedsnavn)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_geo_location))
                        );
                        Log.d(TAG, "GPS provider after 1.geomarker = " + geomarker);
                        imageBtnMyLoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(geolatLng));
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(geolatLng, 15, 0, 0)));
                            }
                        });

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
            });
        }
    }

    private void initSearch() {

        // region mapSetup
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        final LatLngBounds NORGE = new LatLngBounds(
                new LatLng(57.931883, 0.162047), new LatLng(67.786666, 18.441137));

        mplaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,
                NORGE, null);

        mSearchInput.setAdapter(mplaceAutoCompleteAdapter);

        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    getSearchResults();
                }
                return false;
            }
        });
    }

    private void getSearchResults() {
        //Hent geolovcation for results
        String mSearchString = mSearchInput.getText().toString();
        Toast.makeText(this, "Du søkte: " + mSearchString.toString(), Toast.LENGTH_SHORT).show();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(mSearchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            double inputAddressLat = address.getLatitude();
            double inputAddressLon = address.getLongitude();
            LatLng inputLatLong = new LatLng(inputAddressLat, inputAddressLon);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(inputLatLong));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(inputLatLong, 14, 0, 0)));


            Log.d(TAG, "Location funnet!: " + address.toString());
            Toast.makeText(this, address.getLocality().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // region mapSetup og henting fra database
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Toast.makeText(this, "Trykk lenge på det stedet i kartet du vil opprette en teltplass", Toast.LENGTH_LONG).show();


        //Skaffer data fra Firebase og plasserer markører
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Viser notifikasjon for hvor lenge siden sist bruker har oprettet en teltplass
                if(CUser != null){
                    showNotification(dataSnapshot);
                }

                //Henter teltplasser fra Firebase og lager markører for hver i kartet
                showMarkers(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });
        final LatLngBounds NORGE = new LatLngBounds(
                new LatLng(57.931883, 0.162047), new LatLng(67.786666, 18.441137));

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(NORGE, 0));

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        setUpUISettings();
        initSearch();

    }

    private void showNotification(DataSnapshot dataSnapshot) {
        Teltplass sisteTeltplass = new Teltplass();

        Calendar calendar1 = Calendar.getInstance();
        String currentDate = calendar1.getTime().toString();
        String lastDate = "Fri Nov 16 13:41:24 GMT+01:00 2017";

        //Toast.makeText(this, currentDate, Toast.LENGTH_LONG).show();


        for (DataSnapshot ds : dataSnapshot.child("mineTeltplasser").child(UID).getChildren()){
            String tempDate = (ds.child("timeStamp").getValue(String.class));
            if(tempDate != null){
                if (tempDate.compareTo(lastDate) >= 0){
                    lastDate = tempDate;
                }
            }
        }

        String textTitle = "Klar for en ny telttur?";
        String textContent = "Du har ikke lagd en ny teltplass siden " + lastDate + ". Kanskje på tide å lage en ny?";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_minefavoritter)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textContent));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, mBuilder.build());
    }

    private void showMarkers(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.child("teltplasser").getChildren()){
            String name = ds.child("navn").getValue(String.class);
            String location = ds.child("latLng").getValue(String.class);

            // Testing fra gruppemøte med Remi og Marius :))
            // Legg i ArrayList
            String test = ds.getKey();
            // Teltplass test1 = ds.getValue(Teltplass.class);


            location = location.replace("p", ".");
            location = location.replace("k", ",");

            //Toast.makeText(this, location, Toast.LENGTH_LONG).show();

            String fullLoc [] = location.split(",");
            double latitude = Double.parseDouble(fullLoc[0]);
            double longitude = Double.parseDouble(fullLoc[1]);

            LatLng currLoc = new LatLng(latitude, longitude);

            Marker m = mMap.addMarker(new MarkerOptions()
                            .position(currLoc)
                            .title(name)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_teltplass_marker_green))
            );
            m.setTag(test);
            //Log.d(TAG, " key: " + m.getTag());

        }
    }

    private void filterDialog() {
        filterItems = getResources().getStringArray(R.array.filter_items);
        checkedItems = new boolean[filterItems.length];
        for (int i = 0; i < checkedItems.length;i++) {
            if (mSelectedItems.contains(i)) {
                checkedItems[i] = true;
            }
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Filtrer teltplasser");

        mBuilder.setMultiChoiceItems(filterItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    mSelectedItems.add(which);
                } else if (mSelectedItems.contains(which)) {
                    mSelectedItems.remove(Integer.valueOf(which));
                }
            }
        });

        mBuilder.setPositiveButton("Ferdig", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = "";
                for (int i = 0; i < mSelectedItems.size(); i++) {
                    item = filterItems[(int) mSelectedItems.get(i)];
                    if(item.equals("Skog")) {
                        Log.d(TAG,"Skog: " + item.toString());
                        skog = true;
                    }
                    else if(item.equals("Fjell")) {
                        Log.d(TAG,"Fjell: " + item.toString());
                        fjell = true;
                    }
                    else if(item.equals("Fiske")) {
                        Log.d(TAG,"Fiske: " + item.toString());
                        fiske = true;
                    }
                }
                Toast.makeText(MainActivity.this, "Teltplasser filtrert", Toast.LENGTH_SHORT).show();
                filterMarkers(skog, fjell,fiske);
            }
        });
        mBuilder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Avbryt klikket", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        mBuilder.setNeutralButton("Tøm filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Tøm filter klikket", Toast.LENGTH_SHORT).show();
                mSelectedItems.clear();
                skog = false;
                fjell = false;
                fiske = false;
                filterMarkers(skog, fjell,fiske);
            }
        });

        mBuilder.show();
    }

    private void filterMarkers(final Boolean fSkog, final Boolean fFjell, final Boolean fFiske){
        //Fjerner alle markers
        mMap.clear();
        markers.clear();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Oppretter markers
                for(DataSnapshot ds : dataSnapshot.child("teltplasser").getChildren()){

                    Boolean meetsRequirements = true;

                    String name = ds.child("navn").getValue(String.class);
                    String location = ds.child("latLng").getValue(String.class);
                    Boolean skog = ds.child("skog").getValue(Boolean.class);
                    Boolean fjell = ds.child("fjell").getValue(Boolean.class);
                    Boolean fiske = ds.child("fiske").getValue(Boolean.class);

                    location = location.replace("p", ".");
                    location = location.replace("k", ",");

                    String fullLoc [] = location.split(",");
                    double latitude = Double.parseDouble(fullLoc[0]);
                    double longitude = Double.parseDouble(fullLoc[1]);

                    LatLng currLoc = new LatLng(latitude, longitude);
                    if(fSkog.equals(true) && !skog.equals(true)){
                        meetsRequirements = false;
                    }
                    if(fFjell.equals(true) && !fjell.equals(true)){
                        meetsRequirements = false;
                    }
                    if(fFiske.equals(true) && !fiske.equals(true)){
                        meetsRequirements = false;
                    }
                    if(meetsRequirements.equals(true)){
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(currLoc)
                                .title(name)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_teltplass_marker_green)));
                        marker.setTag(location); //Må muligens se nærmere på
                        markers.add(marker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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
    public void onMapLongClick(final LatLng latLng) {

        // TODO: Bytt registrer-teltplass-ikon
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();

        if(CUser == null) {
            Toast.makeText(MainActivity.this,
                    "Du må logge inn for å opprette teltplass", Toast.LENGTH_LONG).show();
        }
        else {
            mNyTeltplass = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Ny teltplass")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_opprett_teltplass))
                    .draggable(true)
            );
            mNyTeltplass.setDraggable(true);

            double lat = mNyTeltplass.getPosition().latitude;
            double lng = mNyTeltplass.getPosition().longitude;
            String latString = String.valueOf(lat);
            String lngString = String.valueOf(lng);
            String latlngString = latString + "," + lngString;
            Log.d(TAG, "latlng: " + latlngString);


            Bundle bundleRegistrer = new Bundle();
            bundleRegistrer.putString("latlong", latlngString);
            bundleRegistrer.putString("tittel", "Ny teltplass her?");

            OpprettTeltplassBottomSheetDialog bottomSheetRegistrer = new OpprettTeltplassBottomSheetDialog();
            bottomSheetRegistrer.show(getSupportFragmentManager(), "teltplassBottomSheetRegistrer");

            bottomSheetRegistrer.setArguments(bundleRegistrer);
        }

    }

    // endregion

    // region markerEvents
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng mPos = marker.getPosition();

        try {
            if (geomarker != null) {
                LatLng geoPos = geomarker.getPosition();

                if (mPos.equals(geoPos)) {
                    Toast.makeText(this, "Du har trykket på din egen posisjon", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        catch (NullPointerException e) {
            Log.d(TAG, "Geomarker position not found");
        }
        //Åpner Bottom Sheet med OpprettTeltplass
        String nyteltplass = "Ny teltplass";

        if (marker.getTitle().equals(nyteltplass)) {
            Log.d(TAG, "ny teltplass marker er klikket på");
            double lat = marker.getPosition().latitude;
            double lng = marker.getPosition().longitude;
            String latString = String.valueOf(lat);
            String lngString = String.valueOf(lng);
            String latlngString = latString + "," + lngString;
            Log.d(TAG, "latlng: " + latlngString);


            Bundle bundleRegistrer = new Bundle();
            bundleRegistrer.putString("latlong", latlngString);
            bundleRegistrer.putString("tittel", "Ny teltplass her?");

            OpprettTeltplassBottomSheetDialog bottomSheetRegistrer = new OpprettTeltplassBottomSheetDialog();
            bottomSheetRegistrer.show(getSupportFragmentManager(), "teltplassBottomSheetRegistrer");

            bottomSheetRegistrer.setArguments(bundleRegistrer);

        }
        else {
            Log.d(TAG, "eksisterende teltplass er klikket på");

            //Hent marker position og konverter til string
            String pos = marker.getPosition().toString();
            String name = marker.getTitle();
            String id = (String) marker.getTag();

            Bundle bundle = new Bundle();
            bundle.putString("latlong",pos);
            bundle.putString("tittel",name);
            bundle.putString("brukernavn", "Caroline");
            bundle.putString("dato", "14.01.2014");
            bundle.putString("id",id);


            TeltplassQuickviewBottomSheetDialog bottomSheet = new TeltplassQuickviewBottomSheetDialog();
            bottomSheet.show(getSupportFragmentManager(), "teltplassBottomSheet");

            bottomSheet.setArguments(bundle);
        }

        return false;

    }
    @Override
    public void onButtonClicked(String text) {
        // Når visTeltplass-knapp inni bottom sheet er trykket skjer dette:
        //TODO: Åpne teltplass-activity her og send med teltplass-ID
        mTextView = findViewById(R.id.visTeltplassKlikk);
        mTextView.setText(text);
        Log.d(TAG, "onButtoncliked");

    }
    // endregion

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerMain);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMain);
        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }


    @Override
    public boolean onMyLocationButtonClick() {

        mMap.moveCamera(CameraUpdateFactory.newLatLng(geomarker.getPosition()));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(geomarker.getPosition(), 15, 0, 0)));

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static Marker getmNyTeltplass(){
        return mNyTeltplass;
    }
}