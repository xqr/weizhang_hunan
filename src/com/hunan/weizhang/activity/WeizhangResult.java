package com.hunan.weizhang.activity;

import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sprzny.hunan.R;
import com.hunan.weizhang.adapter.WeizhangResponseAdapter;
import com.hunan.weizhang.api.client.HunanWeizhangApiClient;
import com.hunan.weizhang.api.client.WeizhangApiClient;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.qrcode.QrCodeExample;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WzHunanService;

/**
 * title：查询违章信息
 * 
 * @author paul
 * 
 */
public class WeizhangResult extends BaseActivity {
    final Handler cwjHandler = new Handler();
    WeizhangMessage info = null;
    
    private View popLoader;

    private WeizhangHistoryService weizhangHistoryService;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 循环添加列表项来显示 自定义adapter
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.csy_activity_result);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("违章查询结果");

        // 返回按钮
        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        weizhangHistoryService = new WeizhangHistoryService(WeizhangResult.this);
        
        popLoader = (View) findViewById(R.id.popLoader);
        popLoader.setVisibility(View.VISIBLE);

        Intent intent = this.getIntent();
        
        String flag = intent.getStringExtra("flag");
        CarInfo car = null;
//        VerificationCode verificationCode = null;
        String telephone = null;
        WeizhangMessage  weizhangMessage = null;
        if (flag != null && flag.equals("history")) {
            weizhangMessage = (WeizhangMessage) intent.getSerializableExtra("weizhangMessage");
            car = weizhangMessage.getCarInfo();
        } else {
            car = (CarInfo) intent.getSerializableExtra("carInfo");
//            verificationCode = (VerificationCode) intent.getSerializableExtra("verificationCode");
            telephone = intent.getStringExtra("telephone");
        }
        step4(weizhangMessage, car, null, telephone);

        // 查询内容: 车牌
        TextView query_chepai = (TextView) findViewById(R.id.query_chepai);
        query_chepai.setText(car.getChepaiNo());
        TextView query_city = (TextView) findViewById(R.id.query_city);
        if (car.getHaopaiType().equals("02")) {
            query_city.setText("小型汽车");
        } else {
            query_city.setText("大型汽车");
        }
    }

    public void step4(final WeizhangMessage  weizhangMessage, final CarInfo car, 
            final VerificationCode verificationCode, final String telephone) {
        // 声明一个子线程
        new Thread() {
            @Override
            public void run() {
                try {
                    WeizhangMessage newWeizhangMessage = weizhangMessage;
                    if (weizhangMessage == null) {
                        // 优先从历史记录查询
                        newWeizhangMessage = weizhangHistoryService.getHistory(car);
                    }
                    
                    if(newWeizhangMessage != null 
                            && (newWeizhangMessage.getSearchTimestamp() + 1000 * 60 * 60 * 8) >= new Date().getTime()) {
                        info = newWeizhangMessage;
                        cwjHandler.post(mUpdateResults); // 高速UI线程可以更新结果了
                        return;
                    }
                    
                    // 从业务服务器获取信息
                    WzHunanService.queryWeizhang(car, telephone);
                    
//                    // 校验验证码正确性
//                    VerificationCode newVerificationCode = verificationCode;
//                    if (newVerificationCode == null 
//                            || newVerificationCode.getRandCode() == null) {
//                        newVerificationCode = getVerificationCode();
//                    }
                    
//                    newWeizhangMessage = WeizhangApiClient.toQueryVioltionByCarAction(car, newVerificationCode);
//                    //  验证码错误自动重试1次
//                    if (newWeizhangMessage != null && newWeizhangMessage.getMessage().equals("图片验证码有误")) {
//                        newVerificationCode = getVerificationCode();
//                        newWeizhangMessage = WeizhangApiClient.toQueryVioltionByCarAction(car, newVerificationCode);
//                    }
                    newWeizhangMessage = HunanWeizhangApiClient.toQueryVioltionByCarAction(car);
                    // 查询成功，缓存结果
                    if (newWeizhangMessage != null && newWeizhangMessage.getCode().equals("1")) {
                        weizhangHistoryService.appendHistory(newWeizhangMessage);
                    }
                    info = newWeizhangMessage;
                    cwjHandler.post(mUpdateResults); // 高速UI线程可以更新结果了
                } catch (Exception e) {
                    
                }
            }
        }.start();
    }

//    /**
//     * 获取验证码
//     */
//    public VerificationCode getVerificationCode() {
//        int i = 1;
//        VerificationCode verificationCode = null;
//        do {
//            verificationCode = WeizhangApiClient.getVerifCodeAction();
//            if (verificationCode != null) {
//                String result = QrCodeExample.qrCode(verificationCode.getTpyzm());
//                if (result == null || result.length() != 4) {
//                    verificationCode = null;
//                } else {
//                    verificationCode.setRandCode(result);
//                }
//            }
//            i++;
//        } while(verificationCode == null && i  <= 3);
//        
//        return verificationCode;
//    }
//    
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    private void updateUI() {
        TextView result_null = (TextView) findViewById(R.id.result_null);
        TextView result_title = (TextView) findViewById(R.id.result_title);
        ListView result_list = (ListView) findViewById(R.id.result_list);

        popLoader.setVisibility(View.GONE);
        
        // 直接将信息限制在 Activity中
        if (info != null 
                && info.getCode().equals("1")) {
            
            if ( info.getData() == null
                    || info.getData().size() == 0) {
                // 未查找违章记录
                result_null.setText("恭喜, 没有查到违章记录！");
                result_title.setVisibility(View.GONE);
                result_list.setVisibility(View.GONE);
                result_null.setVisibility(View.VISIBLE);
                return ;
            }
            
            result_null.setVisibility(View.GONE);
            result_title.setVisibility(View.VISIBLE);
            result_list.setVisibility(View.VISIBLE);

            result_title.setText("共违章" + info.getUntreatedCount() + "次, 计" 
                            + info.getTotalScores() +"分, 罚" + info.getTotalFkje() + "元");
            
            WeizhangResponseAdapter mAdapter = new WeizhangResponseAdapter(
                    this, info.getData());
            result_list.setAdapter(mAdapter);
        } else {
            // 没有查到为章记录
            if(info == null) {
                result_null.setText("交管局系统连线忙碌中，请稍后再试");
            } else {
                result_null.setText(info.getMessage());
            }
            
            result_title.setVisibility(View.GONE);
            result_list.setVisibility(View.GONE);
            result_null.setVisibility(View.VISIBLE);
        }
    }
}
