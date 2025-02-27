package com.example.mobdev_project.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.mobdev_project.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImagePicker {

    private static String currentPhotoPath;
    public static Uri currentPhotoUri;
    public static  File currentPhotoFile;

    public final static int GALLERY_REQUEST_CODE = 666;
    public final static int CAMERA_REQUEST_CODE = 999;

    public static void pickFromGallery(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        // If needed mime types can be defined.
//        String[] mimeTypes = {"image/jpeg", "image/png"};
//        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    public static void pickFromCamera(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create file to save picture in.
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException e) {
                Log.e("ImagePicker", "pickFromCamera: ", e);
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(activity,
                        "com.example.mobdev_project.fileprovider",
                        photoFile);
                currentPhotoFile = photoFile;
                Log.d("Camera", currentPhotoFile.getPath());
                currentPhotoUri = photoUri;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private static File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BURROW_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static void pickImage(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.ThemeOverlay_MaterialComponents_Dialog);
        builder.setTitle("Kies afbeelding")
                .setMessage("Kies een afbeelding uit de gallerij of trek zelf een foto")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pickFromCamera(activity);
                    }
                })
                .setNeutralButton("Annuleren", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Gallerij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pickFromGallery(activity);
                    }
                });
        builder.create().show();
    }

}
