package no.hiof.oleedvao.bardun;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbarMain);
        setUpNavigationDrawer();
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerMain);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMain);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
