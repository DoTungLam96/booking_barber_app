package com.example.ungdungcattoc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Models.Salon;
import com.example.ungdungcattoc.Models.SalonView;
import com.example.ungdungcattoc.Models.TimeSlot;
import com.example.ungdungcattoc.R;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private int layout;
    private Context context;
    private List<TimeSlot> timeSlotList;
    private List<SalonView> listSalonView;

    public TimeSlotAdapter(int layout, Context context, List<TimeSlot> timeSlotList) {
        this.layout = layout;
        this.context = context;
        this.timeSlotList = timeSlotList;
        listSalonView = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_time_slot_step_3, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.txt_time_slot.setText(new StringBuilder(  Common.convertTimeSlotToString(position)  ));

       //if all position is available, just show list
       if (timeSlotList.size() == 0)
       {
          holder.txt_description.setText(Common.Available);
          holder.txt_description.setTextColor(context.getResources().getColor(android.R.color.black));

           holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
           holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
       }
       else
       {
           for (TimeSlot timeSlot : timeSlotList)
           {
               int slot = Integer.parseInt(timeSlot.getSlot().toString());
               if (slot == position)
               {
                   //set cannot select item with status FULL
                   holder.cardView.setTag(Common.DISABLE_TAG);

                   holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                   holder.txt_description.setText(Common.Full);
                   holder.txt_description.setTextColor(context.getResources().getColor(android.R.color.white));
                   holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
               }
           }
       }

       //enable item is selected
        listSalonView.add(new SalonView(holder.cardView,holder.txt_time_slot, holder.txt_description));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag() != null) return;
                    for (SalonView salonView : listSalonView)
                    {
                        if (salonView.getCardView().getTag() == null)
                        {
                            salonView.getCardView().setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                            salonView.getTextViewName().setTextColor(context.getResources().getColor(android.R.color.black) );
                            salonView.getTextViewAddress().setTextColor(context.getResources().getColor(android.R.color.black) );
                        }
                    }
                    //set color for item is selected
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.slotTimeBackground));
                    holder.txt_description.setTextColor(context.getResources().getColor(android.R.color.white) );
                    holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white) );
                    //send broadcast
                    Intent intent = new Intent(Common.ACTION_ENABLE_BUTTON_NEXT);
                    intent.putExtra(Common.KEY_SLOT_TIME, position);
                    intent.putExtra(Common.KEY_STEP, 3);
                    context.sendBroadcast(intent);
                }
            });


    }

    @Override
    public int getItemCount() {
        return Common.TIME_TOTAL_SLOT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txt_time_slot, txt_description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot);
            txt_description = itemView.findViewById(R.id.txt_time_slot_description);
        }
    }
}
