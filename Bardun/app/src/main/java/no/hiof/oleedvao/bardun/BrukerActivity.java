package no.hiof.oleedvao.bardun;

import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bruker);
        toolbar = findViewById(R.id.toolbarBruker);
        setUpNavigationDrawer();

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();

        mDatabaseRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
                //String name = dataSnapshot.child("Users").child("Name").getValue().toString();
                //User u1 = dataSnapshot.getValue(User.class);
                //Toast.makeText(getApplicationContext(), u1.Name, Toast.LENGTH_LONG).show();
                //txtName.setText(name);
                //txtEmail.setText(u1.Email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });



        //Test Data Hardcode
        //User test1 = new User("Fredrik N Svendsen", "b@c.no");
        //mDatabaseRef.child("users").child(UID).setValue(test1);
        //Toast.makeText(this, "IM HERE", Toast.LENGTH_LONG).show();
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            User test1 = new User();
            test1.setName(ds.child(UID).getValue(User.class).getName());
            test1.setEmail(ds.child(UID).getValue(User.class).getEmail());

            Log.d("TAG","showData: name: " + test1.getName());
            Log.d("TAG","showData: email: " + test1.getEmail());

            txtName.setText(test1.getName());
            txtEmail.setText(test1.getEmail());
        }
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerBruker);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutBruker);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
