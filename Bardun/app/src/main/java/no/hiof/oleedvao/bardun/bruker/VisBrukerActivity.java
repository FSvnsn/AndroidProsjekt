package no.hiof.oleedvao.bardun.bruker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.adapter.RecycleViewAdapter;
import no.hiof.oleedvao.bardun.teltplass.Teltplass;

public class VisBrukerActivity extends AppCompatActivity {

    private String UID;
    private Intent intent;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private TextView txtVisBrukerNavn;
    private TextView txtVisBrukerEmail;
    private TextView txtVisBrukerAlder;
    private ImageView imgVisBrukerBilde;
    private TextView txtVisBrukerBeskrivelse;
    private android.support.v7.widget.Toolbar toolbar;

    private RecyclerView myRecyclerView;
    private List<Teltplass> listTeltplass;
    private RecycleViewAdapter recycleAdapter;

    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_bruker);

        //Henter UID'en fra extraen
        intent = getIntent();
        UID = intent.getExtras().getString("UID");

        listTeltplass = new ArrayList<Teltplass>();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        txtVisBrukerNavn = findViewById(R.id.txtVisBrukerNavn);
        txtVisBrukerEmail = findViewById(R.id.txtVisBrukerEmail);
        txtVisBrukerAlder = findViewById(R.id.txtVisBrukerAlder);
        txtVisBrukerBeskrivelse = findViewById(R.id.txtVisBrukerBeskrivelse);
        imgVisBrukerBilde = findViewById(R.id.imgVisBrukerBilde);

        toolbar = findViewById(R.id.toolbar_instillinger);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bardun");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRecyclerView = findViewById(R.id.rvVisBrukerTeltplasser);
        recycleAdapter = new RecycleViewAdapter(VisBrukerActivity.this, listTeltplass);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(VisBrukerActivity.this));
        myRecyclerView.setAdapter(recycleAdapter);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
                getAlleTeltplasser(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Henter brukerens teltplasser og legger de til i RecyclerView
    private void getAlleTeltplasser(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.child("mineTeltplasser").child(UID).getChildren()){
            String latLng = ds.child("latLng").getValue(String.class);
            String navn = ds.child("navn").getValue(String.class);
            String beskrivelse = ds.child("beskrivelse").getValue(String.class);
            String imageID = ds.child("imageId").getValue(String.class);

            listTeltplass.add(new Teltplass(latLng, navn, beskrivelse, imageID));
            recycleAdapter.notifyDataSetChanged();
        }
    }

    //Viser riktig brukerdata
    private void getData(DataSnapshot dataSnapshot) {
        User test1 = new User();

        try{
            test1.setName(dataSnapshot.child("users").child(UID).getValue(User.class).getName());
            test1.setEmail(dataSnapshot.child("users").child(UID).getValue(User.class).getEmail());
            test1.setAge(dataSnapshot.child("users").child(UID).getValue(User.class).getAge());
            test1.setDescription(dataSnapshot.child("users").child(UID).getValue(User.class).getDescription());

            String imageId = dataSnapshot.child("users").child(UID).getValue(User.class).getImageId();
            StorageReference imageRef = mStorageReference.child("images/" + imageId);

            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgVisBrukerBilde.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        catch(NullPointerException e){
            Toast.makeText(VisBrukerActivity.this, UID, Toast.LENGTH_LONG).show();

        }

        Log.d("TAG","showData: name: " + test1.getName());
        Log.d("TAG","showData: email: " + test1.getEmail());
        Log.d("TAG","showData: age: " + test1.getAge());

        txtVisBrukerNavn.setText(test1.getName());
        txtVisBrukerEmail.setText(test1.getEmail());
        txtVisBrukerAlder.setText(Integer.toString(test1.getAge()) + " år gammel");
        txtVisBrukerBeskrivelse.setText(test1.getDescription());
    }
}
