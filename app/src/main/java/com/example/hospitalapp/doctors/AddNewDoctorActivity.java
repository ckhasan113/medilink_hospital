package com.example.hospitalapp.doctors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hospitalapp.LoadingDialog;
import com.example.hospitalapp.MainActivity;
import com.example.hospitalapp.R;
import com.example.hospitalapp.pojo.DoctorDetails;
import com.example.hospitalapp.pojo.HospitalDetails;
import com.example.hospitalapp.signin.RegistrationActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewDoctorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private SimpleDateFormat selectedDateOfMonth = new SimpleDateFormat("dd", Locale.getDefault());

    private SimpleDateFormat dateFormatForDateOfMonth = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonthOfMonth = new SimpleDateFormat("MM", Locale.getDefault());
    private SimpleDateFormat dateFormatForYearOfMonth = new SimpleDateFormat("yyyy", Locale.getDefault());

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference doctorRef;

    private StorageReference storageReference;

    private CircleImageView addImage;

    private TextView startTV, endTV, degreeTV, specialityTV, datesTV;

    private Spinner degreeSP, specialitySP;

    private EditText nameEdt, registrationEdt, patientEdt, blockEdt, floorEdt, roomEdt, feesEdt, phoneEdt;

    private Button addDoctorBtn, updateBtn;

    private String doctorID, image, name, degree, speciality, registration,  start, end, patient, block, floor, room, fees, phone;

    private List<String> days = new ArrayList<String>();
    private List<String> dates = new ArrayList<String>();

    private boolean isPermissionGranted = false;

    private int mHour, mMinute;

    private Uri ImageUrl_main;

    private String doctorKey;

    private DoctorDetails doctorDetails;

    private LoadingDialog dialog;

    private CompactCalendarView calendarView;

    private boolean doubledateChacker = false;

    private String monthString;
    private String yearString;
    private String dateString;
    private String fullDateString;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_doctor);

        dialog = new LoadingDialog(AddNewDoctorActivity.this, "Loading...");

        doctorKey = getIntent().getStringExtra("DoctorKey");
        doctorDetails = (DoctorDetails) getIntent().getSerializableExtra("DoctorDetails");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Hospital").child(user.getUid());
        doctorRef = userRef.child("DoctorList");

        addImage = findViewById(R.id.add_doctor_image_Btn);
        nameEdt = findViewById(R.id.h_doctor_name);
        degreeSP = findViewById(R.id.isp_degree);
        specialitySP = findViewById(R.id.isp_speciality);
        registrationEdt = findViewById(R.id.h_doctor_register);

        datesTV = findViewById(R.id.dates);
        startTV = findViewById(R.id.stTime);
        endTV = findViewById(R.id.endTime);
        patientEdt = findViewById(R.id.patient_count);
        blockEdt = findViewById(R.id.ch_block);
        floorEdt = findViewById(R.id.ch_floor);
        roomEdt = findViewById(R.id.ch_room);
        feesEdt = findViewById(R.id.ch_fee);
        phoneEdt = findViewById(R.id.ch_phone);

        degreeTV = findViewById(R.id.doctor_details_degree);
        specialityTV = findViewById(R.id.doctor_details_speciality);

        calendarView = findViewById(R.id.selectDateCalender_view);

        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setFirstDayOfWeek(Calendar.SATURDAY);
        calendarView.shouldScrollMonth(false);

        datesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setVisibility(View.VISIBLE);
            }
        });

        dateString = dateFormatForDateOfMonth.format(Calendar.getInstance().getTime());
        monthString = dateFormatForMonthOfMonth.format(Calendar.getInstance().getTime());
        yearString = dateFormatForYearOfMonth.format(Calendar.getInstance().getTime());
        fullDateString = dateString+"/"+monthString+"/"+yearString;

        days.clear();
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String selectedDate = selectedDateOfMonth.format(dateClicked);
                long selectedDateColor = dateClicked.getTime();
                dateString = dateFormatForDateOfMonth.format(dateClicked);
                for (String d: days){

                    if (d.equals(dateString)){
                        long startDate = 0L;
                        try {

                            fullDateString = d+"/"+monthString+"/"+yearString;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = sdf.parse(fullDateString);
                            startDate = date.getTime();
                            dates.add(dateString);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Event ev = new Event(Color.GREEN, startDate, "Doctor Available");
                        calendarView.removeEvent(ev);
                    }
                }
                Event ev1 = new Event(Color.parseColor("#4FBD54"), selectedDateColor, "Date selected");
                doubledateChacker = false;
                calendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#4CAF50"));
                for (String ds: days){
                    if (ds.equals(selectedDate)){
                        Toast.makeText(AddNewDoctorActivity.this, ds+" is removed", Toast.LENGTH_SHORT).show();
                        days.remove(ds);
                        calendarView.removeEvent(ev1);
                        calendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#E46F6D"));
                        doubledateChacker = true;
                        break;
                    }else {
                        calendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#4CAF50"));
                        doubledateChacker = false;
                    }
                }
                if (!doubledateChacker){
                    calendarView.addEvent(ev1);
                    Toast.makeText(AddNewDoctorActivity.this, selectedDate+" is selected", Toast.LENGTH_SHORT).show();
                    days.add(selectedDate);
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }

        });

        addDoctorBtn = findViewById(R.id.add_new_doctor_info);
        updateBtn = findViewById(R.id.update_doctor_info);

        ArrayAdapter spinnerDegreeAdapter = ArrayAdapter.createFromResource(AddNewDoctorActivity.this, R.array.degree, R.layout.spinner_item_select_model);

        spinnerDegreeAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down_model);


        ArrayAdapter spinnerSpecialityAdapter = ArrayAdapter.createFromResource(AddNewDoctorActivity.this, R.array.speciality_id, R.layout.spinner_item_select_model);
        spinnerSpecialityAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down_model);

        ArrayAdapter spinnerDaysAdapter = ArrayAdapter.createFromResource(AddNewDoctorActivity.this, R.array.t_f, R.layout.spinner_item_select_model);
        spinnerDaysAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down_model);

        degreeSP.setAdapter(spinnerDegreeAdapter);
        specialitySP.setAdapter(spinnerSpecialityAdapter);

        if (doctorKey.equals("yes")){

            calendarView.setVisibility(View.VISIBLE);
            addDoctorBtn.setVisibility(View.GONE);
            updateBtn.setVisibility(View.VISIBLE);

            degreeSP.setVisibility(View.GONE);
            degreeTV.setVisibility(View.VISIBLE);

            specialitySP.setVisibility(View.GONE);
            specialityTV.setVisibility(View.VISIBLE);

            doctorID = doctorDetails.getDoctorID();
            image = doctorDetails.getImage();
            name = doctorDetails.getName();
            degree = doctorDetails.getDegree();
            speciality = doctorDetails.getSpeciality();
            registration = doctorDetails.getRegistrationNumber();

            days = doctorDetails.getDateList();

            start = doctorDetails.getStartTime();
            end = doctorDetails.getEndTime();
            patient = doctorDetails.getPatientCount();
            block = doctorDetails.getBlock();
            floor = doctorDetails.getFloor();
            room = doctorDetails.getRoom();
            fees = doctorDetails.getFees();
            phone = doctorDetails.getPhone();

            for (String d: days){

                long startDate = 0L;
                try {

                    fullDateString = d+"/"+monthString+"/"+yearString;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sdf.parse(fullDateString);
                    startDate = date.getTime();
                    dates.add(dateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Event ev = new Event(Color.GREEN, startDate, "Doctor Available");
                calendarView.addEvent(ev);
            }

            nameEdt.setText(name);
            degreeTV.setText(degree);
            specialityTV.setText(speciality);
            registrationEdt.setText(registration);
            startTV.setText(start);
            endTV.setText(end);
            patientEdt.setText(patient);
            blockEdt.setText(block);
            floorEdt.setText(floor);
            roomEdt.setText(room);
            feesEdt.setText(fees);
            phoneEdt.setText(phone);

            ImageUrl_main = Uri.parse(image);
            /*addImage.setImageURI(ImageUrl_main);*/
            Picasso.get().load(ImageUrl_main).into(addImage);
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(AddNewDoctorActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        degreeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    degree = degreeSP.getItemAtPosition(i).toString().trim();
                }else {
                    degree = "";
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        specialitySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    speciality = specialitySP.getItemAtPosition(i).toString().trim();
                }else {
                    speciality = "";
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startTV.setOnClickListener(this);
        endTV.setOnClickListener(this);

        addDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageUrl_main == null){
                    Toast.makeText(AddNewDoctorActivity.this, "Select a picture of doctor", Toast.LENGTH_SHORT).show();
                    return;
                }

                name = nameEdt.getText().toString();
                if (name.isEmpty()){
                    nameEdt.setError(getString(R.string.required_field));
                    nameEdt.requestFocus();
                    return;
                }

                if (degree.isEmpty()){
                    Toast.makeText(AddNewDoctorActivity.this, "Select degree", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (speciality.isEmpty()){
                    Toast.makeText(AddNewDoctorActivity.this, "Select speciality", Toast.LENGTH_SHORT).show();
                    return;
                }

                registration = registrationEdt.getText().toString();
                if (registration.isEmpty()){
                    registrationEdt.setError(getString(R.string.required_field));
                    registrationEdt.requestFocus();
                    return;
                }


                if(start.isEmpty() || end.isEmpty()){
                    Toast.makeText(AddNewDoctorActivity.this, "Time Schedule missing...!", Toast.LENGTH_SHORT).show();
                    return;
                }

                patient = patientEdt.getText().toString();
                if (patient.isEmpty()){
                    patientEdt.setError(getString(R.string.required_field));
                    patientEdt.requestFocus();
                    return;
                }

                block = blockEdt.getText().toString();
                if (block.isEmpty()){
                    block = "null";
                }

                floor = floorEdt.getText().toString();
                if (floor.isEmpty()){
                    floorEdt.setError(getString(R.string.required_field));
                    floorEdt.requestFocus();
                    return;
                }

                room = roomEdt.getText().toString();
                if (room.isEmpty()){
                    roomEdt.setError(getString(R.string.required_field));
                    roomEdt.requestFocus();
                    return;
                }

                fees = feesEdt.getText().toString();
                if (fees.isEmpty()){
                    feesEdt.setError(getString(R.string.required_field));
                    feesEdt.requestFocus();
                    return;
                }

                phone = phoneEdt.getText().toString();
                if (phone.isEmpty()){
                    phoneEdt.setError(getString(R.string.required_field));
                    phoneEdt.requestFocus();
                    return;
                }

                dialog.show();
                doctorID = doctorRef.push().getKey();
                doctorRegistration();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameEdt.getText().toString();
                registration = registrationEdt.getText().toString();
                patient = patientEdt.getText().toString();
                block = blockEdt.getText().toString();
                floor = floorEdt.getText().toString();
                room = roomEdt.getText().toString();
                fees = feesEdt.getText().toString();
                phone = phoneEdt.getText().toString();
                dialog.show();
                doctorRegistration();
            }
        });
    }

    private void doctorRegistration() {

        storageReference = FirebaseStorage.getInstance().getReference();
        final Uri imageUri = ImageUrl_main;
        final StorageReference imageRef = storageReference.child("HospitalDoctorImage").child(imageUri.getLastPathSegment());

        if (doctorKey.equals("yes")){
            image = ImageUrl_main.toString();
            DoctorDetails details = new DoctorDetails(doctorID, image, name, degree, speciality, registration, days, start, end, patient, block, floor, room, fees, phone);
            doctorRef.child(doctorID).child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(AddNewDoctorActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewDoctorActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(AddNewDoctorActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
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
                    image = downloadUri.toString();
                    DoctorDetails details = new DoctorDetails(doctorID, image, name, degree, speciality, registration, days, start, end, patient, block, floor, room, fees, phone);
                    doctorRef.child(doctorID).child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(AddNewDoctorActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddNewDoctorActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                dialog.dismiss();
                                Toast.makeText(AddNewDoctorActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

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
                .checkSelfPermission(AddNewDoctorActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(AddNewDoctorActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AddNewDoctorActivity.this,
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
        Intent intent = new Intent(AddNewDoctorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.stTime){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay>11) {
                                if (hourOfDay==12){
                                    startTV.setText(hourOfDay + ":" + minute+" PM");
                                    start = startTV.getText().toString();
                                }else {
                                    int cou = hourOfDay-12;
                                    startTV.setText(cou + ":" + minute+" PM");
                                    start = startTV.getText().toString();
                                }


                            }else {
                                startTV.setText(hourOfDay + ":" + minute+" AM");
                                start = startTV.getText().toString();
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }else if (view.getId()==R.id.endTime){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay>11) {
                                if (hourOfDay==12){
                                    endTV.setText(hourOfDay + ":" + minute+" PM");
                                    end = endTV.getText().toString();
                                }else {
                                    int cou = hourOfDay-12;
                                    endTV.setText(cou + ":" + minute+" PM");
                                    end = endTV.getText().toString();
                                }


                            }else {
                                endTV.setText(hourOfDay + ":" + minute+" AM");
                                end = endTV.getText().toString();
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }
    }
}
