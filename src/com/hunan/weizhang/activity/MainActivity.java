package com.hunan.weizhang.activity;

import java.util.Locale;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sprzny.shanghai.R;
import com.hunan.weizhang.api.client.weizhang.HunanWeizhangApiClient;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.qrcode.QrCodeExample;
import com.hunan.weizhang.service.AllCapTransformationMethod;

public class MainActivity extends BaseActivity {
    
    private String defaultChepai = "沪"; 
    
    // 车牌简称
    private TextView short_name;
    // 车牌类型
    private Spinner haopai_lx;
    private ImageView haopai_query;
    // 车牌号选择
    private View btn_cpsz;
    // 查询按钮
    private Button btn_query;
    
    private EditText chepai_number;
    private EditText engine_number;
    private EditText telephone_number;
    
    // 验证码对象
    private VerificationCode verificationCode;
    
    // 行驶证图示
    private View popXSZ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.csy_activity_main);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("违章查询");
        
        // 返回按钮
        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 选择省份缩写
        haopai_lx = (Spinner) findViewById(R.id.haopai_lx);
        chepai_number = (EditText) findViewById(R.id.chepai_number);
        engine_number = (EditText) findViewById(R.id.engine_number);
        short_name = (TextView) findViewById(R.id.chepai_sz);
        telephone_number = (EditText) findViewById(R.id.telephone_number);
        // ----------------------------------------------
        
        // 增加字母大小监控转换
        chepai_number.setTransformationMethod(new AllCapTransformationMethod());
        engine_number.setTransformationMethod(new AllCapTransformationMethod());
        
        // 默认选中小汽车
        haopai_lx.setSelection(1, true);
        
        haopai_query = (ImageView) findViewById(R.id.haopai_query);
        btn_cpsz = (View) findViewById(R.id.btn_cpsz);
        btn_query = (Button) findViewById(R.id.btn_query);
        
        haopai_query.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                haopai_lx.performClick();
            }
        });
        
        btn_cpsz.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShortNameList.class);
                String shortNameStr = short_name.getText().toString().trim();
                if (shortNameStr == null || shortNameStr.isEmpty()) {
                    shortNameStr = "湘";
                }
                intent.putExtra("select_short_name", shortNameStr);
                startActivityForResult(intent, 0);
            }
        });

        btn_query.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 获取违章信息
                CarInfo car = new CarInfo();
                
                final String shortNameStr = short_name.getText().toString().trim();
                // 发动机和车牌字母必须大写
                final String chepaiNumberStr = chepai_number.getText().toString().trim().toUpperCase(Locale.getDefault());
                final String engineNumberStr = engine_number.getText().toString().trim().toUpperCase(Locale.getDefault());
                final String telephoneNumberStr = telephone_number.getText().toString().trim();
                final String  haopaiTypeStr = haopai_lx.getSelectedItem().toString();

                // 车牌
                car.setChepaiNo(shortNameStr + chepaiNumberStr);
                car.setEngineNo(engineNumberStr);
                // 号牌类型
                if (haopaiTypeStr.equals("小型汽车")) {
                    car.setHaopaiType("02");
                } else if (haopaiTypeStr.equals("大型汽车")) {
                    car.setHaopaiType("01");
                }
                
                boolean result = checkQueryItem(car); 
                if (!result) {
                    return;
                }
                
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("carInfo", car);
                bundle.putSerializable("verificationCode", verificationCode);
                bundle.putString("telephone", telephoneNumberStr);
                intent.putExtras(bundle);
                
                intent.setClass(MainActivity.this, WeizhangResult.class);
                startActivity(intent); 
            }
        });
        
        // 默认为湘
        short_name.setText(defaultChepai);

        // 显示隐藏行驶证图示
        popXSZ = (View) findViewById(R.id.popXSZ);
        popXSZ.setOnTouchListener(new popOnTouchListener());
        hideShowXSZ();
        
//        //获取验证码
//        new GetVerificationCodeTask().execute();
    }
    
    /**
     * 获取验证码
     */
    public class GetVerificationCodeTask extends AsyncTask<Void, Void, Boolean> {
        
        @Override
        protected Boolean doInBackground(Void... params) {
            int i = 1;
            do {
                verificationCode = HunanWeizhangApiClient.getVerifCodeAction();
                if (verificationCode != null) {
                    String result = QrCodeExample.qrCode(verificationCode.getTpyzm());
                    if (result == null || result.length() != 4) {
                        verificationCode = null;
                    } else {
                        verificationCode.setRandCode(result);
                    }
                }
                i++;
            } while(verificationCode == null && i  <= 3);
            
            return true;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
        case 0:
            Bundle bundle = data.getExtras();
            String ShortName = bundle.getString("short_name");
            short_name.setText(ShortName);
            break;
        }
    }

    /**
     *  提交表单检测
     * 
     * @param car
     * @return
     */
    private boolean checkQueryItem(CarInfo car) {
        if (car.getChepaiNo().length() != 7) {
            Toast.makeText(MainActivity.this, "您输入的车牌号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (car.getChepaiNo().charAt(1) < 'A' 
                || car.getChepaiNo().charAt(1) > 'Z') {
            Toast.makeText(MainActivity.this, "您输入的车牌号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
        
      //发动机
        if (car.getEngineNo().equals("")) {
            Toast.makeText(MainActivity.this, "输入发动机号不为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (car.getEngineNo().length() < 5) {
            Toast.makeText(MainActivity.this, "请输入完整的发动机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    /**
     *  显示隐藏行驶证图示
     */
    private void hideShowXSZ() {
        View btn_help2 = (View) findViewById(R.id.ico_engine);
        Button btn_closeXSZ = (Button) findViewById(R.id.btn_closeXSZ);
        
        btn_help2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.VISIBLE);
            }
        });
        btn_closeXSZ.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.GONE);
            }
        });
    }

    /**
     *  避免穿透导致表单元素取得焦点
     */
    private class popOnTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            popXSZ.setVisibility(View.GONE);
            return true;
        }
    }
}
