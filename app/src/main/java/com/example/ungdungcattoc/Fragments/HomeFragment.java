package com.example.ungdungcattoc.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ungdungcattoc.Adapter.HomeSlideAdapter;
import com.example.ungdungcattoc.Adapter.LookBookAdapter;
import com.example.ungdungcattoc.BookingActivity;
import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Interface.IBannerLoadListener;
import com.example.ungdungcattoc.Interface.ILookBookLoadListener;
import com.example.ungdungcattoc.MainActivity;
import com.example.ungdungcattoc.Models.Banner;
import com.example.ungdungcattoc.R;
import com.example.ungdungcattoc.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ss.com.bannerslider.Slider;

public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener {

    private Unbinder unbinder;

    @BindView(R.id.layout_contain_information)
    LinearLayout liner_Information;
    @BindView(R.id.txt_username_show)
    TextView txt_user_information;
    @BindView(R.id.imageSlider)
    SliderView slider_banner;
    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;
    @OnClick(R.id.card_view_booking)
    void onClickBooking()
    {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }


    //FireStore
    CollectionReference bannerRef, lookBookRef;
    //init Interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;

    public HomeFragment()
    {
        bannerRef = FirebaseFirestore.getInstance().collection(Common.Banner);
        lookBookRef = FirebaseFirestore.getInstance().collection(Common.Lookbook);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        iBannerLoadListener = this;
        iLookBookLoadListener = (ILookBookLoadListener) this;

        //init slider
        Slider.init(new PicassoImageLoadingService());

        //Check user is logged
        if (MainActivity.mAuth.getCurrentUser() != null && !MainActivity.mAuth.getCurrentUser().getPhoneNumber().isEmpty())
        {
            setUserInformation();
            loadBanner();
            loadLookBook();
        }

        return view;
    }

    //region Get data to load banner
    private void loadBanner() {
        bannerRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   List<Banner> bannerList = new ArrayList<>();
                   if (task.isSuccessful())
                   {
                       for (DocumentSnapshot bannerItem : task.getResult())
                       {
                           Banner banner = bannerItem.toObject(Banner.class);
                           bannerList.add(banner);
                       }
                       iBannerLoadListener.onBannerLoadSuccess(bannerList);
                   }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> bannerList) {
        slider_banner.setSliderAdapter(new HomeSlideAdapter(getContext(), bannerList));
        slider_banner.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        slider_banner.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        slider_banner.setIndicatorSelectedColor(Color.WHITE);
        slider_banner.setIndicatorUnselectedColor(Color.GRAY);
        slider_banner.setScrollTimeInSec(5); //set scroll delay in seconds :
        slider_banner.startAutoCycle();
    }
   //endregion

    //region Get data to load lookbook

    private void loadLookBook()
    {
        lookBookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Banner> lookBookList = new ArrayList<>();
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot bannerItem : task.getResult())
                    {
                        Banner banner = bannerItem.toObject(Banner.class);
                        lookBookList.add(banner);
                    }
                    iLookBookLoadListener.onLookBookLoadSuccess(lookBookList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLookBookLoadSuccess(List<Banner> lookBookList) {
          recycler_look_book.setHasFixedSize(true);
          recycler_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
          recycler_look_book.setAdapter(new LookBookAdapter(getActivity(), R.layout.custom_layout_look_book, lookBookList));
    }
    //endregion

    //region display information current user
    private void setUserInformation() {
        liner_Information.setVisibility(View.VISIBLE);
        txt_user_information.setText(Common.currentUser.getName());
    }
    //endregion

}