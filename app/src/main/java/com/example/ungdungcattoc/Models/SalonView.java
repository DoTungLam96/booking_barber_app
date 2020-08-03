package com.example.ungdungcattoc.Models;

import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class SalonView {
    private CardView cardView;
    private TextView textViewName,textViewAddress;

    public SalonView(CardView cardView, TextView textViewName, TextView textViewAddress) {
        this.cardView = cardView;
        this.textViewName = textViewName;
        this.textViewAddress = textViewAddress;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public TextView getTextViewName() {
        return textViewName;
    }

    public void setTextViewName(TextView textViewName) {
        this.textViewName = textViewName;
    }

    public TextView getTextViewAddress() {
        return textViewAddress;
    }

    public void setTextViewAddress(TextView textViewAddress) {
        this.textViewAddress = textViewAddress;
    }
}
