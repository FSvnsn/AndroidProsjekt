package no.hiof.oleedvao.bardun.teltplass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.main.MainActivity;

//https://developer.android.com/training/camera/photobasics

public class OpprettTeltplassActivity extends AppCompatActivity {
    //final variabler
    private static final int REQUEST_IMAGE_GET = 1000;
    private static final int REQUEST_TAKE_PHOTO = 2000;
    private final long ONE_MEGABYTE = 1024 * 1024;


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

    //views
    private ImageView imageView;
    private EditText editTextOpprettTeltplassNavn;
    private EditText editTextOpprettTeltplassBeskrivelse;
    private SeekBar seekBarOpprettTeltplassUnderlag;
    private SeekBar seekBarOpprettTeltplassUtsikt;
    private SeekBar seekBarOpprettTeltplassAvstand;
    private Switch switchOpprettTeltplassSkog;
    private Switch switchOpprettTeltplassFjell;
    private Switch switchOpprettTeltplassFiske;
    private Button buttonOpprettTeltplass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setter layout
        setContentView(R.layout.activity_opprett_teltplass);

        //Instantierer views
        imageView = findViewById(R.id.imageViewEditTeltplass);
        editTextOpprettTeltplassNavn = findViewById(R.id.editTextEditTeltplassNavn);
        editTextOpprettTeltplassBeskrivelse = findViewById(R.id.editTextEditTeltplassBeskrivelse);
        seekBarOpprettTeltplassUnderlag = findViewById(R.id.seekBarEditTeltplassUnderlag);
        seekBarOpprettTeltplassAvstand = findViewById(R.id.seekBarEditTeltplassAvstand);
        seekBarOpprettTeltplassUtsikt = findViewById(R.id.seekBarEditTeltplassUtsikt);
        switchOpprettTeltplassSkog = findViewById(R.id.switchEditTeltplassSkog);
        switchOpprettTeltplassFjell = findViewById(R.id.switchEditTeltplassFjell);
        switchOpprettTeltplassFiske = findViewById(R.id.switchEditTeltplassFiske);
        buttonOpprettTeltplass = findViewById(R.id.buttonLagreTeltplassEndringer);

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



    }

    //Kommentert vekk på grunn av TransactionTooLargeException
    /*
    @Override
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

    //Metode for å hente bilde fra galleri
    public void getPicture(View view){
        //implisitt intent for å hente bilde
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    //Metode for å ta bilde med kamera
    public void takePicture(View view) {
        //implisitt intent for å ta bilde med kamera
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
        //Behandling av hentet bilde fra galleri
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            try{
                Uri photoUri = data.getData();
                currentPhotoUri = photoUri;
                Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                imageView.setImageBitmap(picture);
            } catch (IOException e){
                Log.e("Implicit Intent", "Failed to get image: " + e.getStackTrace());
                Toast.makeText(this, "Klarte ikke hente bilde fra galleri.", Toast.LENGTH_LONG).show();
            }
        }
        //Behandling av bilde tatt med kamera
        else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            try{
                File file = new File(currentPhotoPath);
                Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                imageView.setImageBitmap(picture);
            }catch(Exception e){
                Log.e("Implicit Intent", "Failed to capture image: " + e.getStackTrace());
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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

    //Metode for å legge til bilde til galleri. (Ikke benyttet)
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //Hvis bruker trykker på opprett teltplass knapp
    //Metode for å opprette teltplass i databasen
    public void opprettTeltplass(View view){

        String emptyString = new String(); //variabel for inputtesting
        if(!editTextOpprettTeltplassNavn.getText().toString().equals(emptyString) &&
                !editTextOpprettTeltplassBeskrivelse.getText().toString().equals(emptyString)){ //Sjekker om alle nødvendige felt er fylt ut. (Kunne vært forbedret/utvidet)
            if(isConnectedToInternet()){
                //laster opp bilde
                uploadImage(currentPhotoUri);

                //Skaffer og converter latLng
                LatLng latLng = getIntent().getExtras().getParcelable("latLng");
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                //Konverterer LatLng til databaseId
                String location = lat + "," + lng;
                location = location.replace(".", "p");
                location = location.replace(",", "k");

                //lager et timestamp for når teltplassen ble opprettet
                Calendar cal = Calendar.getInstance();
                String timeStamp = cal.getTime().toString();

                //Oppretter teltplass-objekt
                Teltplass teltplass = new Teltplass(location,
                        editTextOpprettTeltplassNavn.getText().toString(),
                        editTextOpprettTeltplassBeskrivelse.getText().toString(),
                        seekBarOpprettTeltplassUnderlag.getProgress(),
                        seekBarOpprettTeltplassUtsikt.getProgress(),
                        seekBarOpprettTeltplassAvstand.getProgress(),
                        switchOpprettTeltplassSkog.isChecked(),
                        switchOpprettTeltplassFjell.isChecked(),
                        switchOpprettTeltplassFiske.isChecked(),
                        currentImageId,
                        UID,
                        timeStamp);

                //Legger til teltplass i database
                mDatabaseRef.child("teltplasser").child(teltplass.getLatLng()).setValue(teltplass);
                mDatabaseRef.child("mineTeltplasser").child(UID).child(teltplass.getLatLng()).setValue(teltplass);

                //Navigerer til MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Du må fylle inn alle feltene", Toast.LENGTH_LONG).show();
        }
    }

    //Hvis bruker trykker på avbryt knapp
    public void avbrytOpprettTeltplass(View view){
        //Navigerer til MainActivity
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    //https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934
    //Metode for å laste opp bilde til database
    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            if(isConnectedToInternet()){
                //Progress dialog
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Laster opp...");
                progressDialog.show();

                //Oppretter en unik bildeId
                currentImageId = UUID.randomUUID().toString();
                //Path i Firebase Storage
                StorageReference ref = mStorageReference.child("images/"+ currentImageId);
                //Legger til Firebase Storage
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(OpprettTeltplassActivity.this, "Bilde ble opplastet", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(OpprettTeltplassActivity.this, "Feilet "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Opplastet "+(int)progress+"%");
                            }
                        });
            }
            else {
                Toast.makeText(this, "Får ikke tilgang til Internett! Sjekk tilkoblingen din.", Toast.LENGTH_LONG).show();
            }

        }
    }

    //src:
    //https://stackoverflow.com/questions/19050444/how-to-handle-with-no-internet-and-lost-connection-in-android
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
