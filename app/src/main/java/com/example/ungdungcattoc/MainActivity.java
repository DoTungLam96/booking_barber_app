package com.example.ungdungcattoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ungdungcattoc.Common.Common;
import com.example.ungdungcattoc.Models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
   public static int PERMISSION_1 = 12;
   public static  final  int APP_REQUEST_CODE = 1996;
   public static   FirebaseAuth mAuth;
   private boolean isAllow;
    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.txt_skip)
    TextView txt_Skip;

    @OnClick(R.id.btn_login)
    void loginUser()
    {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), APP_REQUEST_CODE);
    }


    @OnClick(R.id.txt_skip)
    void skipLoginJustGoHome()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Common.IS_LOGIN, false);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra(Common.IS_LOGIN ,  true);
            startActivity(intent);
            finish();
        }
        else
        {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(MainActivity.this);
        }
        GetPermissionRuntime();
    }
    //region Permission application
    private void GetPermissionRuntime() {

        int CheckPermissionInTernet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int CheckPermissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int CheckPermissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CheckPermissionReadCalendar = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int CheckPermissionWriteCalendar = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        if     (CheckPermissionRead != PackageManager.PERMISSION_GRANTED ||
                CheckPermissionReadCalendar != PackageManager.PERMISSION_GRANTED ||
                CheckPermissionWriteCalendar != PackageManager.PERMISSION_GRANTED ||
                CheckPermissionWrite != PackageManager.PERMISSION_GRANTED ||
                CheckPermissionInTernet != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET},PERMISSION_1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_1 && permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
             Toasty.success(this,"Permission allow.", Toasty.LENGTH_SHORT).show();
        }
        else {
            Toasty.error(this, "Please allow permission to use app.", Toasty.LENGTH_LONG).show();
        }
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK)
            {
                String number_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    // Successfully signed in
                    Intent iLogin = new Intent(MainActivity.this, HomeActivity.class);
                    iLogin.putExtra(Common.IS_LOGIN,true);
                    startActivity(iLogin);
                    finish();
                    return;
                }
                else
                {
                    if (response == null)
                    {
                        Toast.makeText(this, "Cancel login.", Toast.LENGTH_SHORT).show();
                    }
                    if (response.getError().getMessage().length() > 0)
                    {
                        Toast.makeText(this,response.getError().getMessage() , Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(this, "Sign In failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}