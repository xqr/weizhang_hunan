package com.hunan.weizhang.adapter;

import java.util.List;

import com.sprzny.hunan.R;
import com.hunan.weizhang.model.WeizhangMessage;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter {
    
    private  List<WeizhangMessage> mDate;
    private Context mContext;

    public HistoryListAdapter( Context mContext, List<WeizhangMessage> mDate){
        this.mContext=mContext;
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
        
        View view = View.inflate(mContext, R.layout.csy_listitem_index, null);
        
        //初始化
        WeizhangMessage weizhangMessage = mDate.get(position) ;
        
        TextView chepaiView =(TextView) view.findViewById(R.id.chepai_view);
        TextView weizhangmessageView = (TextView) view.findViewById(R.id.weizhangmessage_view);
        TextView weizhangmessageTotalView = (TextView) view.findViewById(R.id.weizhangmessage_total_view);
        
        // 绑定数据
        chepaiView.setText(weizhangMessage.getCarInfo().getChepaiNo());
        if (weizhangMessage.getData() == null 
                || weizhangMessage.getData().size() == 0) {
            // 无违章记录
            weizhangmessageView.setVisibility(View.GONE);
            weizhangmessageTotalView.setTextSize(16);
            weizhangmessageTotalView.setText("暂无违章记录");
        } else {
            String message = String.format("扣分 <font color='#FF0000'>%s分</font>      罚款 <font color='#FF0000'>%s元</font>", 
                    weizhangMessage.getTotalScores(), weizhangMessage.getTotalFkje());
            weizhangmessageView.setText(Html.fromHtml(message));
            String text = String.format("共 <font color='#FF0000'>%s次</font> 违章", weizhangMessage.getData().size());
            weizhangmessageTotalView.setText(Html.fromHtml(text));
        }
        //返回
        return view;
    }
}
