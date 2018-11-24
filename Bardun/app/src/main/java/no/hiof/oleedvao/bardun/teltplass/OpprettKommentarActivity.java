package no.hiof.oleedvao.bardun.teltplass;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import no.hiof.oleedvao.bardun.R;

public class OpprettKommentarActivity extends AppCompatActivity {

    //views
    private EditText editTextKommentar;
    private Button buttonOpprettKommentar;
    private Button buttonAvbrytKommentar;

    //Database-relaterte variabler
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;
    private String teltplassId;

    private Calendar cal;
    private String brukernavn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opprett_kommentar);

        //Instansierer views
        editTextKommentar = findViewById(R.id.editTextKommentar);
        buttonOpprettKommentar = findViewById(R.id.buttonOpprettKommentar);
        buttonAvbrytKommentar = findViewById(R.id.buttonAvbrytKommentar);

        if(isConnectedToInternet()){
            //Instansierer database-relaterte variabler
            mDatabase = FirebaseDatabase.getInstance();
            mDatabaseRef = mDatabase.getReference();
            mAuth = FirebaseAuth.getInstance();
            CUser = mAuth.getCurrentUser();
            UID = CUser.getUid();
            teltplassId = getIntent().getExtras().getString("teltplassId");

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    brukernavn = dataSnapshot.child("users").child(UID).child("name").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }

        cal =Calendar.getInstance();
    }

    //Onclick metdoe for opprett kommentar
    public void opprettKommentar(View view){
        if(isConnectedToInternet()){
            Date date = cal.getTime();

            Kommentar kommentar = new Kommentar(date.toString(), brukernavn, editTextKommentar.getText().toString());

            mDatabaseRef.child("teltplassKommentarer").child(teltplassId).child(date.toString()).setValue(kommentar);

            Intent intent = new Intent(this, TeltplassActivity.class);
            intent.putExtra("Id",teltplassId);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }

    }

    //Onclick for avbryt kommentar knapp
    public void avbrytKommentar(View view){
        Intent intent = new Intent(this, TeltplassActivity.class);
        intent.putExtra("Id",teltplassId);
        startActivity(intent);
    }

    //src:
    //https://stackoverflow.com/questions/19050444/how-to-handle-with-no-internet-and-lost-connection-in-android
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
