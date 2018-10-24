package no.hiof.oleedvao.bardun;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class BrukerActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;
    TextView txtName;
    TextView txtEmail;
    TextView txtAge;
    Button btnBrukerRediger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bruker);
        toolbar = findViewById(R.id.toolbarBruker);
        setUpNavigationDrawer();

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAge = findViewById(R.id.txtAge);
        btnBrukerRediger = findViewById(R.id.btnBrukerRediger);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();

        btnBrukerRediger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

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
            User test1 = new User();

            try{
                test1.setName(dataSnapshot.child("users").child(UID).getValue(User.class).getName());
                test1.setEmail(dataSnapshot.child("users").child(UID).getValue(User.class).getEmail());
                test1.setAge(dataSnapshot.child("users").child(UID).getValue(User.class).getAge());
            }
            catch(NullPointerException e){
                createUser();
            }

            Log.d("TAG","showData: name: " + test1.getName());
            Log.d("TAG","showData: email: " + test1.getEmail());
            Log.d("TAG","showData: age: " + test1.getAge());

            txtName.setText(test1.getName());
            txtEmail.setText(test1.getEmail());
            txtAge.setText(Integer.toString(test1.getAge()));
    }

    private void createUser() {
        startActivity(new Intent(this, RedigerBrukerActivity.class));
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerBruker);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutBruker);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
