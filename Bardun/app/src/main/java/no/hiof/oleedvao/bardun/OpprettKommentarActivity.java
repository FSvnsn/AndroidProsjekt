package no.hiof.oleedvao.bardun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opprett_kommentar);

        //Instansierer views
        editTextKommentar = findViewById(R.id.editTextKommentar);
        buttonOpprettKommentar = findViewById(R.id.buttonOpprettKommentar);
        buttonAvbrytKommentar = findViewById(R.id.buttonAvbrytKommentar);

        //Instansierer database-relaterte variabler
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();
        teltplassId = getIntent().getExtras().getString("teltplassId");

        cal =Calendar.getInstance();
    }

    public void opprettKommentar(View view){
        Date date = cal.getTime();
        mDatabaseRef.child("teltplassKommentarer").child(teltplassId).child(UID).child(date.toString()).setValue(editTextKommentar.getText().toString());
    }

    public void avbrytKommentar(View view){

    }
}
