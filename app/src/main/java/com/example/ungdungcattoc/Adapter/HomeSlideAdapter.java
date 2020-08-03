package com.example.ungdungcattoc.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ungdungcattoc.Models.Banner;
import com.example.ungdungcattoc.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HomeSlideAdapter extends SliderViewAdapter<HomeSlideAdapter.SliderAdapterVH> {
    private Context context;
    private List<Banner> mSliderItems = new ArrayList<>();

    public HomeSlideAdapter(Context context, List<Banner> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public HomeSlideAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<Banner> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Banner sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Picasso.get().load(mSliderItems.get(position).getImage()).into(viewHolder.imgHinh);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView imgHinh;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imgHinh = itemView.findViewById(R.id.imgHinhSlider);
        }
    }

}
