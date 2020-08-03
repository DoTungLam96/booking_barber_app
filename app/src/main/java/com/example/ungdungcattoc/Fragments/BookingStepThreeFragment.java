package com.example.ungdungcattoc.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Adapter.TimeSlotAdapter;
import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Common.SpaceItemDecoration;
import com.example.ungdungcattoc.Interface.ITimeSlotLoadListener;
import com.example.ungdungcattoc.Models.TimeSlot;
import com.example.ungdungcattoc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class BookingStepThreeFragment extends Fragment implements ITimeSlotLoadListener {

    Unbinder unbinder;
    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;
    @BindView(R.id.calenderView)
    HorizontalCalendarView calendarView;

    DocumentReference barberDoc;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog alertDialog;
    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver receiverDisplayTimeSlot;

    //region declare instance
    private static BookingStepThreeFragment instance;

    public static BookingStepThreeFragment getInstance()
    {
        if (instance == null)
        {
            instance = new BookingStepThreeFragment();
        }
        return instance;
    }
    //endregion


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iTimeSlotLoadListener = this;
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_booking_step_3, null,false);
        unbinder = ButterKnife.bind(this, view);

        initView(view);

        receiverDisplayTimeSlot = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              Calendar calendar = Calendar.getInstance();
              calendar.add(Calendar.DATE, 0); // add current date
              loadAvailableTimeSlot( (Common.currentBarber.getBarberID()), simpleDateFormat.format(calendar.getTime()) );
            }
        };

        return  view;
    }

    private void initView(View view) {
        recycler_time_slot.setHasFixedSize(true);
        recycler_time_slot.setLayoutManager(new GridLayoutManager(getActivity(), 3) );
        recycler_time_slot.addItemDecoration(new SpaceItemDecoration(8));

        //Calendar
        Calendar startCalendar = Calendar.getInstance();

        startCalendar.add(Calendar.DATE , 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE , 3);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calenderView)
                                                        .range(startCalendar, endCalendar)
                                                        .datesNumberOnScreen(1)
                                                        .mode(HorizontalCalendar.Mode.DAYS)
                                                        .defaultSelectedDate(startCalendar)
                                                        .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
               if (Common.currentDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.currentDate = date;
                    loadAvailableTimeSlot(Common.currentBarber.getBarberID(), simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    private void loadAvailableTimeSlot(String barberID, String date) {
         alertDialog.show();
         // AllSalon/30Shine/Branch/FWKvJ74vyLjLw6BGN6Ew/Barber/8lm00necFeLDgbJJOJCB
        barberDoc = FirebaseFirestore.getInstance().collection(Common.AllSalon)
                                                    .document(Common.NameSalon)
                                                    .collection(Common.Branch)
                                                    .document(Common.currentSalon.getSalonId())
                                                    .collection(Common.Barber)
                                                    .document(barberID);
        //get information from this barber
        barberDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot  snapshot = task.getResult();
                    if (snapshot.exists())
                    {
                       CollectionReference dateRef = FirebaseFirestore.getInstance().collection(Common.AllSalon)
                                .document(Common.NameSalon)
                                .collection(Common.Branch)
                                .document(Common.currentSalon.getSalonId())
                                .collection(Common.Barber)
                                .document(barberID)
                                .collection(date);
                       dateRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                              if (task.isSuccessful())
                              {
                                  QuerySnapshot queryDocumentSnapshots = task.getResult();

                                  //if not any booking to cut hair in day
                                  if (queryDocumentSnapshots.isEmpty())
                                  {
                                     iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                  }
                                  else
                                  {
                                      List<TimeSlot> timeSlotList = new ArrayList<>();
                                    try {
                                        for (QueryDocumentSnapshot timeSlotItem  : queryDocumentSnapshots)
                                        {
                                            timeSlotList.add(timeSlotItem.toObject(TimeSlot.class));
                                        }
                                    }
                                    catch (Exception ex)
                                    {
                                        Log.e("errorapp", ex.getMessage());
                                    }

                                      iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlotList);
                                  }
                              }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getActivity(), "Error fragemnt 3 : "+e.getMessage()  , Toast.LENGTH_SHORT).show();
                           }
                       });

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error fragment 3 : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(R.layout.layout_time_slot_step_3, getContext(), timeSlotList );
        recycler_time_slot.setAdapter(timeSlotAdapter);
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(R.layout.layout_time_slot_step_3, getContext(), new ArrayList<>() );
        recycler_time_slot.setAdapter(timeSlotAdapter);
        alertDialog.dismiss();
    }

    //region register broadcast
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiverDisplayTimeSlot, new IntentFilter(Common.ACTION_DISPLAY_TIME_SLOT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiverDisplayTimeSlot);
    }
    //endregion
}
