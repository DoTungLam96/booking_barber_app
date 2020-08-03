package com.example.ungdungcattoc.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ungdungcattoc.Fragments.BookingStepFourFragment;
import com.example.ungdungcattoc.Fragments.BookingStepOneFragment;
import com.example.ungdungcattoc.Fragments.BookingStepThreeFragment;
import com.example.ungdungcattoc.Fragments.BookingStepTwoFragment;

public class MyViewPageAdapter extends FragmentPagerAdapter {

    public MyViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 :
            {
               return BookingStepOneFragment.getInstance();
            }
            case 1 :
            {
                return BookingStepTwoFragment.getInstance();
            }
            case 2 :
            {
                return BookingStepThreeFragment.getInstance();
            }
            case 3 :
            {
                return BookingStepFourFragment.getInstance();
            }

            default: break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
