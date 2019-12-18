package com.example.mobdev_project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobdev_project.Helpers.DateHelpers;
import com.example.mobdev_project.Helpers.ImagePicker;
import com.example.mobdev_project.Helpers.NotificationPublisher;
import com.example.mobdev_project.Helpers.PermissionHelpers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class AddFragment extends Fragment {
    final private Calendar myCalendar = Calendar.getInstance();

    private EditText txtCouponName;
    private EditText txtEndDate;
    private EditText txtNotifyDate;
    private Button btnChooseImage;
    private ImageView imgCoupon;
    private Uri currentImageUri;
    private Button btnAddCoupon;
    private ProgressBar pbAddCoupon;

    private Date ExpireDate;
    private Date NotifyDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtCouponName = view.findViewById(R.id.txtCouponName);
        txtEndDate = view.findViewById(R.id.txtEndDate);
        txtNotifyDate = view.findViewById(R.id.txtNotifyDate);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        imgCoupon = view.findViewById(R.id.imgCoupon);
        btnAddCoupon = view.findViewById(R.id.btnAddCoupon);
        pbAddCoupon = view.findViewById(R.id.pbAddCoupon);

        imgCoupon.setImageURI(currentImageUri);

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionHelpers.hasRequeredPermissions(getContext())) {
                    // Choose image.
                    ImagePicker.pickImage(getActivity());
                } else {
                    // Request permissions.
                    PermissionHelpers.requestRequiredPermissions(getActivity());
                }
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),
                        onEndDateSet,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtNotifyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),
                        onNotifyDateSet,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnAddCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbAddCoupon.setVisibility(View.VISIBLE);


                String couponName = txtCouponName.getText().toString();
                final Coupon coupon;

                if (couponName != null && ExpireDate != null && NotifyDate != null && currentImageUri != null) {
                    coupon = new Coupon(couponName, ExpireDate, NotifyDate, currentImageUri);
                } else {
                    pbAddCoupon.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    Database.getInstance().CreateCoupon(coupon)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "Document added");
                                    pbAddCoupon.setVisibility(View.GONE);
                                    createCouponAlarm(NotifyDate.getTime(), coupon);
                                    resetView();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore", e.getMessage());
                                    pbAddCoupon.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Oops something went wrong :'(", Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    Log.e("AddFragment", "CreateCoupon: ", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private final DatePickerDialog.OnDateSetListener onEndDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);

            txtEndDate.setText(DateHelpers.FormatDate(myCalendar.getTime()));
            ExpireDate = myCalendar.getTime();
        }
    };

    private final DatePickerDialog.OnDateSetListener onNotifyDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);

            final TimePickerDialog.OnTimeSetListener onTimeSet = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalendar.set(Calendar.MINUTE, minute);

                    txtNotifyDate.setText(DateHelpers.FormatDateTime(myCalendar.getTime()));
                    NotifyDate = myCalendar.getTime();
                }
            };

            // Now open time picker.
            new TimePickerDialog(
                getActivity(),
                onTimeSet,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                true
            ).show();
        }
    };

    private void resetView() {
        imgCoupon.setImageResource(android.R.color.transparent);
        txtCouponName.getText().clear();
        txtNotifyDate.getText().clear();
        txtEndDate.getText().clear();
        ExpireDate = null;
        NotifyDate = null;
        currentImageUri = null;
    }

    private void createCouponAlarm(long alarmTimeMillis, Coupon coupon) {
//        final long intervalPeriod=10*1000;
            AlarmManager alarmManager;

        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("default",
                    "primary", NotificationManager.IMPORTANCE_HIGH);

            assert getActivity() != null;
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(notificationChannel);

            alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(getActivity().getApplicationContext(), NotificationPublisher.class);
            i.putExtra("couponName", coupon.Name);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 1234,
                    i, 0);

            assert alarmManager != null;
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalPeriod, intent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis,pendingIntent);
        }
    }

    void handlePermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (PermissionHelpers.isPermissionRequestSuccessful(requestCode, permissions, grantResults)){
            // Open camera or gallery.
            ImagePicker.pickImage(getActivity());
        }
    }


    void handleActivityResult(int requestCode, int resultCode, Intent data) {

        // Get image.
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ImagePicker.GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    imgCoupon.setImageURI(selectedImage);
                    currentImageUri = data.getData();
                    break;
                case ImagePicker.CAMERA_REQUEST_CODE:
                    //Uri cameraImage = (Uri)data.getExtras().get(MediaStore.EXTRA_OUTPUT);
                    // Workaround because for some reason the intent data gets deleted after the picture is taken.
                    Uri cameraImage = ImagePicker.currentPhotoUri;
                    imgCoupon.setImageURI(cameraImage);
                    currentImageUri = ImagePicker.currentPhotoUri;
                    break;
                default:
                    break;
            }
        }
    }
}
