package no.hiof.oleedvao.bardun;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class InstillingerActivity extends AppCompatActivity {

    private Button btnLogOut;
    private Switch switchNotifications;
    private android.support.v7.widget.Toolbar toolbarInstillinger;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    private Boolean switchReady;
    private String TAG = "Svendsen";

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instillinger);

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        toolbarInstillinger = findViewById(R.id.toolbar_instillinger);
        setSupportActionBar(toolbarInstillinger);
        getSupportActionBar().setTitle("Instillinger");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchReady = FALSE;

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();

        try{
            UID = CUser.getUid();
        }
        catch (NullPointerException e){
            e.printStackTrace();
            Log.d(TAG, "Ingen bruker logget inn");
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(InstillingerActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (CUser != null){
            btnLogOut.setText("Logg Ut");
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
        else{
            btnLogOut.setText("Logg Inn");
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }

        if (CUser != null){
            getUserSettings();

            switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(switchNotifications.isChecked() == TRUE && switchReady == TRUE){
                        mDatabaseRef.child("users").child(UID).child("sendNotification").setValue(TRUE);
                    }
                    else if (switchNotifications.isChecked() == FALSE && switchReady == TRUE){
                        mDatabaseRef.child("users").child(UID).child("sendNotification").setValue(FALSE);
                    }
                }
            });
        }
    }

    private void getUserSettings() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean sendNotification = dataSnapshot.child("users").child(UID).child("sendNotification").getValue(Boolean.class);

                if (sendNotification == null){
                    switchNotifications.setChecked(TRUE);
                }
                else{
                    switchNotifications.setChecked(sendNotification);
                }

                switchReady = TRUE;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signIn() {
        startActivity(new Intent(InstillingerActivity.this, LoginActivity.class));
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //put something you want to happen here eg.
                        startActivity(new Intent(InstillingerActivity.this, LoginActivity.class));
                    }
                });
    }
}
