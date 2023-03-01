package com.example.hospitalapp.bookingList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.hospitalapp.R;
import com.example.hospitalapp.adapter.BookingListAdapter;
import com.example.hospitalapp.pojo.DoctorDetails;
import com.example.hospitalapp.pojo.HospitalBooking;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingListActivity extends AppCompatActivity implements BookingListAdapter.BookingListAdapterListener, Booking_Done_Dialog.BookingDialogListener {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference adminRef;
    private DatabaseReference patientRef;
    private DatabaseReference hospitalDoctorBookingRef;
    private DatabaseReference hospitalDoctorBookingCompletedRef;

    private DoctorDetails doctor;
    private TextView doctorNameTV;

    private RecyclerView bookingRecycler;

    private BookingListAdapter adapter;

    private List<HospitalBooking> doctorBookingList = new ArrayList<HospitalBooking>();

    private String pID;

    private String bKey;

    private String bDate;

    private HospitalBooking hospitalBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        doctor = (DoctorDetails) getIntent().getSerializableExtra("DoctorDetails");

        doctorNameTV = findViewById(R.id.doctor_name_TV);

        doctorNameTV.setText("Dr. "+doctor.getName());

        bookingRecycler = findViewById(R.id.doctor_booking_list_recycler);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        adminRef  =rootRef.child("Admin");
        hospitalDoctorBookingRef = rootRef.child("Hospital").child(user.getUid()).child("DoctorList").child(doctor.getDoctorID()).child("Booking").child("Pending");
        hospitalDoctorBookingCompletedRef = rootRef.child("Hospital").child(user.getUid()).child("DoctorList").child(doctor.getDoctorID()).child("Booking").child("Completed");
        patientRef = rootRef.child("Patient");

        hospitalDoctorBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorBookingList.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    HospitalBooking hb = d.child("Value").getValue(HospitalBooking.class);
                    doctorBookingList.add(hb);
                }
                Collections.reverse(doctorBookingList);
                adapter = new BookingListAdapter(BookingListActivity.this, doctorBookingList);
                LinearLayoutManager llm = new LinearLayoutManager(BookingListActivity.this);
                llm.setOrientation(RecyclerView.VERTICAL);
                bookingRecycler.setLayoutManager(llm);
                bookingRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BookingListActivity.this, HospitalBookingDoctorListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCompleteBooking(HospitalBooking hb, String bookingKey, String bookingDate, String patientID) {
        hospitalBooking = hb;
        pID = patientID;
        bKey = bookingKey;
        bDate = bookingDate;

        Booking_Done_Dialog descriptionDialog = new Booking_Done_Dialog();
        descriptionDialog.show(getSupportFragmentManager(), "Confirmation: ");
    }

    @Override
    public void onSubmit() {

        adminRef.child("Hospital Completed Booking List").child(user.getUid()).child(bDate).child(bKey).child("Value").setValue(hospitalBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hospitalDoctorBookingCompletedRef.child(bKey).child("Value").setValue(hospitalBooking).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        rootRef.child("Hospital").child(user.getUid()).child("Completed").child(bKey).child("Value").setValue(hospitalBooking).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                patientRef.child(pID).child("HospitalBookingList").child(bKey).removeValue();

                                hospitalDoctorBookingRef.child(bKey).removeValue();
                            }
                        });
                    }
                });
            }
        });
    }
}
