package com.example.hospitalapp.signin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hospitalapp.LoadingDialog;
import com.example.hospitalapp.MainActivity;
import com.example.hospitalapp.R;
import com.example.hospitalapp.pojo.HospitalDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private StorageReference storageReference;

    private String hospitalID, photoLink, name, registrationNumber, area, city, road, house, history, phone, email, password, rePassword;

    private Uri ImageUrl_main;

    private CircleImageView addImage;

    private CardView registrationLayout;

    private EditText nameEdit, registerEdt, areaEdt, cityEdit, roadEdt, houseEdt, historyEdt, phoneEdt, emailEdt, passEdt, rePassEdt;

    private boolean isPermissionGranted = false;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dialog = new LoadingDialog(RegistrationActivity.this, "Loading...");

        firebaseAuth = FirebaseAuth.getInstance();

        addImage = findViewById(R.id.addimagebtn);

        nameEdit = findViewById(R.id.hospital_name);
        registerEdt = findViewById(R.id.registerNumber);
        areaEdt = findViewById(R.id.hospital_area);
        cityEdit = findViewById(R.id.hospital_city);
        roadEdt = findViewById(R.id.hospital_road);
        houseEdt = findViewById(R.id.hospital_house);
        historyEdt = findViewById(R.id.hospital_history);
        phoneEdt = findViewById(R.id.phone_number);
        emailEdt = findViewById(R.id.email);
        passEdt = findViewById(R.id.password);
        rePassEdt = findViewById(R.id.re_password);

        registrationLayout = findViewById(R.id.hospital_register);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(RegistrationActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registrationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageUrl_main == null){
                    Toast.makeText(RegistrationActivity.this, "Select a picture for hospital", Toast.LENGTH_SHORT).show();
                    return;
                }

                name = nameEdit.getText().toString();
                if (name.isEmpty()){
                    nameEdit.setError(getString(R.string.required_field));
                    nameEdit.requestFocus();
                    return;
                }

                registrationNumber = registerEdt.getText().toString();
                if (registrationNumber.isEmpty()){
                    registerEdt.setError(getString(R.string.required_field));
                    registerEdt.requestFocus();
                    return;
                }

                area = areaEdt.getText().toString();
                if (area.isEmpty()){
                    areaEdt.setError(getString(R.string.required_field));
                    areaEdt.requestFocus();
                    return;
                }

                city = cityEdit.getText().toString();
                if (city.isEmpty()){
                    cityEdit.setError(getString(R.string.required_field));
                    cityEdit.requestFocus();
                    return;
                }

                road = roadEdt.getText().toString();
                if (road.isEmpty()){
                    roadEdt.setError(getString(R.string.required_field));
                    roadEdt.requestFocus();
                    return;
                }

                house = houseEdt.getText().toString();
                if (house.isEmpty()){
                    houseEdt.setError(getString(R.string.required_field));
                    houseEdt.requestFocus();
                    return;
                }

                history = historyEdt.getText().toString();
                if (history.isEmpty()){
                    historyEdt.setError(getString(R.string.required_field));
                    historyEdt.requestFocus();
                    return;
                }

                phone = phoneEdt.getText().toString();
                if (phone.isEmpty()){
                    phoneEdt.setError(getString(R.string.required_field));
                    phoneEdt.requestFocus();
                    return;
                }

                email = emailEdt.getText().toString();
                if (email.isEmpty()){
                    emailEdt.setError(getString(R.string.required_field));
                    emailEdt.requestFocus();
                    return;
                }

                password = passEdt.getText().toString()+"hospital";
                if (password.isEmpty()){
                    passEdt.setError(getString(R.string.required_field));
                    passEdt.requestFocus();
                    return;
                }

                rePassword = rePassEdt.getText().toString()+"hospital";
                if (rePassword.isEmpty()){
                    rePassEdt.setError(getString(R.string.required_field));
                    rePassEdt.requestFocus();
                    return;
                }

                if (!(rePassword.equals(password))){
                    Toast.makeText(RegistrationActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    rePassEdt.setError("Enter same password");
                    rePassEdt.requestFocus();
                    return;
                }

                dialog.show();

                registerUser();
            }
        });

    }

    private void registerUser() {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseAuth = FirebaseAuth.getInstance();
                                rootRef = FirebaseDatabase.getInstance().getReference();
                                user = firebaseAuth.getCurrentUser();
                                userRef = rootRef.child("Hospital").child(user.getUid());
                                hospitalID = String.valueOf(user.getUid());
                                Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                userRegistration();
                            }else {
                                Toast.makeText(RegistrationActivity.this, "Failed to register...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void userRegistration() {
        storageReference = FirebaseStorage.getInstance().getReference();
        final Uri imageUri = ImageUrl_main;
        final StorageReference imageRef = storageReference.child("HospitalImage").child(imageUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                photoLink = downloadUri.toString();
                HospitalDetails details = new HospitalDetails(hospitalID, photoLink, name, registrationNumber, area, city, road, house, history, phone, email, password);
                userRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            String token = FirebaseInstanceId.getInstance().getToken();
                            Map<String, Object> tokenMap = new HashMap<>();
                            tokenMap.put("device_token", token);

                            rootRef.child("Users").child(user.getUid()).child("Token").setValue(tokenMap);

                            startActivity(intent);
                            finish();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Failed to registration", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
            ImageUrl_main = data.getData();
            addImage.setImageURI(ImageUrl_main);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(RegistrationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(RegistrationActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(RegistrationActivity.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },PERMISSION_CODE);

        }else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==PERMISSION_CODE){

            if((grantResults[0] ==PackageManager.PERMISSION_GRANTED
                    && grantResults[1] ==PackageManager.PERMISSION_GRANTED
            )){
                isPermissionGranted = true;
            }else {
                checkPermission();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
