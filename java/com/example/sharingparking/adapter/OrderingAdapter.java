package com.example.sharingparking.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sharingparking.R;
import com.example.sharingparking.entity.Publish;

import java.util.List;

import static com.example.sharingparking.utils.CommonUtil.dateToFormDate;
import static com.example.sharingparking.utils.CommonUtil.splitParkingAddress;

/**
 * 预订车位
 * Created by Lizhiguo on 2018/4/25.
 */

public class OrderingAdapter extends RecyclerView.Adapter<OrderingAdapter.ViewHolder> {

    private OrderingInterface mOrderingInterface;
    private Context mContext;

    private List<Publish> mPublishes;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView txtLockOwner;
        TextView txtOwnerPhone;
        TextView txtRentTime;
        TextView txtParkingPrice;
        TextView txtParkingAddress;
        TextView txtParkingDetailAddress;
        Button btnOrderingParking;


        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            txtLockOwner = (TextView) view.findViewById(R.id.txt_parking_owner);
            txtOwnerPhone = (TextView) view.findViewById(R.id.txt_parking_owner_phone);
            txtRentTime = (TextView) view.findViewById(R.id.txt_rent_time);
            txtParkingPrice = (TextView) view.findViewById(R.id.txt_ordering_price);
            txtParkingAddress = (TextView) view.findViewById(R.id.txt_parking_address);
            txtParkingDetailAddress = (TextView) view.findViewById(R.id.txt_ordering_parking_detail_address);
            btnOrderingParking = (Button) view.findViewById(R.id.btn_ordering_parking);

        }
    }

    public OrderingAdapter(List<Publish> publishes) {
        mPublishes = publishes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ordering,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Publish publish = mPublishes.get(position);
        holder.txtLockOwner.setText(publish.getUser().getUserName());
        holder.txtOwnerPhone.setText(publish.getUser().getPhoneNumber());
        String[] address = splitParkingAddress(publish.getLock().getAddress());
        holder.txtParkingAddress.setText(address[0]);
        holder.txtParkingDetailAddress.setText(address[1]);
        holder.txtParkingPrice.setText(publish.getParkingMoney() + "");
        holder.txtRentTime.setText(dateToFormDate(publish.getPublishStartTime())
                + "-" + dateToFormDate(publish.getPublishEndTime()));


        holder.btnOrderingParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOrderingInterface.orderingParking(publish.getPublishId());

            }
        });

        holder.txtParkingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到地图
                mOrderingInterface.showMap(publish);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPublishes.size();
    }

    public interface OrderingInterface{
        //预订车位
        public void orderingParking(Integer orderingId);
        //跳转到地图界面
        public void showMap(Publish publish);
    }

    public void setOrderingInterface(OrderingInterface orderingInterface){

        this.mOrderingInterface = orderingInterface;

    }

}
