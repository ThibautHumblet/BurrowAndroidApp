package com.example.mobdev_project;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

import java.io.File;
import java.util.Date;

public class Coupon {
    public String Name;
    public Date ExpireDate;
    public Date NotifyDate;

    @Exclude
    public File ImageFile;

    public String ImageDownloadUrl;

    public Coupon(String name, Date expDate, Date notifyDate, File imageFile) {
        Name = name;
        ExpireDate = expDate;
        NotifyDate = notifyDate;
        ImageFile = imageFile;
    }
}
