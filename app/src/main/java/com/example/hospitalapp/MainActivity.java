package com.example.hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitalapp.adapter.DoctorListAdapter;
import com.example.hospitalapp.bookingList.HospitalBookingDoctorListActivity;
import com.example.hospitalapp.doctors.AddNewDoctorActivity;
import com.example.hospitalapp.pojo.DoctorDetails;
import com.example.hospitalapp.pojo.HospitalDetails;
import com.example.hospitalapp.signin.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DoctorListAdapter.DoctorDetailsListener {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private boolean isPermissionGranted = false;

    private Uri ImageUrl_main;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference doctorRef;

    private StorageReference storageReference;

    private Toolbar toolbar;

    private LoadingDialog dialog;

    private TextView addNewDoctorTV;

    private ImageView profileImageIV;

    private TextView nameTV, areaTV, cityTV, historyTV, uploadPhotoTV, savePhotoTV, bookingTV;

    private String hospitalID, image, name, registration, area, city, road, house, history, phone, email, password;

    private List<DoctorDetails> doctorList = new ArrayList<DoctorDetails>();
    private RecyclerView doctorRecycler;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new LoadingDialog(MainActivity.this,"Loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Hospital").child(user.getUid());
        doctorRef = userRef.child("DoctorList");

        toolbar = (Toolbar) findViewById(R.id.hospital_toolbar_base);
        setSupportActionBar(toolbar);

        profileImageIV = findViewById(R.id.hospital_image);
        nameTV = findViewById(R.id.hospital_name);
        areaTV = findViewById(R.id.hospital_address_area);
        cityTV = findViewById(R.id.hospital_address_city);
        historyTV = findViewById(R.id.hospital_history);
        uploadPhotoTV = findViewById(R.id.upload_photo);
        savePhotoTV = findViewById(R.id.save_photo);
        bookingTV = findViewById(R.id.booking_list);

        addNewDoctorTV = findViewById(R.id.add_new_doctor);

        doctorRecycler = findViewById(R.id.hospital_doctor_list_recycler);

        uploadPhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(MainActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        savePhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                userRegistration();
            }
        });

        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorList.clear();
                for (DataSnapshot hd: dataSnapshot.getChildren()){
                    DoctorDetails doc = hd.child("Details").getValue(DoctorDetails.class);
                    doctorList.add(doc);
                }
                DoctorListAdapter adapter = new DoctorListAdapter(MainActivity.this, doctorList);
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                llm.setOrientation(RecyclerView.VERTICAL);
                doctorRecycler.setLayoutManager(llm);
                doctorRecycler.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HospitalDetails h = dataSnapshot.getValue(HospitalDetails.class);

                hospitalID = h.getHospitalID();
                image = h.getImage();
                name = h.getName();
                registration = h.getRegistration();
                area = h.getArea();
                city = h.getCity();
                road = h.getRoad();
                house = h.getHouse();
                history = h.getHistory();
                phone = h.getPhone();
                email = h.getEmail();
                password = h.getPassword();

                Uri photoUri = Uri.parse(image);
                Picasso.get().load(photoUri).into(profileImageIV);

                nameTV.setText(name);
                areaTV.setText("House: "+house+", "+area);
                cityTV.setText(city);
                historyTV.setText(history);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bookingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HospitalBookingDoctorListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addNewDoctorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewDoctorActivity.class);
                intent.putExtra("DoctorKey", "null");
                startActivity(intent);
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify1", "notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("hospital");
    }

    private void userRegistration() {
        DatabaseReference rootUpdateRef = FirebaseDatabase.getInstance().getReference().child("Hospital");
        final DatabaseReference updateRef = rootUpdateRef.child(user.getUid());
        final String key = user.getUid();
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
                String photoLink = downloadUri.toString();
                HospitalDetails details = new HospitalDetails(key, photoLink, name, registration, area, city, road, house, history, phone, email, password);
                updateRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            savePhotoTV.setVisibility(View.GONE);
                            uploadPhotoTV.setVisibility(View.VISIBLE);
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed to registration", Toast.LENGTH_SHORT).show();

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
            profileImageIV.setImageURI(ImageUrl_main);
            savePhotoTV.setVisibility(View.VISIBLE);
            uploadPhotoTV.setVisibility(View.GONE);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(MainActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    public void log_out(MenuItem item){
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("device_token", "");
        rootRef.child("Users").child(user.getUid()).child("Token").updateChildren(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onDoctorDetails(DoctorDetails doctorDetails) {
        Intent intent = new Intent(MainActivity.this, AddNewDoctorActivity.class);
        intent.putExtra("DoctorKey", "yes");
        intent.putExtra("DoctorDetails", doctorDetails);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
