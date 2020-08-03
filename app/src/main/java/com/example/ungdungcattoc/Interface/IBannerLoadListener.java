package com.example.ungdungcattoc.Interface;

import com.example.ungdungcattoc.Models.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> bannerList);
}
