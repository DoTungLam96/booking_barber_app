package com.example.ungdungcattoc.Interface;

import com.example.ungdungcattoc.Models.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> listBranch);
}
