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
import com.example.sharingparking.entity.Publish;

import java.util.List;

import static com.example.sharingparking.utils.CommonUtil.dateToFormDate;

/**
 * Created by Lizhiguo on 2018/4/17.
 */

public class PublishedAdapter extends RecyclerView.Adapter<PublishedAdapter.ViewHolder> {

    private String TAG = "PublishedAdapter";

    private PublishedInterface mPublishedInterface;

    private Context mContext;

    private List<Publish> mPublishes;

    private Integer publishState;



    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView txtPublishNo;
        TextView txtLockNo;
        TextView txtStartTime;
        TextView txtEndTime;
        TextView txtParkingPrice;
        TextView txtPublishState;
        Button btnCancelPublish;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            txtPublishNo = (TextView) view.findViewById(R.id.txt_publish_no);
            txtLockNo = (TextView) view.findViewById(R.id.txt_parking_no);
            txtStartTime = (TextView) view.findViewById(R.id.txt_publish_start_time);
            txtEndTime = (TextView) view.findViewById(R.id.txt_publish_end_time);
            txtParkingPrice = (TextView) view.findViewById(R.id.txt_publish_price);
            txtPublishState = (TextView) view.findViewById(R.id.txt_publish_state);
            btnCancelPublish = (Button) view.findViewById(R.id.btn_cancel_publish);

        }
    }


    public PublishedAdapter(List<Publish> publishes){
        this.mPublishes = publishes;
    }

    public PublishedAdapter(List<Publish> publishes,Integer publishState){
        this.mPublishes = publishes;
        this.publishState = publishState;
    }

    @Override
    public PublishedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_publish,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Publish publish = mPublishes.get(position);
        Log.d(TAG,publish.getPublishId() + "");
        holder.txtPublishNo.setText(publish.getPublishId() + "");
        holder.txtLockNo.setText(publish.getLockId() + "");
        holder.txtStartTime.setText(dateToFormDate(publish.getPublishStartTime()));
        holder.txtEndTime.setText(dateToFormDate(publish.getPublishEndTime()));
        holder.txtParkingPrice.setText(publish.getParkingMoney() + "");

        if(publishState != 1){
            holder.btnCancelPublish.setVisibility(View.GONE);
        }

        holder.txtPublishState.setText(handlePublishState(publish.getPublishState()));

        holder.btnCancelPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //利用接口，处理点击事件
                Log.d(TAG,mPublishedInterface.toString());
                mPublishedInterface.cancelPublished(publish.getPublishId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPublishes.size();
    }


    public interface PublishedInterface{
        //取消发布
        public void cancelPublished(Integer publishId);
    }

    public void setPublishedInterface(PublishedInterface publishedInterface){

        this.mPublishedInterface = publishedInterface;

    }

    //处理发布状态
    private String handlePublishState(Integer stateNumber){
        if(stateNumber == 1){
            return "正在发布";
        }else if(stateNumber == 2){
            return "发布超时";
        }else {
            return "已取消";
        }
    }

}
