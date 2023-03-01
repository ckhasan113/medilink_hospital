package com.example.hospitalapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalapp.R;
import com.example.hospitalapp.pojo.HospitalBooking;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingListAdapterViewHolder> {

    private Context context;
    private List<HospitalBooking> bookingList;
    private BookingListAdapterListener listener;

    public BookingListAdapter(Context context, List<HospitalBooking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        listener = (BookingListAdapterListener) context;
    }

    @NonNull
    @Override
    public BookingListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingListAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.booking_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingListAdapterViewHolder holder, int position) {

        final HospitalBooking hb = bookingList.get(position);

        holder.patientNameTV.setText("Mr. "+hb.getPatientFirstName()+" "+hb.getPatientLastName());
        holder.descriptionTV.setText("Description:\n"+hb.getDescription());
        holder.dateTV.setText(hb.getBookingDate());
        Picasso.get().load(Uri.parse(hb.getPatientImage())).into(holder.patientImageIV);

        holder.bookingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCompleteBooking(hb, hb.getBookingID(), hb.getBookingDate(), hb.getPatientID());
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingListAdapterViewHolder extends RecyclerView.ViewHolder{

        CardView bookingLayout;
        ImageView patientImageIV;
        TextView patientNameTV, descriptionTV, dateTV;

        public BookingListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingLayout = itemView.findViewById(R.id.bookingList);
            patientImageIV = itemView.findViewById(R.id.bookingPatientIV);
            patientNameTV = itemView.findViewById(R.id.bookingPatientNameTV);
            descriptionTV = itemView.findViewById(R.id.bookingPatientDescriptionTV);
            dateTV = itemView.findViewById(R.id.bookingDateTV);
        }
    }

    public interface BookingListAdapterListener{
        void onCompleteBooking(HospitalBooking hb, String bookingKey, String bookingDate, String patientID);
    }
}
