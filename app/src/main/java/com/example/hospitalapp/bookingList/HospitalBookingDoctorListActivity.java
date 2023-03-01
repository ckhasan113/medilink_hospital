package com.example.hospitalapp.bookingList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.hospitalapp.LoadingDialog;
import com.example.hospitalapp.MainActivity;
import com.example.hospitalapp.R;
import com.example.hospitalapp.adapter.DoctorListAdapter;
import com.example.hospitalapp.pojo.DoctorDetails;
import com.example.hospitalapp.pojo.HospitalBooking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HospitalBookingDoctorListActivity extends AppCompatActivity implements DoctorListAdapter.DoctorDetailsListener {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference hospitalDoctorRef;
    private DatabaseReference hospitalDoctorBookingRef;

    private Toolbar searchBar;

    private LoadingDialog dialog;

    private LinearLayoutManager llm;

    private RecyclerView hospitalDoctorRecycler;

    private DoctorListAdapter adapter;

    private List<DoctorDetails> doctorDetailsList = new ArrayList<DoctorDetails>();

    private List<HospitalBooking> doctorBookingList = new ArrayList<HospitalBooking>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_booking_doctor_list);

        dialog = new LoadingDialog(HospitalBookingDoctorListActivity.this,"Loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        hospitalDoctorRef = rootRef.child("Hospital").child(user.getUid()).child("DoctorList");

        hospitalDoctorRecycler = findViewById(R.id.hospital_booking_doctor_list_recycler);

        searchBar = findViewById(R.id.search_bar_hospital_booking_doctor_list);
        setSupportActionBar(searchBar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\" style=\"text-align: center\">" + "Doctor List" + "</font>"));

        hospitalDoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorDetailsList.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    DoctorDetails doc = d.child("Details").getValue(DoctorDetails.class);
                    doctorDetailsList.add(doc);
                }
                adapter = new DoctorListAdapter(HospitalBookingDoctorListActivity.this, doctorDetailsList);
                llm = new LinearLayoutManager(HospitalBookingDoctorListActivity.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);

                hospitalDoctorRecycler.setLayoutManager(llm);
                hospitalDoctorRecycler.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.item_search).getActionView();

        if (null != searchView) {
            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            searchView.setPadding(3,0,0,0);
            searchView.setQueryHint("Search by full name...");

        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final String dName = s.toLowerCase();
                dialog.show();
                hospitalDoctorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        doctorDetailsList.clear();
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            DoctorDetails doc = d.child("Details").getValue(DoctorDetails.class);
                            String name = doc.getName().toLowerCase();
                            if (dName.equals(name)){
                                doctorDetailsList.add(doc);
                            }
                        }
                        adapter = new DoctorListAdapter(HospitalBookingDoctorListActivity.this, doctorDetailsList);
                        llm = new LinearLayoutManager(HospitalBookingDoctorListActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);

                        hospitalDoctorRecycler.setLayoutManager(llm);
                        hospitalDoctorRecycler.setAdapter(adapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public void onDoctorDetails(final DoctorDetails doctorDetails) {
        hospitalDoctorBookingRef = hospitalDoctorRef.child(doctorDetails.getDoctorID()).child("Booking");
        hospitalDoctorBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorBookingList.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    HospitalBooking hb = d.child("Value").getValue(HospitalBooking.class);
                    doctorBookingList.add(hb);
                }
                if (doctorBookingList.size() != 0){
                    Intent intent = new Intent(HospitalBookingDoctorListActivity.this, BookingListActivity.class);
                    intent.putExtra("DoctorDetails", doctorDetails);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(HospitalBookingDoctorListActivity.this, "No appointment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HospitalBookingDoctorListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
