package com.example.ungdungcattoc.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Models.BookingInformation;
import com.example.ungdungcattoc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BookingStepFourFragment extends Fragment {

    //region declare view
    @BindView(R.id.txt_salon_location)
    TextView txt_salon_location;
    @BindView(R.id.txt_booking_time)
    TextView txt_booking_time;
    @BindView(R.id.txt_booking_user)
    TextView txt_booking_user;
    @BindView(R.id.txt_salon_name)
    TextView txt_salon_name;
    @BindView(R.id.txt_salon_time_open)
    TextView txt_salon_time_open;
    @BindView(R.id.txt_salon_website)
    TextView txt_salon_website;
    //endregion

    SimpleDateFormat simpleDateFormat;
    Unbinder unbinder;
    BroadcastReceiver receiverConfirmBooking;

    //region Event click button confirm
     @OnClick(R.id.btn_confirm)
     void confirmBooking()
     {
         //create booking information
         BookingInformation bookingInformation = new BookingInformation();
         bookingInformation.setBarberID(Common.currentBarber.getBarberID());
         bookingInformation.setCustomerName(Common.currentUser.getName());
         bookingInformation.setCustomerPhone(Common.currentUser.getPhone());
         bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
         bookingInformation.setSalonId(Common.currentSalon.getSalonId());
         bookingInformation.setSalonName(Common.currentSalon.getName());
         bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                 .append(" at ")
                 .append(simpleDateFormat.format(Common.currentDate.getTime()) ).toString() );
         bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

         //submit to firebase
         DocumentReference dateRef = FirebaseFirestore.getInstance().collection(Common.AllSalon)
                 .document(Common.NameSalon)
                 .collection(Common.Branch)
                 .document(Common.currentSalon.getSalonId())
                 .collection(Common.Barber)
                 .document(Common.currentBarber.getBarberID())
                 .collection(new SimpleDateFormat("dd_MM_yyyy").format(Common.currentDate.getTime() ))
                 .document(String.valueOf(Common.currentTimeSlot));
         //wirte date
         dateRef.set(bookingInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 getActivity().finish();
                 Toast.makeText(getContext(), "Success !", Toast.LENGTH_SHORT).show();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(getActivity(), "Error fragment 4: "+e.getMessage() , Toast.LENGTH_SHORT).show();
             }
         });
     }
    //endregion

    //region declare fragment
    private static BookingStepFourFragment instance;

    public static BookingStepFourFragment getInstance()
    {
        if (instance == null)
        {
            instance = new BookingStepFourFragment();
        }
        return instance;
    }
    //endregion
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_booking_step_4, null,false);
        unbinder = ButterKnife.bind(this, view);

        //region handle broadcast reciver
         receiverConfirmBooking = new BroadcastReceiver() {
             @Override
             public void onReceive(Context context, Intent intent) {
                 txt_booking_user.setText(Common.currentBarber.getName());
                 txt_booking_time.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                                          .append(" at ")
                                           .append(simpleDateFormat.format(Common.currentDate.getTime())) );
                 txt_salon_location.setText(Common.currentSalon.getAddress());
                 txt_salon_time_open.setText(Common.currentSalon.getOpenHour());
                 txt_salon_website.setText(Common.currentSalon.getWebsite());
                 txt_salon_name.setText(Common.currentSalon.getName());
             }
         };
        //endregion

        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }


    //region register broadcast
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiverConfirmBooking, new IntentFilter(Common.ACTION_CONFIRM_BOOKING));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiverConfirmBooking);
    }
    //endregion
}
