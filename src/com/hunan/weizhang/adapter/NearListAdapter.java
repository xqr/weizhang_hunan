package com.hunan.weizhang.adapter;

import java.util.List;

import com.hunan.weizhang.model.CustomSearchResultData;
import com.sprzny.quanguo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NearListAdapter extends BaseAdapter {
    private Context context; 
    private List<CustomSearchResultData> list;
    
    public NearListAdapter() {
        
    }
    
    public NearListAdapter(Context context, List<CustomSearchResultData> list) {
        this.context = context;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        if (position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null){
            viewHolder = (ViewHolder)convertView.getTag();
        } else { 
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
            convertView = mInflater.inflate(R.layout.csy_listitem_near, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.titleTv = (TextView) convertView.findViewById(R.id.title);
            viewHolder.distanceTv = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.addressTv = (TextView) convertView.findViewById(R.id.address);
            
            convertView.setTag(viewHolder);
        }
        
        // set数据
        CustomSearchResultData data = (CustomSearchResultData) getItem(position);
        if (data != null) {
            if (data.getName().length() > 12) {
                viewHolder.titleTv.setText(data.getName().substring(0, 12) + "...");
            } else {
                viewHolder.titleTv.setText(data.getName());
            }
            if (data.getDetail() == 1) {
                viewHolder.distanceTv.setText(data.getDetail_info().getDistance() + "米");
            }
            viewHolder.addressTv.setText(data.getAddress());
        }
        
        return convertView;
    }
    
    
    private static class ViewHolder
    {
        TextView titleTv;
        TextView distanceTv;
        TextView addressTv;
    }
}
