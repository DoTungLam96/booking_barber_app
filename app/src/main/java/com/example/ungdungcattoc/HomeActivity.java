package com.example.ungdungcattoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Fragments.HomeFragment;
import com.example.ungdungcattoc.Fragments.ShoppingFragment;
import com.example.ungdungcattoc.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {

    boolean isLogin;
    boolean isCheckMail;
    AlertDialog dialogSpot;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);
        dialogSpot = new SpotsDialog.Builder().setContext(this).build();
        //init
        userRef = FirebaseFirestore.getInstance().collection("User");

        getDataFromMain();

        //region init and set action NavigationBottom
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home)
                {
                    fragment = new HomeFragment();
                }
                else if (item.getItemId() == R.id.action_shopping)
                {
                    fragment = new ShoppingFragment();
                }
                return loadFragment(fragment);
            }
        });

      //  bottomNavigationView.setSelectedItemId(R.id.action_home);
        //endregion
    }

    //region show fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return  true;
        }
        return  false;
    }
    //endregion

    //region check intent send from MainActivity
    private void getDataFromMain() {
        // if login = true, enable full access
        // if login = false , just user around shopping to view
        if (getIntent() != null)
        {
            isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin)
            {
                dialogSpot.show();
                //Check if user is existed
                if (  MainActivity.mAuth.getCurrentUser() != null)
                {
                    DocumentReference userDataRef = null;
                    FirebaseUser user = MainActivity.mAuth.getCurrentUser();
                    //region Check and get phone number if exists
                    if (user != null && user.getPhoneNumber().length() > 0)
                    {
                        userDataRef = userRef.document(MainActivity.mAuth.getCurrentUser().getPhoneNumber());
                        userDataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    DocumentSnapshot userSnapshot =  task.getResult();
                                    if (!userSnapshot.exists())
                                    {
                                        showUpdateDialog(MainActivity.mAuth.getCurrentUser().getPhoneNumber());
                                    }
                                    else
                                    {
                                        Common.currentUser = userSnapshot.toObject(User.class);
                                        bottomNavigationView.setSelectedItemId(R.id.action_home);
                                    }
                                      if (dialogSpot.isShowing())
                                      {
                                          dialogSpot.dismiss();
                                      }

                                }
                            }
                        });
                    }
                    //endregion

                    //region Check and get email from current User if exists
                    else if (MainActivity.mAuth.getCurrentUser().getEmail().length() > 0 && isCheckMail )
                    {
                        userDataRef = userRef.document(MainActivity.mAuth.getCurrentUser().getEmail());
                        userDataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    DocumentSnapshot userSnapshot =   task.getResult();
                                    if (!userSnapshot.exists())
                                    {
                                        showUpdateDialog(MainActivity.mAuth.getCurrentUser().getEmail());
                                    }
                                    else {
                                        dialogSpot.dismiss();
                                    }
                                }
                            }
                        });
                    }
                    //endregion
                }
            }
        }
    }
    //endregion

    //region Show dialog bottom to update information user
    private void showUpdateDialog(String email) {
        if (dialogSpot.isShowing()) dialogSpot.dismiss();

         bottomSheetDialog = new BottomSheetDialog(this);
         bottomSheetDialog.setTitle("One more step !");
         bottomSheetDialog.setCanceledOnTouchOutside(false);
         bottomSheetDialog.setCancelable(false);
         View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information, null);
        Button btn_update = sheetView.findViewById(R.id.btn_update);
        TextInputEditText edt_name_update = sheetView.findViewById(R.id.edt_name);
        TextInputEditText edt_address_update = sheetView.findViewById(R.id.edt_address);

        btn_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!dialogSpot.isShowing())
                    dialogSpot.show();
                String userName = edt_name_update.getText().toString();
                String address = edt_address_update.getText().toString();
                if (userName.length() <= 0 && address.length() <= 0)
                {
                    Toast.makeText(HomeActivity.this, "Please input your name and address.", Toast.LENGTH_SHORT).show();
                    if(dialogSpot.isShowing())
                        dialogSpot.dismiss();
                }
                else
                {
                    User user = new User( userName , address , email);
                    userRef.document(email).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(dialogSpot.isShowing())
                                        dialogSpot.dismiss();
                                    bottomSheetDialog.dismiss();
                                    Common.currentUser = user;
                                    Toast.makeText(HomeActivity.this, "Thanks for your information.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(dialogSpot.isShowing())
                                dialogSpot.dismiss();
                            bottomSheetDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
    //endregion
}