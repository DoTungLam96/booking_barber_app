package com.example.ungdungcattoc.Common;

import android.content.Intent;
import android.os.Parcelable;

import com.example.ungdungcattoc.Models.Barber;
import com.example.ungdungcattoc.Models.Salon;
import com.example.ungdungcattoc.Models.TimeSlot;
import com.example.ungdungcattoc.Models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Common {
    public static  final String Available = "Available";
    public static  final String Full = "Full";
    public static  final String Banner = "Banner";
    public static  final String Lookbook = "Lookbook";
    public static  final String AllSalon = "AllSalon";
    public static  final String Barber = "Barber";
    public static  final String Branch = "Branch";
    public static final String ACTION_ENABLE_BUTTON_NEXT = "ACTION_ENABLE_BUTTON_NEXT";
    public static final String ACTION_SEND_DATA_STEP_1 = "ACTION STEP 1";

    public static final String KEY_DATA_STEP_1 = "KEY 1";
    public static final String KEY_SALON_STORE = "KEY_SALON_STORE";
    public static final String ACTION_DISPLAY_TIME_SLOT = "ACTION_DISPLAY_TIME_SLOT" ;
    public static final String KEY_STEP = "KEY_STEP" ;
    public static final String KEY_BARBER_SELECTED = "KEY_BARBER_SELECTED" ;
    public static final int TIME_TOTAL_SLOT = 17 ;
    public static final String KEY_SLOT_TIME = "KEY_SLOT_TIME";
    public static final String ACTION_CONFIRM_BOOKING = "ACTION_CONFIRM_BOOKING";
    public  static String IS_LOGIN = "IsLogin";
    public static User currentUser;
    public static Object DISABLE_TAG ="Disble";

    //item Stepview in BookingActivity
    public static ArrayList<String> itemStepView = new ArrayList<String>(
            Arrays.asList(
                    "Salon",
                    "Barber",
                    "Time",
                    "Confirm"
            )
    );
    public static Salon currentSalon;
    public static int Step = 0;
    public static String NameSalon;
    public static Barber currentBarber;
    public static int currentTimeSlot = -1;
    public static Calendar currentDate = Calendar.getInstance();

    public static String convertTimeSlotToString(int slot) {
        switch (slot)
        {
            case 0 :
                return "9:00 - 9:30";
            case 1 :
                return "9:30 - 10:00";
            case 2 :
                return "10:00 - 10:30";
            case 3 :
                return "10:30 - 11:00";
            case 4 :
                return "11:00 - 11:30";
            case 5 :
                return "13:30 - 14:00";
            case 6 :
                return "14:00 - 14:30";
            case 7 :
                return "14:30 - 15:00";
            case 8 :
                return "15:00 - 15:30";
            case 9 :
                return "16:30 - 17:00";
            case 10 :
                return "17:00 - 17:30";
            case 11 :
                return "17:30 - 18:00";
            case 12 :
                return "18:00 -18:30";
            case 13 :
                return "18:30 - 19:00";
            case 14 :
                return "19:00 - 19:30";
            case 15 :
                return "19:30 - 20:00";
            case 16 :
                return "20:00 - 20:30";
            case 17 :
                return "20:30 - 21:00";

            default:
                return "CLOSE !";

        }
    }

}
