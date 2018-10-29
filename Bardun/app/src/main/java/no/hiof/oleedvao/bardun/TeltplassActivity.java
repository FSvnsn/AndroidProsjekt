package no.hiof.oleedvao.bardun;

import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class TeltplassActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
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
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();
        teltplassId = "30p000k30p000";

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
    }

    private void showData(DataSnapshot dataSnapshot) {
        Teltplass teltplass = new Teltplass();
        try{
            textViewTeltplassNavn.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getNavn());
            textViewTeltplassUnderlag.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUnderlag().toString() + "/10");
            textViewTeltplassUtsikt.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUtsikt().toString() + "/10");
            textViewTeltplassAvstand.setText(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getAvstand().toString() + "/100");
            switchTeltplassSkog.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getSkog());
            switchTeltplassFjell.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFjell());
            switchTeltplassFiske.setChecked(dataSnapshot.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFiske());
        }
        catch(NullPointerException e){

        }


    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerTeltplass);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutTeltplass);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
