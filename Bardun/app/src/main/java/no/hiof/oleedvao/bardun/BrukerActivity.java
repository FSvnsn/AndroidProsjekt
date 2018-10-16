package no.hiof.oleedvao.bardun;

import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private DatabaseReference mDatabase;
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();

        ValueEventListener UserListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue(String.class);
                User u1 = dataSnapshot.getValue(User.class);
                //Toast.makeText(getApplicationContext(), u1.Name, Toast.LENGTH_LONG).show();
                txtName.setText(name);
                //txtEmail.setText(u1.Email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        };

        FirebaseDatabase.getInstance().getReference(UID).addListenerForSingleValueEvent(UserListener);



        //Test Data Hardcode
        //User test1 = new User("Fredrik Svendsen", "a@b.no");
        //mDatabase.child("users").child(UID).setValue(test1);
        //Toast.makeText(this, "IM HERE", Toast.LENGTH_LONG).show();
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerBruker);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutBruker);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
