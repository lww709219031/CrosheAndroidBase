package com.croshe.base.map.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.croshe.base.map.R;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janesen on 2017/5/10.
 */

public class BaseMapAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<PoiItem> data = new ArrayList<>();

    private PoiItem currCheckedPoi;
    private String currCheckPoiId = "";
    private int currCheckedPosition = -1;



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.android_base_map_item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof AddressViewHolder) {
            AddressViewHolder addressViewHolder = (AddressViewHolder) holder;
            addressViewHolder.setPoiItem(data.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 获得选中的位置
     * @return
     */
    public PoiItem getSelectPoi() {
        return currCheckedPoi;
    }
    public List<PoiItem> getData() {
        return data;
    }

    public void setData(List<PoiItem> data) {
        currCheckPoiId = null;
        currCheckedPosition = -1;
        this.data = data;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private PoiItem poiItem;
        private CheckBox tvAddress;

        public AddressViewHolder(View itemView) {
            super(itemView);

            tvAddress = (CheckBox) itemView.findViewById(R.id.tvAddress);
            itemView.findViewById(R.id.llItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(poiItem);
                }
            });
            tvAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        boolean needNotify = !currCheckPoiId.equals(poiItem.getPoiId());
                        currCheckPoiId = poiItem.getPoiId();
                        if (needNotify) {
                            notifyItemChanged(currCheckedPosition);
                        }
                        currCheckedPosition = getAdapterPosition();
                        currCheckedPoi = poiItem;
                    }else{
                        if (currCheckPoiId.equals(poiItem.getPoiId())) {
                            notifyItemChanged(currCheckedPosition);
                        }
                    }
                }
            });
            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(poiItem);
                }
            });
        }

        public void setPoiItem(PoiItem poiItem) {
            this.poiItem = poiItem;
            tvAddress.setText(poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet());

            if (StringUtils.isEmpty(currCheckPoiId)) {
                if (getAdapterPosition() == 0) {
                    currCheckPoiId = poiItem.getPoiId();
                    tvAddress.setChecked(true);
                }
            }else{
                if (currCheckPoiId.equals(poiItem.getPoiId())) {
                    tvAddress.setChecked(true);
                }else{
                    tvAddress.setChecked(false);
                }
            }
        }
    }
}
