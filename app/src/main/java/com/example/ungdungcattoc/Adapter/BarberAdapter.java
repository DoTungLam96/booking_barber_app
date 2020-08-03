package com.example.ungdungcattoc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Models.Banner;
import com.example.ungdungcattoc.Models.Barber;
import com.example.ungdungcattoc.Models.SalonView;
import com.example.ungdungcattoc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.ViewHolder> {
    private Context context;
    private int layout;
    private List<Barber> barberList;
    List<SalonView> listSalonView;

    public BarberAdapter(Context context, int layout, List<Barber> lookBookList) {
        this.context = context;
        this.layout = layout;
        this.barberList = lookBookList;
        listSalonView = new ArrayList<>();
    }

    @NonNull
    @Override
    public BarberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, null);
        BarberAdapter.ViewHolder viewHolder = new BarberAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.txt_name.setText(barberList.get(position).getName());
       holder.rtb_rating.setRating(barberList.get(position).getRating());

        listSalonView.add(new SalonView(holder.cardView,holder.txt_name,null));
       //handle click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (SalonView salonView : listSalonView )
                {
                    salonView.getCardView().setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                    salonView.getTextViewName().setTextColor(context.getResources().getColor(android.R.color.black));
                }
                //set background color for item is selected
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.slotTimeBackground));
                    holder.txt_name.setTextColor(context.getResources().getColor(android.R.color.white));
                //send broadcast to tell Booking activity to enable button Next
                Intent intent = new Intent();
                intent.setAction(Common.ACTION_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED, barberList.get(position));
                intent.putExtra(Common.KEY_STEP, 2);
                context.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name;
        RatingBar rtb_rating;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            rtb_rating = itemView.findViewById(R.id.rtb_barber);
            cardView = itemView.findViewById(R.id.card_view_barber);
        }
    }
}
