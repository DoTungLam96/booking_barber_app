package com.example.ungdungcattoc.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungcattoc.Adapter.SalonAdapter;
import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Common.SpaceItemDecoration;
import com.example.ungdungcattoc.Interface.IAllSalonLoadListener;
import com.example.ungdungcattoc.Interface.IBranchLoadListener;
import com.example.ungdungcattoc.Models.Salon;
import com.example.ungdungcattoc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStepOneFragment extends Fragment implements IAllSalonLoadListener, IBranchLoadListener {

    CollectionReference allSalonRef;
    CollectionReference branchRef;
    IAllSalonLoadListener iAllSalonLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.recycler_all_salon)
    RecyclerView recyclerAllSalon;
    @BindView(R.id.spinner)
    MaterialSpinner materialSpinner;

    Unbinder unbinder;

    AlertDialog spotsDialog;

    private static BookingStepOneFragment instance;

    public static BookingStepOneFragment getInstance()
    {
        if (instance == null)
        {
           instance = new BookingStepOneFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iAllSalonLoadListener = this;
        iBranchLoadListener = this;
        allSalonRef = FirebaseFirestore.getInstance().collection(Common.AllSalon);
         spotsDialog = new SpotsDialog.Builder().setContext(getActivity()).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_booking_step_1_layout, null,false);
        unbinder = ButterKnife.bind(this, view);

        //init view
        initView();

        loadAllSalon();

        return  view;
    }

    private void initView() {
        recyclerAllSalon.setHasFixedSize(true);
        recyclerAllSalon.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerAllSalon.addItemDecoration(new SpaceItemDecoration(4));
    }

    //region load item into Spinner
    private void loadAllSalon() {
        allSalonRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful())
               {
                   List<String> list = new ArrayList<>();
                   list.add("Please choose salon");
                   for (DocumentSnapshot snapshotSalon : task.getResult())
                   {
                       list.add(snapshotSalon.getId());
                   }
                   iAllSalonLoadListener.onLoadAllSalonSuccess(list);
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onLoadAllSalonSuccess(List<String> areaNameList) {
        materialSpinner.setItems(areaNameList);
        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0)
                {
                    recyclerAllSalon.setVisibility(View.VISIBLE);
                    loadBranchItemSalon(item.toString());
                }
                else
                {
                    recyclerAllSalon.setVisibility(View.GONE);
                }
            }
        });
    }

    //endregion

    //region load item into Recycler_all_salon

    private void loadBranchItemSalon(String itemId)
    {
        spotsDialog.show();
        Common.NameSalon = itemId;
        branchRef = FirebaseFirestore.getInstance().collection(Common.AllSalon)
                                                    .document(itemId)
                                                    .collection(Common.Branch);
        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    List<Salon> salonList  = new ArrayList<>();
                    for (DocumentSnapshot snapShotItem : task.getResult())
                    {   Salon salon = snapShotItem.toObject(Salon.class);
                        salon.setSalonId(snapShotItem.getId());
                        salonList.add(salon);
                    }
                    iBranchLoadListener.onBranchLoadSuccess(salonList);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                spotsDialog.dismiss();
            }
        });
    }

    @Override
    public void onBranchLoadSuccess(List<Salon> listBranch) {
        SalonAdapter salonAdapter = new SalonAdapter(getActivity(), R.layout.layout_all_salon_step1, listBranch);
        recyclerAllSalon.setAdapter(salonAdapter);

        spotsDialog.dismiss();
    }

    //endregion

}
