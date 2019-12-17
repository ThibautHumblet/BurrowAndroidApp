package com.example.mobdev_project;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

class Database {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private static Database singleInstance;

    private Database() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    static Database getInstance() {
        if (singleInstance == null) {
            singleInstance = new Database();
        }

        return singleInstance;
    }

    /**
     * Creates a coupon in Firestore and uploads the image to Firebase Storage.
     * @param coupon The coupon to upload.
     * @return The Firestore task.
     * @throws FirebaseAuthInvalidCredentialsException Exception for not logged in user.
     */
    Task<Void> CreateCoupon(final Coupon coupon) throws FirebaseAuthInvalidCredentialsException {

        if (auth.getCurrentUser() != null) {
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
            return uriTask.continueWithTask(new Continuation<Uri, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<Uri> task) throws Exception {
                    if (task.isSuccessful()) {
                        coupon.Id = couponRef.getId();
                        coupon.ImageDownloadUrl = task.getResult().toString();
                        return couponRef.set(coupon);
                    } else {
                        throw task.getException();
                    }
                }
            });
        } else {
            throw new FirebaseAuthInvalidCredentialsException("Auth error", "User not logged in");
        }

    }

    /**
     * Gets all the coupons for the current user.
     * @return QuerySnapshot of the coupon collection.
     * @throws FirebaseAuthInvalidCredentialsException Exception for not logged in user.
     */
    Task<QuerySnapshot> GetCoupons() throws FirebaseAuthInvalidCredentialsException {

        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();

            return db.collection(uid).get();
        } else {
            throw new FirebaseAuthInvalidCredentialsException("Auth error", "User not logged in");
        }

    }

    /**
     * Deletes the coupon with the given id.
     * @param id The id of the coupon to delete.
     * @return The deletion task.
     * @throws FirebaseAuthInvalidCredentialsException Exception for not logged in user.
     */
    Task<Void> DeleteCoupon(String id) throws FirebaseAuthInvalidCredentialsException {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();

            final DocumentReference couponRef = db.collection(uid).document(id);

            return couponRef.delete();
        } else {
            throw new FirebaseAuthInvalidCredentialsException("Auth error", "User not logged in");
        }
    }
}
