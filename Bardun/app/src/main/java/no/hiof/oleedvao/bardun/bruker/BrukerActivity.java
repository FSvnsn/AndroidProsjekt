package no.hiof.oleedvao.bardun.bruker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.adapter.ViewPagerAdapter;
import no.hiof.oleedvao.bardun.fragment.MineFavoritterFragment;
import no.hiof.oleedvao.bardun.fragment.MineTeltplasserFragment;
import no.hiof.oleedvao.bardun.fragment.NavigationDrawerFragment;

public class BrukerActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    private TabLayout tablayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtAge;
    private TextView txtDescription;
    private Button btnBrukerRediger;
    private ImageView imgProfile;

    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bruker);
        toolbar = findViewById(R.id.toolbarBruker);
        setUpNavigationDrawer();

        tablayout = findViewById(R.id.tab_layout_id);
        appBarLayout = findViewById(R.id.app_bar_layout_id);
        viewPager = findViewById(R.id.view_pager_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new MineTeltplasserFragment(), "Mine Teltplasser");
        adapter.AddFragment(new MineFavoritterFragment(), "Mine Favoritter");
        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAge = findViewById(R.id.txtAge);
        txtDescription = findViewById(R.id.txtDescription);
        btnBrukerRediger = findViewById(R.id.btnBrukerRediger);
        imgProfile = findViewById(R.id.imgProfile);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
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
                test1.setDescription(dataSnapshot.child("users").child(UID).getValue(User.class).getDescription());

                String imageId = dataSnapshot.child("users").child(UID).getValue(User.class).getImageId();
                StorageReference imageRef = mStorageReference.child("images/" + imageId);

                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgProfile.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
            catch(NullPointerException e){
                e.printStackTrace();
            }

            if (test1.getName() == "" || test1.getEmail() == null || test1.getAge() == 0){
                createUser();
            }

            Log.d("TAG","showData: name: " + test1.getName());
            Log.d("TAG","showData: email: " + test1.getEmail());
            Log.d("TAG","showData: age: " + test1.getAge());

            txtName.setText(test1.getName());
            txtEmail.setText(test1.getEmail());
            txtAge.setText(Integer.toString(test1.getAge()) + " år gammel");
            txtDescription.setText(test1.getDescription());
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
