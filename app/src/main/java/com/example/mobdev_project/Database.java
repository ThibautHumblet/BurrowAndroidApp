package com.example.mobdev_project;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Database {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private static Database singleInstance;

    private Database() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static Database getInstance() {
        if (singleInstance == null) {
            singleInstance = new Database();
        }

        return singleInstance;
    }

    /**
     * Creates a coupon in Firestore and uploads the image to Firebase Storage.
     * @param coupon The coupon to upload.
     * @return The Firestore task.
     */
    public Task<Void> CreateCoupon(final Coupon coupon) {
        String uid = auth.getCurrentUser().getUid();
        Uri imageUri = Uri.parse(coupon.ImageFile.toURI().toString());
        String imageName = imageUri.getLastPathSegment();


        final DocumentReference couponRef = db.collection(uid).document();

        final StorageReference imageRef = storage.getReference()
                                    .child(uid)
                                    .child(couponRef.getId())
                                    .child(imageName);

        // Task to upload the image.
        UploadTask storageTask = imageRef.putFile(imageUri);

        // After the image is uploaded get the download url.
        Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    return imageRef.getDownloadUrl();
                } else {
                    throw task.getException();
                }
            }
        });

        // Upload data to database after we get the download url.
        // This download url should be included in the coupon.
        Task<Void> dbTask = uriTask.continueWithTask(new Continuation<Uri, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Uri> task) throws Exception {
                if (task.isSuccessful()) {
                    coupon.ImageDownloadUrl = task.getResult().toString();
                    return couponRef.set(coupon);
                } else {
                    throw task.getException();
                }
            }
        });

        return dbTask;
    }
}
