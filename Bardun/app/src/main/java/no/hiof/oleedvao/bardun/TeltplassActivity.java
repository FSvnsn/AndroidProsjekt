package no.hiof.oleedvao.bardun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class TeltplassActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;
    private String teltplassId;

    private ImageView imageViewTeltplass;
    private TextView textViewTeltplassNavn;
    private ImageButton imageButtonThumbsUp;
    private ImageButton imageButtonThumbsDown;
    private ImageButton imageButtonKommentar;
    private ImageButton imageButtonFavoritt;
    private TextView textViewTeltplassUnderlag;
    private TextView textViewTeltplassUtsikt;
    private TextView textViewTeltplassAvstand;
    private Switch switchTeltplassSkog;
    private Switch switchTeltplassFjell;
    private Switch switchTeltplassFiske;

    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teltplass);

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
        switchTeltplassSkog = findViewById(R.id.switchTeltplassSkog);
        switchTeltplassFjell = findViewById(R.id.switchTeltplassFjell);
        switchTeltplassFiske = findViewById(R.id.switchTeltplassFiske);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();

        UID = CUser.getUid();
        teltplassId = getIntent().getExtras().getString("Id");

        mDatabaseRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

        imageButtonFavoritt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavoritter(v);
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        try{
            textViewTeltplassNavn.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getNavn());
            textViewTeltplassUnderlag.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUnderlag().toString() + "/10");
            textViewTeltplassUtsikt.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUtsikt().toString() + "/10");
            textViewTeltplassAvstand.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getAvstand().toString() + "/100");
            switchTeltplassSkog.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getSkog());
            switchTeltplassFjell.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFjell());
            switchTeltplassFiske.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFiske());

            String imageId = dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getImageId();
            StorageReference imageRef = mStorageReference.child("images/" + imageId);

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



        }
        catch(NullPointerException e){
            Toast.makeText(this, "Something wrong", Toast.LENGTH_LONG).show();
        }


    }

    //onClick metode for å opprette kommentar
    public void navigerTilOpprettKommentar(View view){
        Intent intent = new Intent(this, OpprettKommentarActivity.class);
        intent.putExtra("teltplassId", teltplassId);
        startActivity(intent);
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerTeltplass);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutTeltplass);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }

    private void addToFavoritter(View view){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getData(DataSnapshot dataSnapshot) {
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

        mDatabaseRef.child("mineFavoritter").child(UID).child(teltplassId).setValue(teltplass1);
    }
}
