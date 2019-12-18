package com.example.mobdev_project;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

import java.io.File;
import java.util.Date;

public class Coupon {
    public String Id;
    public String Name;
    public Date ExpireDate;
    public Date NotifyDate;

    @Exclude
    public Uri ImageUri;

    public String ImageDownloadUrl;

    Coupon() {}

    Coupon(String name, Date expDate, Date notifyDate, Uri imageUri) {
        Name = name;
        ExpireDate = expDate;
        NotifyDate = notifyDate;
        ImageUri = imageUri;
    }
}
