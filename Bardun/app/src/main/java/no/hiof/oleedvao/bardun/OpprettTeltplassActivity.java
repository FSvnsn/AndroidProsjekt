package no.hiof.oleedvao.bardun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.Date;
import java.util.UUID;

public class OpprettTeltplassActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_GET = 1000;
    private static final int REQUEST_TAKE_PHOTO = 2000;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opprett_teltplass);

        //Instantierer views
        imageView = findViewById(R.id.imageViewOpprettTeltplass);
        editTextOpprettTeltplassNavn = findViewById(R.id.editTextOpprettTeltplassNavn);
        editTextOpprettTeltplassBeskrivelse = findViewById(R.id.editTextOpprettTeltplassBeskrivelse);
        seekBarOpprettTeltplassUnderlag = findViewById(R.id.seekBarOpprettTeltplassUnderlag);
        seekBarOpprettTeltplassAvstand = findViewById(R.id.seekBarOpprettTeltplassAvstand);
        seekBarOpprettTeltplassUtsikt = findViewById(R.id.seekBarOpprettTeltplassUtsikt);
        switchOpprettTeltplassSkog = findViewById(R.id.switchOppretTeltplassSkog);
        switchOpprettTeltplassFjell = findViewById(R.id.switchOpprettTeltplassFjell);
        switchOpprettTeltplassFiske = findViewById(R.id.switchOpprettTeltplassFiske);

        //Instansierer database-relaterte variabler
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();
    }

    //Metode for å hente bilde fra galleri
    public void getPicture(View view){
        //creates implicit intent to get image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                Toast.makeText(this, "Couldn't get picture", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            try{
                //Retrieves file from saved photo path
                File file = new File(currentPhotoPath);
                //gets bitmap from file uri
                Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                //sets the image view bitmap to the retrieved image bitmap
                imageView.setImageBitmap(picture);
            }catch(Exception e){
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void opprettTeltplass(View view){
        if(editTextOpprettTeltplassNavn.getText() != null &&
                editTextOpprettTeltplassBeskrivelse.getText() != null){

            uploadImage(currentPhotoUri);

            //Skaffer og converter latLng
            LatLng latLng = getIntent().getExtras().getParcelable("latLng");
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            String lat = String.valueOf(latitude);
            String lng = String.valueOf(longitude);
            String location = lat + "," + lng;
            location = location.replace(".", "p");
            location = location.replace(",", "k");

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
                    UID);

            mDatabaseRef.child("teltplasser").child(teltplass.getLatLng()).setValue(teltplass);
            mDatabaseRef.child("mineTeltplasser").child(UID).child(teltplass.getLatLng()).setValue(teltplass);
        }
        else{
            Toast.makeText(this, "Du må fylle inn alle feltene", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            currentImageId = UUID.randomUUID().toString();

            StorageReference ref = mStorageReference.child("images/"+ currentImageId);


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(OpprettTeltplassActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(OpprettTeltplassActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

}
