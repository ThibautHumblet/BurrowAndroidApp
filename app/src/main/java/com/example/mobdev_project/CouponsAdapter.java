package com.example.mobdev_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobdev_project.Helpers.DateHelpers;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {
    private List<Coupon> coupons;
    private LayoutInflater inflater;
    private CouponClickListener couponClickListener;

    CouponsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        coupons = new ArrayList<>();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.coupon_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);

        Picasso.get().load(coupon.ImageDownloadUrl).into(holder.imgCoupon);

        holder.lblName.setText(coupon.Name);
        holder.lblExpireDate.setText(DateHelpers.FormatDate(coupon.ExpireDate));
        holder.lblNotifyDate.setText(DateHelpers.FormatDateTime(coupon.NotifyDate));
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCoupon;
        TextView lblName;
        TextView lblNotifyDate;
        TextView lblExpireDate;
        Button btnDelete;


        ViewHolder(View itemView) {
            super(itemView);
            imgCoupon = itemView.findViewById(R.id.imgCoupon);
            lblName = itemView.findViewById(R.id.lblName);
            lblNotifyDate = itemView.findViewById(R.id.lblNotifyDate);
            lblExpireDate = itemView.findViewById(R.id.lblExpireDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(clickListener);
            btnDelete.setOnClickListener(deleteListener);
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (couponClickListener != null) {
                    couponClickListener.onClick(view, getAdapterPosition());
                }
            }
        };

        private View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (couponClickListener != null) {
                    couponClickListener.onDelete(view, getAdapterPosition());
                }
            }
        };
    }

    Coupon getCoupon(int position) {
        return coupons.get(position);
    }

    void clear() {
        coupons.clear();
        notifyDataSetChanged();
    }

    void delete(int position) {
        coupons.remove(position);
        notifyDataSetChanged();
    }

    void add(Coupon coupon) {
        this.coupons.add(coupon);
        notifyDataSetChanged();
    }

    void addAll(List<Coupon> coupons) {
        this.coupons.addAll(coupons);
        notifyDataSetChanged();
    }

    void setClickListener(CouponClickListener couponClickListener) {
        this.couponClickListener = couponClickListener;
    }

    public interface CouponClickListener {
        void onClick(View view, int position);
        void onDelete(View view, int position);
    }
}
