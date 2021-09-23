package com.teacher.TeacherAM;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Dialog logout_dialog;
    private FirebaseAuth mAuth;

    AppUpdateManager appUpdateManager;
    int RequestUpdate = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        appUpdateManager = AppUpdateManagerFactory.create(this);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if ((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                result,
                                AppUpdateType.IMMEDIATE,
                                MainActivity.this,
                                RequestUpdate);

                    }
                    catch (IntentSender.SendIntentException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        logout_dialog = new Dialog(MainActivity.this);
        logout_dialog.setContentView(R.layout.logout_dialog);
        logout_dialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        Button logout = logout_dialog.findViewById(R.id.logout);
        Button cancel = logout_dialog.findViewById(R.id.cancel);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, Log_in_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_dialog.dismiss();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.settings, R.id.studentAttendance)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                logout_dialog.show();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                result,
                                AppUpdateType.IMMEDIATE,
                                MainActivity.this,
                                RequestUpdate
                        );
                    }
                    catch (IntentSender.SendIntentException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}