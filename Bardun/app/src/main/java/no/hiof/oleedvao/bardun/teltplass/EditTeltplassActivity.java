package no.hiof.oleedvao.bardun.teltplass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.main.MainActivity;

public class EditTeltplassActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_GET = 1000;
    private static final int REQUEST_TAKE_PHOTO = 2000;
    final long ONE_MEGABYTE = 1024 * 1024;

    //Bilde-relaterte variabler
    private String currentPhotoPath;
    private Uri currentPhotoUri;
    private String currentImageId;

    //Database-relaterte variabler
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;
    private String teltplassId;
    private String imageId;

    //views
    private ImageView imageView;
    private EditText editTextEditTeltplassNavn;
    private EditText editTextEditTeltplassBeskrivelse;
    private SeekBar seekBarEditTeltplassUnderlag;
    private SeekBar seekBarEditTeltplassUtsikt;
    private SeekBar seekBarEditTeltplassAvstand;
    private Switch switchEditTeltplassSkog;
    private Switch switchEditTeltplassFjell;
    private Switch switchEditTeltplassFiske;
    private Button buttonLagreTeltplassEndringer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teltplass);

        //Instantierer views
        imageView = findViewById(R.id.imageViewEditTeltplass);
        editTextEditTeltplassNavn = findViewById(R.id.editTextEditTeltplassNavn);
        editTextEditTeltplassBeskrivelse = findViewById(R.id.editTextEditTeltplassBeskrivelse);
        seekBarEditTeltplassUnderlag = findViewById(R.id.seekBarEditTeltplassUnderlag);
        seekBarEditTeltplassAvstand = findViewById(R.id.seekBarEditTeltplassAvstand);
        seekBarEditTeltplassUtsikt = findViewById(R.id.seekBarEditTeltplassUtsikt);
        switchEditTeltplassSkog = findViewById(R.id.switchEditTeltplassSkog);
        switchEditTeltplassFjell = findViewById(R.id.switchEditTeltplassFjell);
        switchEditTeltplassFiske = findViewById(R.id.switchEditTeltplassFiske);
        buttonLagreTeltplassEndringer = findViewById(R.id.buttonLagreTeltplassEndringer);

        if(isConnectedToInternet()){
            //Instansierer database-relaterte variabler
            mDatabase = FirebaseDatabase.getInstance();
            mDatabaseRef = mDatabase.getReference();
            mStorage = FirebaseStorage.getInstance();
            mStorageReference = mStorage.getReference();
            mAuth = FirebaseAuth.getInstance();
            CUser = mAuth.getCurrentUser();
            UID = CUser.getUid();
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }

        //henter teltplassId fra forrige Activity
        teltplassId = getIntent().getStringExtra("Id");

        //viser data
        showData();



    }

    //Removed due to TransactionTooLargeException
    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        Bitmap bm =((BitmapDrawable)imageView.getDrawable()).getBitmap();
        outState.putParcelable("bitmap",bm);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        imageView.setImageBitmap((Bitmap) savedInstanceState.getParcelable("bitmap"));

        super.onRestoreInstanceState(savedInstanceState);
    }*/

    //(Knudsen, n.d)
    //Metode for å hente bilde fra galleri
    public void getPicture(View view){
        //creates implicit intent to get image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }


    //(Mantech, 2015)
    //Metode for å ta bilde med kamera
    public void takePicture(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("PicError","Could not createImageFile");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "no.hiof.oleedvao.bardun.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //metode for å behandle resultater for implisitte intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //(Knudsen, n.d)
        //Behandling av hentet bilde fra galleri
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            try{
                //get the image uri
                Uri photoUri = data.getData();
                currentPhotoUri = photoUri;
                //get the image bitmap from the uri
                Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                //set the image view bitmap to the retrieved image bitmap
                imageView.setImageBitmap(picture);
            } catch (IOException e){
                Log.e("Implicit Intent", "Failed to get image: " + e.getStackTrace());
                Toast.makeText(this, "Couldn't get picture", Toast.LENGTH_SHORT).show();
            }
        }
        //(Android Developers, 2018)
        //Behandling av bilde tatt med kamera
        else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            try{
                //Retrieves file from saved photo path
                File file = new File(currentPhotoPath);
                //gets bitmap from file uri
                Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                //sets the image view bitmap to the retrieved image bitmap
                imageView.setImageBitmap(picture);
            }catch(Exception e){
                Log.e("Implicit Intent", "Failed to capture image: " + e.getStackTrace());
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //(Android Developers, 2018)
    //Metode for å lage en bildefil
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        currentPhotoUri = Uri.fromFile(new File(currentPhotoPath));
        //galleryAddPic();
        return image;
    }

    //Onclick metode for lagre endringer knapp
    public void editTeltplass(View view){
        if(isConnectedToInternet()){
            if(currentPhotoUri != null){
                uploadImage(currentPhotoUri);
            }

            Calendar cal = Calendar.getInstance();
            String timeStamp = cal.getTime().toString();

            Teltplass teltplass = new Teltplass(teltplassId,
                    editTextEditTeltplassNavn.getText().toString(),
                    editTextEditTeltplassBeskrivelse.getText().toString(),
                    seekBarEditTeltplassUnderlag.getProgress(),
                    seekBarEditTeltplassUtsikt.getProgress(),
                    seekBarEditTeltplassAvstand.getProgress(),
                    switchEditTeltplassSkog.isChecked(),
                    switchEditTeltplassFjell.isChecked(),
                    switchEditTeltplassFiske.isChecked(),
                    currentImageId,
                    UID,
                    timeStamp);

            mDatabaseRef.child("teltplasser").child(teltplass.getLatLng()).setValue(teltplass);
            mDatabaseRef.child("mineTeltplasser").child(UID).child(teltplass.getLatLng()).setValue(teltplass);

            Intent intent = new Intent(EditTeltplassActivity.this, TeltplassActivity.class);
            intent.putExtra("Id", teltplassId);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }

    }

    //Onclick metode for slett teltplass knapp
    public void slettTeltplass(View view){

        /*//Fjerner ikke alle steder
        mDatabaseRef.child("teltplasser").child(teltplassId).removeValue();
        mDatabaseRef.child("mineTeltplasser").child(UID).child(teltplassId).removeValue();*/

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    //Onclick metode for å avbryte redigering av teltplass
    public void avbrytEditTeltplass(View view){
        Intent intent = new Intent(this, TeltplassActivity.class);
        intent.putExtra("Id", teltplassId);
        startActivity(intent);
    }

    //(Izuchukwu, 2017)
    //Metode for å laste opp bilde til database
    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            if(isConnectedToInternet()){
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = mStorageReference.child("images/"+ imageId);

                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(EditTeltplassActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditTeltplassActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            }
                        });
            }
            else {
                Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
            }

        }
    }

    //Metode for å vise hente data fra databasen og vise det frem i views
    private void showData(){
        //todo : sjekk internett
        if(isConnectedToInternet()){
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    editTextEditTeltplassNavn.setText(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getNavn());
                    editTextEditTeltplassBeskrivelse.setText(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getBeskrivelse());
                    seekBarEditTeltplassUnderlag.setProgress(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUnderlag());
                    seekBarEditTeltplassUtsikt.setProgress(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getUtsikt());
                    seekBarEditTeltplassAvstand.setProgress(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getAvstand());
                    switchEditTeltplassSkog.setChecked(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getSkog());
                    switchEditTeltplassFjell.setChecked(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFjell());
                    switchEditTeltplassFiske.setChecked(ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getFiske());

                    imageId = ds.child("teltplasser").child(teltplassId).getValue(Teltplass.class).getImageId();
                    StorageReference imageRef = mStorageReference.child("images/" + imageId);

                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
        }

    }

    //(Patel, 2013)
    //Metode for å sjekke tilgang til internett
    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

}
