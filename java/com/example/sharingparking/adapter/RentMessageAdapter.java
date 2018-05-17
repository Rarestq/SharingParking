package com.example.sharingparking.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sharingparking.R;
import com.example.sharingparking.entity.Ordering;

import java.util.List;

/**
 * 租用信息适配器
 * Created by Lizhiguo on 2018/3/17.
 */

public class RentMessageAdapter extends RecyclerView.Adapter<RentMessageAdapter.ViewHolder>{

    private Context mContext;

    private List<Ordering> mOrderingList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView txtParkingOwner;   //车位主
        TextView txtParkingAddress; //车位地址
        TextView txtParkingOwnerPhone;//车位主联系方式
        TextView txtParkingPrice;//车位价格
        TextView txtParkingExpense;//费用
        TextView txtRentTime;//租用时间

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            txtParkingOwner = (TextView) view.findViewById(R.id.txt_parking_owner);
            txtParkingAddress = (TextView) view.findViewById(R.id.txt_parking_address);
            txtParkingOwnerPhone = (TextView) view.findViewById(R.id.txt_parking_owner_phone);
            txtParkingPrice = (TextView) view.findViewById(R.id.txt_parking_price);
            txtParkingExpense = (TextView) view.findViewById(R.id.txt_rent_expense);
            txtRentTime = (TextView) view.findViewById(R.id.txt_rent_time);
        }
    }

    public RentMessageAdapter(List<Ordering> orderingList){
        this.mOrderingList = orderingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rentmessage,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ordering ordering = mOrderingList.get(position);
        holder.txtParkingOwner.setText(ordering.getUser().getUserName());
        holder.txtParkingOwnerPhone.setText(ordering.getUser().getPhoneNumber());
        holder.txtParkingAddress.setText(ordering.getLock().getAddress());
        holder.txtParkingPrice.setText("");
        holder.txtParkingExpense.setText(ordering.getExpense() + "");
        holder.txtRentTime.setText("");

    }

    @Override
    public int getItemCount() {
        return mOrderingList.size();
    }

}
