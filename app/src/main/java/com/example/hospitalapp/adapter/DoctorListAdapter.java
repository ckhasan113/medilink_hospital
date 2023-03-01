package com.example.hospitalapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalapp.R;
import com.example.hospitalapp.pojo.DoctorDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListAdapterViewHolder> {

    private Context context;
    private List<DoctorDetails> doctorDetailsList;

    private DoctorDetailsListener listener;

    public DoctorListAdapter(Context context, List<DoctorDetails> doctorDetailsList) {
        this.context = context;
        this.doctorDetailsList = doctorDetailsList;
        listener = (DoctorDetailsListener) context;
    }

    @NonNull
    @Override
    public DoctorListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DoctorListAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.doctor_list_recycler_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListAdapterViewHolder holder, int position) {
        final DoctorDetails doctor = doctorDetailsList.get(position);

        Uri photoUri = Uri.parse(doctor.getImage());
        Picasso.get().load(photoUri).into(holder.docImage);
        holder.docName.setText("Dr. "+doctor.getName());
        holder.docDegree.setText(doctor.getDegree());
        holder.docSpeciality.setText(doctor.getSpeciality());
        holder.docAppointment.setText(doctor.getPatientCount()+" Appointments");

        holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDoctorDetails(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorDetailsList.size();
    }

    class DoctorListAdapterViewHolder extends RecyclerView.ViewHolder{

        LinearLayout getDetailsLayout;

        ImageView docImage;
        TextView docName, docDegree, docSpeciality, docAppointment;
        public DoctorListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            getDetailsLayout = itemView.findViewById(R.id.getDoctorDetails);

            docImage = itemView.findViewById(R.id.doctor_image);
            docName = itemView.findViewById(R.id.doctor_name);
            docDegree = itemView.findViewById(R.id.doctor_degree);
            docSpeciality = itemView.findViewById(R.id.doctor_speciality);
            docAppointment = itemView.findViewById(R.id.doctor_appointment_number);
        }
    }

    public interface DoctorDetailsListener{
        void onDoctorDetails(DoctorDetails doctorDetails);
    }
}
