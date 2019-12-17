package com.example.mobdev_project;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class AddFragment extends Fragment {
    final private Calendar myCalendar = Calendar.getInstance();

    private EditText txtCouponName;
    private EditText txtEndDate;
    private EditText txtNotifyDate;
    private Button btnChooseImage;
    private ImageView imgCoupon;
    private File currentImageFile;
    private Uri currentImageUri;
    private Button btnAddCoupon;
    private ProgressBar pbAddCoupon;

    private Date ExpireDate;
    private Date NotifyDate;

    private static final int CHOOSER_PERMISSIONS_REQUEST_CODE = 7459;
    private static final int CAMERA_REQUEST_CODE = 7500;
    private static final int CAMERA_VIDEO_REQUEST_CODE = 7501;
    private static final int GALLERY_REQUEST_CODE = 7502;
    private static final int DOCUMENTS_REQUEST_CODE = 7503;

    private EasyImage easyImage;

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("imageUri", currentImageUri);
    }

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


        // Initialize
        easyImage = new EasyImage.Builder(getContext())
                .setChooserTitle("Pick image")
                .setCopyImagesToPublicGalleryFolder(false)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName("Burrow_images")
                .allowMultiple(false)
                .build();

        txtCouponName = view.findViewById(R.id.txtCouponName);
        txtEndDate = view.findViewById(R.id.txtEndDate);
        txtNotifyDate = view.findViewById(R.id.txtNotifyDate);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        imgCoupon = view.findViewById(R.id.imgCoupon);
        btnAddCoupon = view.findViewById(R.id.btnAddCoupon);
        pbAddCoupon = view.findViewById(R.id.pbAddCoupon);

        // Restore state if needed.
        if (savedInstanceState != null) {
            currentImageUri = savedInstanceState.getParcelable("imageUri");
            imgCoupon.setImageURI(currentImageUri);
        }
        imgCoupon.setImageURI(currentImageUri);

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] necessaryPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (arePermissionsGranted(necessaryPermissions)) {
                    easyImage.openChooser(getActivity());
                } else {
                    requestPermissionsCompat(necessaryPermissions, CHOOSER_PERMISSIONS_REQUEST_CODE);
                }
            }
        });

        final DatePickerDialog.OnDateSetListener onEndDateSet = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                setEndDate(myCalendar.getTime());
            }
        };

        final DatePickerDialog.OnDateSetListener onNotifyDateSet = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                setNotifyDate(myCalendar.getTime());
            }
        };

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
                Coupon coupon = new Coupon(couponName, ExpireDate, NotifyDate, currentImageFile);

                try {
                    Database.getInstance().CreateCoupon(coupon)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "Document added");
                                    pbAddCoupon.setVisibility(View.GONE);
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

    private void setEndDate(Date endDate) {
        txtEndDate.setText(DateHelpers.FormatDate(endDate));
        ExpireDate = endDate;
    }

    private void setNotifyDate(Date notifyDate) {
        txtNotifyDate.setText(DateHelpers.FormatDate(notifyDate));
        NotifyDate = notifyDate;
    }

    private void resetView() {
        imgCoupon.setImageResource(android.R.color.transparent);
        txtCouponName.getText().clear();
        txtNotifyDate.getText().clear();
        txtEndDate.getText().clear();
    }

    private boolean arePermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        }
        return true;
    }

    private void requestPermissionsCompat(String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CHOOSER_PERMISSIONS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openChooser(getActivity());
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForImage(getActivity());
        } else if (requestCode == CAMERA_VIDEO_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForVideo(getActivity());
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openGallery(getActivity());
        } else if (requestCode == DOCUMENTS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openDocuments(getActivity());
        }
    }

    void handleActivityResult(int requestCode, int resultCode, Intent data) {

        easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] imageFiles, @NotNull MediaSource source) {
                for (MediaFile imageFile : imageFiles) {
                    Log.d("EasyImage", "Image file returned: " + imageFile.getFile().toString());
                }
                // Set the image.
                currentImageFile = imageFiles[0].getFile();
                currentImageUri = Uri.fromFile(imageFiles[0].getFile());
                imgCoupon.setImageURI(currentImageUri);
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });
    }
}
