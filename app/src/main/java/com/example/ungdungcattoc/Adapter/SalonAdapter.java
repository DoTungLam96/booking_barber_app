package com.example.ungdungcattoc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Models.Salon;
import com.example.ungdungcattoc.Models.SalonView;
import com.example.ungdungcattoc.R;

import java.util.ArrayList;
import java.util.List;

public class SalonAdapter  extends RecyclerView.Adapter<SalonAdapter.ViewHolder> {
    private Context context;
    private int layout;
    private List<Salon> salonList;
    List<SalonView> listSalonView;
    public SalonAdapter(Context context, int layout, List<Salon> salonList) {
        this.context = context;
        this.layout = layout;
        this.salonList = salonList;
        listSalonView = new ArrayList<>();
    }

    @NonNull
    @Override
    public SalonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, null);
        SalonAdapter.ViewHolder viewHolder = new SalonAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         holder.txt_name_salon.setText(salonList.get(position).getName());
         holder.txt_address_salon.setText(salonList.get(position).getAddress());

         listSalonView.add(new SalonView(holder.cardView,holder.txt_name_salon,holder.txt_address_salon));

        //event click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set white background color if item is'nt selected


                for (SalonView salonView : listSalonView )
                {
                    salonView.getCardView().setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                    salonView.getTextViewName().setTextColor(context.getResources().getColor(android.R.color.black) );
                    salonView.getTextViewAddress().setTextColor(context.getResources().getColor(android.R.color.black) );
                }
                //set background color for item is selected
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.slotTimeBackground));
                holder.txt_name_salon.setTextColor(context.getResources().getColor(android.R.color.white) );
                holder.txt_address_salon.setTextColor(context.getResources().getColor(android.R.color.white) );

                //send broadcast to tell Booking activity to enable button Next
                Intent intent = new Intent();
                intent.setAction(Common.ACTION_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE, salonList.get(position));
                intent.putExtra(Common.KEY_STEP, 1);
                context.sendBroadcast(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name_salon, txt_address_salon;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name_salon = itemView.findViewById(R.id.txt_name_salon);
            txt_address_salon = itemView.findViewById(R.id.txt_address_salon);
            cardView = itemView.findViewById(R.id.card_salon);
        }
    }
}
