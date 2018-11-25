package no.hiof.oleedvao.bardun.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import no.hiof.oleedvao.bardun.bruker.BrukerActivity;
import no.hiof.oleedvao.bardun.main.InstillingerActivity;
import no.hiof.oleedvao.bardun.main.MainActivity;
import no.hiof.oleedvao.bardun.main.MineFavoritterActivity;
import no.hiof.oleedvao.bardun.main.MineTeltplasserActivity;
import no.hiof.oleedvao.bardun.R;

//(Knudsen, n.d)
public class NavigationDrawerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;

    //views
    private NavigationView navigationView;
    private View headerView;
    private TextView textViewHeader;
    private TextView textViewUserName;
    private ActionBarDrawerToggle drawerToggle;

    //database variabler
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    //konsturktor
    public NavigationDrawerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //layout
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container);

        //instansierer database variabler
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
       // UID = CUser.getUid();

        //instansierer views
        navigationView = view.findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
        textViewHeader = headerView.findViewById(R.id.textViewNavDrawerHeader);
        //textViewUserName = headerView.findViewById(R.id.textViewNavDrawerBrukerNavn);

        //setter header text
        textViewHeader.setText("Bardun");

        /*mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textViewUserName.setText(dataSnapshot.child("users").child(UID).child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        navigationView.setNavigationItemSelectedListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    //setter opp drawer
    public void setUpDrawer(DrawerLayout setDrawerLayout, Toolbar toolbar) {
        drawerLayout = setDrawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();
    }


    //Onclick på menyelementer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
            case R.id.nav_bruker:
                brukerClicked();
                break;
            case R.id.nav_mine_teltplasser:
                startActivity(new Intent(getActivity(), MineTeltplasserActivity.class));
                break;
            case R.id.nav_favoritter:
                startActivity(new Intent(getActivity(), MineFavoritterActivity.class));
                break;
            case R.id.nav_innstillinger:
                startActivity(new Intent(getActivity(), InstillingerActivity.class));
                break;
        }
        return false;
    }

    //Sjekker om bruker er logget inn
    private void brukerClicked() {
        if(CUser == null){
            Toast.makeText(getActivity(), "Du må logge inn for å oprette en brukerprofil!", Toast.LENGTH_LONG).show();
        }
        else{
            startActivity(new Intent(getActivity(), BrukerActivity.class));
        }
    }

}
