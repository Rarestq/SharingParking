package com.example.sharingparking.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sharingparking.R;
import com.example.sharingparking.entity.ParkingLock;

import java.util.List;

import static com.example.sharingparking.utils.CommonUtil.splitParkingAddress;

/**
 * 车位信息适配器
 * Created by Lizhiguo on 2018/3/15.
 */

public class LockAdapter extends RecyclerView.Adapter<LockAdapter.ViewHolder>{

    private PublishInterface mPublishInterface;

    private Context mContext;

    private List<ParkingLock> mParkingLockList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView txtLockNo;
        TextView txtLockAddress;
        TextView txtLockDetailAddress;
        Button btnPublish;
        Button btnControlParking;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            txtLockNo = (TextView) view.findViewById(R.id.txt_lock_no);
            txtLockAddress = (TextView) view.findViewById(R.id.txt_lock_address);
            txtLockDetailAddress = (TextView) view.findViewById(R.id.txt_parking_detail_address);
            btnPublish = (Button) view.findViewById(R.id.btn_publish);
            btnControlParking = (Button) view.findViewById(R.id.btn_control_my_parking);

        }
    }

    public LockAdapter(List<ParkingLock> parkingList){
        mParkingLockList = parkingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_parking,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("ParkingActivity","获取信息");
        ParkingLock parking = mParkingLockList.get(position);
        holder.txtLockNo.setText(parking.getLockId() + "");

        /**
         * 将车位地址拆分为定位地址和详细地址
         */
        String[] addresses = splitParkingAddress(parking.getAddress());
        holder.txtLockAddress.setText(addresses[0]);
        holder.txtLockDetailAddress.setText(addresses[1]);
        Log.d("ParkingActivity",addresses[0]);
        Log.d("ParkingActivity",addresses[1]);
        //设置点击事件
        holder.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //利用接口，处理发布点击事件
                mPublishInterface.publish(holder.txtLockNo.getText().toString());
            }
        });

        holder.btnControlParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //利用接口，处理点击控制事件
                mPublishInterface.controlMyParking(parking.getLockId(),parking.getBlueToothId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mParkingLockList.size();
    }

    public interface PublishInterface{
        public void publish(String lockNo);
        public void controlMyParking(int lockNo,int bluetoothId);
    }

    public void setPublishInterface(PublishInterface publishInterface){
        this.mPublishInterface = publishInterface;
    }



}
