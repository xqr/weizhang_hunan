package com.hunan.weizhang.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sprzny.hunan.R;
import com.hunan.weizhang.model.WeizhangInfo;

public class WeizhangResponseAdapter extends BaseAdapter {

    private List<WeizhangInfo> mDate;
    private Context mContext;

    public WeizhangResponseAdapter(Context mContex,List<WeizhangInfo> mDate){
        this.mContext=mContex;
        this.mDate=mDate;
    }
    
    @Override
    public int getCount() {
        return mDate.size();
    }

    @Override
    public Object getItem(int position) {
        return mDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.csy_listitem_result, null);
        // 获取ID
        WeizhangInfo model=mDate.get(position) ;

        TextView wz_time = (TextView) view.findViewById(R.id.wz_time);
        TextView wz_money = (TextView) view.findViewById(R.id.wz_money);
        TextView wz_addr = (TextView) view.findViewById(R.id.wz_addr);
        TextView wz_info = (TextView) view.findViewById(R.id.wz_info);
        TextView wz_status = (TextView) view.findViewById(R.id.wz_status);
        
        // 填写值
        wz_time.setText(model.getWfsj());
        wz_money.setText("计"+model.getWfjfs()+"分, 罚"+model.getFkje()+"元");
        wz_addr.setText(model.getWfdz());
        wz_info.setText(model.getWfxw());
        if (model.getZt().equals("0")) {
            wz_status.setText("未处理");
        } else {
            wz_status.setText("已处理");
            wz_status.setTextColor(mContext.getResources().getColor(R.color.csy_text));
        }
        
        return view;
    }
}
