package com.example.ungdungcattoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.ungdungcattoc.Adapter.MyViewPageAdapter;
import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Models.Barber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;
    @BindView(R.id.step_view)
    StepView step_view;
    @BindView(R.id.view_pager)
    ViewPager view_Pager;

    AlertDialog alertDialog;
    CollectionReference barberRef;

    //region  event button btnPrevious
    @OnClick(R.id.btn_previous_step)
    void previousOnClick()
    {
        if (Common.Step == 3 || Common.Step > 0)
        {
            Common.Step--;
            view_Pager.setCurrentItem(Common.Step);
            btn_next_step.setEnabled(true);
            setColorButton();
        }
    }
    //endregion

    //region  event button btnNext
    @OnClick(R.id.btn_next_step)
    void nextOnClick()
    {
        if (Common.Step < 3 || Common.Step == 0)
        {
            Common.Step++;

            //Step one : after choose Salon
            if (Common.Step == 1)
            {
               if (Common.currentSalon != null)
               {
                   loadBarberBySalon(Common.currentSalon.getSalonId());
               }
            }
            else if (Common.Step == 2)
            {
                //Step one : after choose Salon
                if (Common.currentBarber != null)
                {
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberID());
                }
            }
            view_Pager.setCurrentItem(Common.Step);
        }

        if (Common.Step == 3)
        {
            if (Common.currentTimeSlot != -1)
            {
                //send broadcast confirm to fragment 4
                Intent intentConfirm = new Intent(Common.ACTION_CONFIRM_BOOKING);
                sendBroadcast(intentConfirm);
            }
            btn_next_step.setEnabled(false);
            setColorButton();
        }
    }

    //region Send data to fragemnt 3
    private void loadTimeSlotOfBarber(String barberID) {
        Intent intent = new Intent();
        intent.setAction(Common.ACTION_DISPLAY_TIME_SLOT);
        sendBroadcast(intent);
    }
    //endregion

    //region load barber of salon is selected
    private void loadBarberBySalon(String salonId) {
         alertDialog.show();

         //Now, select all barber of salon
        if (!TextUtils.isEmpty(Common.NameSalon))
        {
            barberRef = FirebaseFirestore.getInstance().collection(Common.AllSalon)
                    .document(Common.NameSalon)
                    .collection(Common.Branch)
                    .document(salonId)
                    .collection(Common.Barber);
            barberRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Barber> barberList = new ArrayList<>();
                    if (task.isSuccessful())
                    {
                        for (DocumentSnapshot snapshotItem : task.getResult())
                        {
                              Barber barber = snapshotItem.toObject(Barber.class);
                              barber.setPassword("");
                              barber.setBarberID(snapshotItem.getId());

                              barberList.add(barber);
                        }

                        //Send broadCast to Fragment 2 to load item
                        Intent intent = new Intent(Common.ACTION_SEND_DATA_STEP_1);
                        intent.putParcelableArrayListExtra(Common.KEY_DATA_STEP_1, (ArrayList<? extends Parcelable>) barberList);
                        sendBroadcast(intent);

                        alertDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BookingActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //endregion

    //endregion

    //Broadcast receiver
    private BroadcastReceiver buttonNextReceiver ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        //region setup Stepview
        setUpStepView();
        setColorButton();
        //endregion

        //region handle broadcast Receicer
        buttonNextReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int step  = intent.getIntExtra(Common.KEY_STEP, 0);
                if (step == 1)
                {
                    Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
                }
                else if (step == 2)
                {
                    Common.currentBarber = intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
                }
                else if (step == 3)
                {
                   Common.currentTimeSlot = intent.getIntExtra(Common.KEY_SLOT_TIME, -1);
                }

                btn_next_step.setEnabled(true);
                setColorButton();
            }
        };
        //endregion

        //region setup Viewpager
        view_Pager.setAdapter(new MyViewPageAdapter(getSupportFragmentManager()));

        //Keep state of 4 screen on fragment avoid re-loading view
        view_Pager.setOffscreenPageLimit(4);

        view_Pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                step_view.go(position, true);
               if (position == 0 )
               {
                   btn_previous_step.setEnabled(false);
               }
               else
               {
                   btn_previous_step.setEnabled(true);
               }
               btn_next_step.setEnabled(false);
               setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //endregion
    }

    //region set color button previous and next when enable
    private void setColorButton() {
      if (btn_next_step.isEnabled())
      {
          btn_next_step.setBackgroundResource(R.color.colorButton);
      }
      else
      {
          btn_next_step.setBackgroundResource(android.R.color.darker_gray);
      }
        if (btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }
    //endregion

    private void setUpStepView() {
       step_view.setSteps(Common.itemStepView);
    }

    //region OnStart and on Destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(buttonNextReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register to listen Broadcast
        registerReceiver(buttonNextReceiver, new IntentFilter(Common.ACTION_ENABLE_BUTTON_NEXT));
    }
    //endregion
}