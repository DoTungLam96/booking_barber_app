package com.example.ungdungcattoc.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Models.Banner;
import com.example.ungdungcattoc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LookBookAdapter extends RecyclerView.Adapter<LookBookAdapter.ViewHolder> {
    private Context context;
    private int layout;
    private List<Banner> lookBookList;

    public LookBookAdapter(Context context, int layout, List<Banner> lookBookList) {
        this.context = context;
        this.layout = layout;
        this.lookBookList = lookBookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(lookBookList.get(position).getImage()).into(holder.img_look_book);

        //event click item in recycler_view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, lookBookList.get(position).getImage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lookBookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_look_book;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_look_book = itemView.findViewById(R.id.img_look_book);
        }
    }
}
