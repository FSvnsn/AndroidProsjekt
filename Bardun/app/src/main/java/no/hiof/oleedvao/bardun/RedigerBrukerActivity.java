package no.hiof.oleedvao.bardun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RedigerBrukerActivity extends AppCompatActivity {

    EditText inputNavn;
    EditText inputEmail;
    EditText inputAlder;
    Button btnBrukerLagre;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rediger_bruker);

        inputNavn = findViewById(R.id.inputNavn);
        inputEmail = findViewById(R.id.inputEmail);
        inputAlder = findViewById(R.id.inputAlder);
        btnBrukerLagre = findViewById(R.id.btnBrukerLagre);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();

        btnBrukerLagre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputNavn.getText().toString() == null || inputEmail.getText().toString() == null || inputAlder.getText().toString() == null ){
                    Toast.makeText(RedigerBrukerActivity.this, "Du må fylle inn alle feltene", Toast.LENGTH_SHORT).show();
                }
                else{
                    String navn = inputNavn.getText().toString();
                    String email = inputEmail.getText().toString();
                    String alder = inputAlder.getText().toString();
                    int alderNum = Integer.parseInt(alder);

                    User test1 = new User(navn, email, alderNum);
                    mDatabaseRef.child("users").child(UID).setValue(test1);
                    Toast.makeText(RedigerBrukerActivity.this, "Bruker er lagret", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RedigerBrukerActivity.this, BrukerActivity.class));
                }
            }
        });
    }
}
