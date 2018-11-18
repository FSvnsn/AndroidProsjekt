package no.hiof.oleedvao.bardun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.Date;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

public class RedigerBrukerActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 1000;
    private static final int REQUEST_TAKE_PHOTO = 2000;

    private EditText inputNavn;
    private EditText inputEmail;
    private EditText inputAlder;
    private EditText inputBeskrivelse;
    private Button btnBrukerLagre;
    private Button btnTaBilde;
    private Button btnLastOppBilde;
    private ImageView imgRedigerBruker;

    private String currentPhotoPath;
    private Uri currentPhotoUri;
    private String currentImageId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;
    private boolean uploadedPic = false;
    private String TAG = "Svendsen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rediger_bruker);

        inputNavn = findViewById(R.id.inputNavn);
        inputEmail = findViewById(R.id.inputEmail);
        inputAlder = findViewById(R.id.inputAlder);
        inputBeskrivelse = findViewById(R.id.inputBeskrivelse);
        btnBrukerLagre = findViewById(R.id.btnBrukerLagre);
        btnTaBilde = findViewById(R.id.btnTaBilde);
        btnLastOppBilde = findViewById(R.id.btnLastOppBilde);
        imgRedigerBruker = findViewById(R.id.imgRedigerBruker);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();

        btnBrukerLagre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String navn = inputNavn.getText().toString();
                String alder = inputAlder.getText().toString();
                try {
                    int alderNum = Integer.parseInt(alder);
                    if (!(alder.matches(""))){
                        mDatabaseRef.child("users").child(UID).child("age").setValue(alderNum);
                    }
                }
                catch(NumberFormatException e){
                    e.printStackTrace();
                }
                String email = inputEmail.getText().toString();
                String beskrivelse = inputBeskrivelse.getText().toString();

                if (uploadedPic == true){
                    uploadImage(currentPhotoUri);
                    mDatabaseRef.child("users").child(UID).child("imageId").setValue(currentImageId);
                }

                if(!(navn.matches(""))){
                    mDatabaseRef.child("users").child(UID).child("name").setValue(navn);
                }

                if (!(email.matches(""))){
                    mDatabaseRef.child("users").child(UID).child("email").setValue(email);
                }

                if (!(beskrivelse.matches(""))){
                    mDatabaseRef.child("users").child(UID).child("description").setValue(beskrivelse);
                }

                checkIfFirstTime();

                    Toast.makeText(RedigerBrukerActivity.this, "Bruker er lagret", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RedigerBrukerActivity.this, BrukerActivity.class));
            }
        });
    }

    //sjekker om bruker har endret på notifikasjons instillinger
    private void checkIfFirstTime() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean sendNotification = dataSnapshot.child("users").child(UID).child("sendNotification").getValue(Boolean.class);

                if (sendNotification == null){
                    mDatabaseRef.child("users").child(UID).child("sendNotification").setValue(TRUE);
                    Log.d(TAG, "Finnes ingen notifikasjons instillinger");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bitmap bm =((BitmapDrawable)imgRedigerBruker.getDrawable()).getBitmap();
        outState.putParcelable("bitmap",bm);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        imgRedigerBruker.setImageBitmap((Bitmap) savedInstanceState.getParcelable("bitmap"));

        super.onRestoreInstanceState(savedInstanceState);
    }

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
                uploadedPic = true;
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
                imgRedigerBruker.setImageBitmap(picture);
                uploadedPic = true;
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
                imgRedigerBruker.setImageBitmap(picture);
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
                            Toast.makeText(RedigerBrukerActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RedigerBrukerActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
