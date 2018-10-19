package no.hiof.oleedvao.bardun;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class TeltplassActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teltplass);
        toolbar = findViewById(R.id.toolbarTeltplass);
        setUpNavigationDrawer();
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavDrawerTeltplass);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutTeltplass);

        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }
}
