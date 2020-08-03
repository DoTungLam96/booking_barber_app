package com.example.ungdungcattoc.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Adapter.BarberAdapter;
import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Common.SpaceItemDecoration;
import com.example.ungdungcattoc.Models.Barber;
import com.example.ungdungcattoc.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStepTwoFragment extends Fragment {

    @BindView(R.id.recycler_barber)
    RecyclerView recyclerBarber;

    Unbinder unbinder;
    BroadcastReceiver receiverDataFromFragment;

    private static BookingStepTwoFragment instance;

    public static BookingStepTwoFragment getInstance()
    {
        if (instance == null)
        {
            instance = new BookingStepTwoFragment();
        }
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_booking_step_2, null,false);
        unbinder = ButterKnife.bind(this,   view);
        initView();

        //region get data from broadcast BookingStepOneFragmetn
        receiverDataFromFragment = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Barber> barberArrayList = intent.getParcelableArrayListExtra(Common.KEY_DATA_STEP_1);
                BarberAdapter barberAdapter = new BarberAdapter(getContext(), R.layout.layout_baber_step_2, barberArrayList);
                recyclerBarber.setAdapter(barberAdapter);
            }
        };
        //endregion

        return  view;
    }

    private void initView() {
        recyclerBarber.setHasFixedSize(true);
        recyclerBarber.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerBarber.addItemDecoration(new SpaceItemDecoration(4));
    }

 //region register broadcast
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiverDataFromFragment, new IntentFilter(Common.ACTION_SEND_DATA_STEP_1));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiverDataFromFragment);
    }

    //endregion
}
