package com.example.mobdev_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mobdev_project.Helpers.NotificationPublisher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment;
    private AddFragment addFragment = new AddFragment();

    final long intervalPeriod=10*1000;
    AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        // Set initial fragment
        bottomNav.setSelectedItemId(R.id.nav_borrowed_me);
    }


    private  BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_add:
                    selectedFragment = addFragment;
                    NotificationChannel notificationChannel = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel = new NotificationChannel("default",
                                "primary", NotificationManager.IMPORTANCE_HIGH);

                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (manager != null) manager.createNotificationChannel(notificationChannel);

                        mAlarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

                        PendingIntent intent = PendingIntent.getBroadcast(getApplicationContext(), 1234,
                                new Intent(getApplicationContext(), NotificationPublisher.class), 0);

                        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalPeriod, intent);
                    }
                    break;
                case R.id.nav_borrowed_me:
                    selectedFragment = new MyCouponsFragment();
                    break;
                case R.id.nav_account:
                    selectedFragment = new AccountFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

            return true;
        }
    };

    // Don't delete needed to call onRequestPermissionsResult in Fragments
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Workaround because onActivityResult isn't called in Fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (selectedFragment instanceof AddFragment) {
            ((AddFragment) selectedFragment).handleActivityResult(requestCode, resultCode, data);
        }
    }
}
