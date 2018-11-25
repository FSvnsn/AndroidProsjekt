package no.hiof.oleedvao.bardun.teltplass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.bruker.VisBrukerActivity;
import no.hiof.oleedvao.bardun.adapter.ViewPagerAdapter;
import no.hiof.oleedvao.bardun.fragment.BeskrivelseFragment;
import no.hiof.oleedvao.bardun.fragment.KommentarerFragment;
import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class TeltplassActivity extends AppCompatActivity {
    //Globale variabler
    final long ONE_MEGABYTE = 1024 * 1024;
    private boolean favoritt = false;

    //toolbar
    private android.support.v7.widget.Toolbar toolbar;

    //database relaterte variabler
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;
    private String teltplassId;
    private String teltplassUID;

    //Views
    private ImageView imageViewTeltplass;
    private TextView textViewTeltplassNavn;
    private ImageButton imageButtonThumbsUp;
    private ImageButton imageButtonThumbsDown;
    private ImageButton imageButtonKommentar;
    private ImageButton imageButtonFavoritt;
    private TextView textViewTeltplassUnderlag;
    private TextView textViewTeltplassUtsikt;
    private TextView textViewTeltplassAvstand;
    private TextView textViewBrukerNavn;
    private Switch switchTeltplassSkog;
    private Switch switchTeltplassFjell;
    private Switch switchTeltplassFiske;
    private TextView textViewTeltplassTimeStamp;
    private Button buttonEditTeltplass;

    //Tab variabler
    private TabLayout tabLayoutTeltplass;
    private ViewPager viewPagerTeltplass;
    private ViewPagerAdapter adapter;
    private BeskrivelseFragment beskrivelseFragment;
    private KommentarerFragment kommentarerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //layout
        setContentView(R.layout.activity_teltplass);

        //toolbar
        toolbar = findViewById(R.id.toolbarTeltplass);
        setUpNavigationDrawer();

        //instantiatierer views
        imageViewTeltplass = findViewById(R.id.imageViewTeltplass);
        textViewTeltplassNavn = findViewById(R.id.textViewTeltplassNavn);
        imageButtonThumbsUp = findViewById(R.id.imageButtonThumbsUp);
        imageButtonThumbsDown = findViewById(R.id.imageButtonThumbsDown);
        imageButtonKommentar = findViewById(R.id.imageButtonKommentar);
        imageButtonFavoritt = findViewById(R.id.imageButtonFavoritt);
        textViewTeltplassUnderlag = findViewById(R.id.textViewTeltplassUnderlag);
        textViewTeltplassUtsikt = findViewById(R.id.textViewTeltplassUtsikt);
        textViewTeltplassAvstand = findViewById(R.id.textViewTeltplassAvstand);
        textViewBrukerNavn = findViewById(R.id.textViewBrukerNavn);
        switchTeltplassSkog = findViewById(R.id.switchTeltplassSkog);
        switchTeltplassFjell = findViewById(R.id.switchTeltplassFjell);
        switchTeltplassFiske = findViewById(R.id.switchTeltplassFiske);
        textViewTeltplassTimeStamp = findViewById(R.id.textViewTeltplassTimeStamp);
        buttonEditTeltplass = findViewById(R.id.buttonLagreTeltplassEndringer);

        //Instansierer tab elementer
        tabLayoutTeltplass = findViewById(R.id.TabLayoutTeltplass);
        viewPagerTeltplass = findViewById(R.id.ViewPagerTeltplass);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        beskrivelseFragment = new BeskrivelseFragment();
        kommentarerFragment = new KommentarerFragment();

        //Henter teltplassId fra forrige Activity
        teltplassId = getIntent().getExtras().getString("Id");

        //Sender teltplass id til tab fragmenter for å sørge for at hva som vises er relevant for teltplassen
        Bundle bundle = new Bundle();
        bundle.putString("teltplassId", teltplassId);
        beskrivelseFragment.setArguments(bundle);
        kommentarerFragment.setArguments(bundle);

        //Legger til fragmenter i view pager
        adapter.AddFragment(beskrivelseFragment,"Beskrivelse");
        adapter.AddFragment(kommentarerFragment,"Kommentarer");
        viewPagerTeltplass.setAdapter(adapter);
        tabLayoutTeltplass.setupWithViewPager(viewPagerTeltplass);

        if(isConnectedToInternet()){
            //Instansierer database variabler
            mDatabase = FirebaseDatabase.getInstance();
            mDatabaseRef = mDatabase.getReference();
            mStorage = FirebaseStorage.getInstance();
            mStorageReference = mStorage.getReference();
            mAuth = FirebaseAuth.getInstance();
            CUser = mAuth.getCurrentUser();

            //Henter bruker id for innloggede bruker (hvis logget inn)
            try
            {
                UID = CUser.getUid();
            }
            catch(NullPointerException e){
                e.printStackTrace();
            }

            //Henter bruker id for brukeren som opprettet den relevante teltplassen
            mDatabaseRef.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    teltplassUID = dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUID();
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }

            });
        }
        else{
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }

        //On click for å trykke på favoritt knapp
        imageButtonFavoritt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Hent fra database
                addToFavoritter(v);
                if (favoritt == false) {
                    imageButtonFavoritt.setImageResource(R.drawable.ic_favorite_checked);
                    favoritt = true;
                }
                else if (favoritt == true) {
                    imageButtonFavoritt.setImageResource(R.drawable.ic_favorite_unchecked);
                    favoritt = false;
                }
            }
        });
    }

    //Metode for å hente data fra database og vise det frem i views
    private void showData(DataSnapshot dataSnapshot) {
        if(isConnectedToInternet()){
            //Henter primitiv data
            textViewTeltplassNavn.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getNavn());
            textViewTeltplassUnderlag.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUnderlag().toString() + "/10");
            textViewTeltplassUtsikt.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUtsikt().toString() + "/10");
            textViewTeltplassAvstand.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getAvstand().toString() + "/100");
            textViewBrukerNavn.setText(dataSnapshot.child("users").child(teltplassUID).child("name").getValue(String.class));
            switchTeltplassSkog.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getSkog());
            switchTeltplassFjell.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFjell());
            switchTeltplassFiske.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFiske());
            textViewTeltplassTimeStamp.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getTimeStamp());

            //Henter bilde lokasjon
            String imageId = dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getImageId();
            StorageReference imageRef = mStorageReference.child("images/" + imageId);

            //Koverterer bilde-bytes til bitmap som settes i image view
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageViewTeltplass.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            //Hvis innloggede bruker er den samme som opprettet teltplassen; Gi bruker mulighet til å redigere
            if(teltplassUID.equals(UID)){
                buttonEditTeltplass.setVisibility(View.VISIBLE);
                buttonEditTeltplass.setClickable(true);
            }
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }


    }

    //onClick metode for opprette kommentar knapp
    public void navigerTilOpprettKommentar(View view){
        Intent intent = new Intent(this, OpprettKommentarActivity.class);
        intent.putExtra("teltplassId", teltplassId);
        startActivity(intent);
    }


    //(Knudsen, n.d)
    //Metode for å sette opp Naviagion drawer
    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerTeltplass);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutTeltplass);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }

    //Onclick metode for å legge til teltplass
    private void addToFavoritter(View view){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addFavoritt(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Metode for å legge til favoritt i databasen
    private void addFavoritt(DataSnapshot dataSnapshot) {
        if(isConnectedToInternet()){
            Teltplass teltplass1 = new Teltplass();
            teltplass1.setNavn(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getNavn());
            teltplass1.setUnderlag((dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUnderlag()));
            teltplass1.setUtsikt(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUtsikt());
            teltplass1.setAvstand(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getAvstand());
            teltplass1.setSkog(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getSkog());
            teltplass1.setFjell(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFjell());
            teltplass1.setFiske(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFiske());
            teltplass1.setBeskrivelse(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getBeskrivelse());
            teltplass1.setLatLng(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getLatLng());
            teltplass1.setImageId(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getImageId());
            teltplass1.setUID(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUID());

            mDatabaseRef.child("mineFavoritter").child(UID).child(teltplassId).setValue(teltplass1);
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }
    }

    //Onclick for å trykke på bruker navn
    public void brukerClicked(View view) {
        Intent intent = new Intent(TeltplassActivity.this, VisBrukerActivity.class);
        intent.putExtra("UID", teltplassUID);
        startActivity(intent);
    }

    //Onclick for å trykke på rediger teltplass knapp
    public void naviateEditTeltplassActivity(View view){
        Intent intent = new Intent(TeltplassActivity.this, EditTeltplassActivity.class);
        intent.putExtra("Id", teltplassId);
        startActivity(intent);
    }

    //(Patel, 2013)
    //Metode for å sjekke tilgang til internett
    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
