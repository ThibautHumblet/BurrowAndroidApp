package com.example.mobdev_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyCouponsFragment extends Fragment implements CouponsAdapter.CouponClickListener{
    CouponsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_my_coupons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon("TEst", new Date("16/12/2019"), new Date("16/12/2019"), null));
        coupons.add(new Coupon("TEst", new Date("16/12/2019"), new Date("16/12/2019"), null));
        coupons.add(new Coupon("TEst", new Date("16/12/2019"), new Date("16/12/2019"), null));


        RecyclerView rcyCoupons = view.findViewById(R.id.rcyCoupons);
        rcyCoupons.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CouponsAdapter(getContext(), coupons);
        adapter.setClickListener(this);
        rcyCoupons.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onDelete(View view, int position) {

    }
}
