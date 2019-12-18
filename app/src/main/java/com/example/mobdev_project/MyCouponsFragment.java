package com.example.mobdev_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyCouponsFragment extends Fragment implements CouponsAdapter.CouponClickListener{
    private CouponsAdapter adapter;
    private SwipeRefreshLayout srlLoadCoupons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_my_coupons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rcyCoupons = view.findViewById(R.id.rcyCoupons);
        srlLoadCoupons = view.findViewById(R.id.srlLoadCoupons);

        srlLoadCoupons.setOnRefreshListener(refreshListener);
        srlLoadCoupons.setRefreshing(true);
        getCoupons();

        rcyCoupons.setLayoutManager(new LinearLayoutManager(getContext()));
        rcyCoupons.setHasFixedSize(true);
        adapter = new CouponsAdapter(getContext());
        adapter.setClickListener(this);
        rcyCoupons.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onDelete(View view, final int position) {
        Coupon coupon = adapter.getCoupon(position);
        try {
            Database.getInstance().DeleteCoupon(coupon.Id).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        adapter.delete(position);
                        Toast.makeText(getContext(), "Coupon verwijderd.", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("MyCouponsFragment", task.getException().getMessage());
                        Toast.makeText(getContext(), "Er heeft zich een fout voorgedaan, probeer opnieuw", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (FirebaseAuthInvalidCredentialsException e) {
            Log.e("MyCouponsFragment", "DeleteCoupon: ", e);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getCoupons();
        }
    };

    private void getCoupons() {
        try {
            Database.getInstance().GetCoupons().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        adapter.clear();
                        for (QueryDocumentSnapshot couponDoc : task.getResult()) {
                            if (couponDoc.exists())
                                adapter.add(couponDoc.toObject(Coupon.class));
                        }
                        srlLoadCoupons.setRefreshing(false);
                    } else {
                        Log.e("MyCouponsFragment", task.getException().getMessage());
                        Toast.makeText(getContext(), "Er heeft zich een fout voorgedaan, probeer opnieuw", Toast.LENGTH_LONG).show();
                        srlLoadCoupons.setRefreshing(false);
                    }
                }
            });
        } catch (FirebaseAuthInvalidCredentialsException e) {
            Log.e("MyCouponsFragment", "GetCoupons: ", e);
            srlLoadCoupons.setRefreshing(false);
        }
    }
}
