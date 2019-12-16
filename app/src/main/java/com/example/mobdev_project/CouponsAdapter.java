package com.example.mobdev_project;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {
    private List<Coupon> coupons;
    private LayoutInflater inflater;
    private CouponClickListener couponClickListener;

    CouponsAdapter(Context context, List<Coupon> data) {
        inflater = LayoutInflater.from(context);
        coupons = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.coupon_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        if (coupon.ImageDownloadUrl != null)
            holder.imgCoupon.setImageURI(Uri.parse(coupon.ImageDownloadUrl));
        holder.lblName.setText(coupon.Name);
        holder.lblExpireDate.setText(coupon.ExpireDate.toString());
        holder.lblNotifyDate.setText(coupon.NotifyDate.toString());
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    void setClickListener(CouponClickListener couponClickListener) {
        this.couponClickListener = couponClickListener;
    }

    public interface CouponClickListener {
        void onClick(View view, int position);
        void onDelete(View view, int position);
    }
}
