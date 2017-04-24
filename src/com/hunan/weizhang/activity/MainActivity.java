package com.hunan.weizhang.activity;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sprzny.quanguo.R;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.ChepaiShortName;
import com.hunan.weizhang.model.ShortName;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.service.AllCapTransformationMethod;

public class MainActivity extends BaseActivity {
    
    private String defaultChepai = "鄂"; 
    
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
    private EditText chejia_number;
    private EditText telephone_number;
    
    // 发动机和车架号
    private LinearLayout row_engine;
    private LinearLayout row_chejia;
    
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
        chejia_number = (EditText) findViewById(R.id.chejia_number);
        short_name = (TextView) findViewById(R.id.chepai_sz);
        telephone_number = (EditText) findViewById(R.id.telephone_number);
        // ----------------------------------------------
        
        // 发动机和车架号 
        row_engine = (LinearLayout) findViewById(R.id.row_engine);
        row_chejia = (LinearLayout) findViewById(R.id.row_chejia);
        
        // 增加字母大小监控转换
        chepai_number.setTransformationMethod(new AllCapTransformationMethod());
        engine_number.setTransformationMethod(new AllCapTransformationMethod());
        chejia_number.setTransformationMethod(new AllCapTransformationMethod());
        
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
        
        // 车牌地区选择
        btn_cpsz.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShortNameList.class);
                String shortNameStr = short_name.getText().toString().trim();
                if (shortNameStr == null || shortNameStr.isEmpty()) {
                    shortNameStr =  defaultChepai;
                }
                intent.putExtra("select_short_name", shortNameStr);
                startActivityForResult(intent, 0);
            }
        });
        
        // 查询按钮
        btn_query.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 获取违章信息
                CarInfo car = new CarInfo();
                
                final String shortNameStr = short_name.getText().toString().trim();
                // 发动机和车牌字母必须大写
                final String chepaiNumberStr = chepai_number.getText().toString().trim().toUpperCase(Locale.getDefault());
                final String engineNumberStr = engine_number.getText().toString().trim().toUpperCase(Locale.getDefault());
                final String chejiaNumberStr = chejia_number.getText().toString().trim().toUpperCase(Locale.getDefault());
                final String telephoneNumberStr = telephone_number.getText().toString().trim();
                final String  haopaiTypeStr = haopai_lx.getSelectedItem().toString();

                // 车牌
                car.setShortName(shortNameStr);
                car.setChepaiNo(shortNameStr + chepaiNumberStr);
                car.setEngineNo(engineNumberStr);
                car.setChejiaNo(chejiaNumberStr);
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
        
        // 设置默认车牌号
        Intent intent = this.getIntent();
        String provName = intent.getStringExtra("provName");
        changeEngineAndChejia(ChepaiShortName.getShortName(provName));

        // 显示隐藏行驶证图示
        popXSZ = (View) findViewById(R.id.popXSZ);
        popXSZ.setOnTouchListener(new popOnTouchListener());
        hideShowXSZ();   
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
        case 0:
            // 用户切换车牌第一个字母时执行
            Bundle bundle = data.getExtras();
            String shortName = bundle.getString("short_name");
            changeEngineAndChejia(ChepaiShortName.getShortNameByName(shortName));
            break;
        }
    }
    
    /**
     * 改变表单（发动机和车架的显示）
     * 
     * @param shortName
     */
    private void changeEngineAndChejia(ShortName shortName) {
        if (shortName != null) {
            short_name.setText(shortName.getName());
        } else {
            short_name.setText(defaultChepai);
        }
        
        if (shortName.isNeedEngine()) {
            if (shortName.isShortEngine()) {
                engine_number.setHint(R.string.csy_short_engine_tip);
            } else {
                engine_number.setHint(R.string.csy_engine_tip);
            }
            row_engine.setVisibility(View.VISIBLE);
        } else {
            row_engine.setVisibility(View.GONE);
        }
        
        if (shortName.isNeedchejia()) {
            if (shortName.isShortchejia()) {
                chejia_number.setHint(R.string.csy_short_chejia_tip);
            } else {
                chejia_number.setHint(R.string.csy_chejia_tip);
            }
            row_chejia.setVisibility(View.VISIBLE);
        } else {
            row_chejia.setVisibility(View.GONE);
        }
    }

    /**
     *  提交表单检测
     * 
     * @param car
     * @return
     */
    private boolean checkQueryItem(CarInfo car) {
        // 车牌号校验
        if (car.getChepaiNo().length() != 7) {
            Toast.makeText(MainActivity.this, "您输入的车牌号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (car.getChepaiNo().charAt(1) < 'A' 
                || car.getChepaiNo().charAt(1) > 'Z') {
            Toast.makeText(MainActivity.this, "您输入的车牌号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 获取车辆信息情况
        ShortName shortName = ChepaiShortName.getShortNameByName(car.getShortName());
        if (shortName == null) {
            return false;
        }
        
        // 发动机
        if (shortName.isNeedEngine()) {
            if (car.getEngineNo().equals("")) {
                Toast.makeText(MainActivity.this, "输入发动机号不为空", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (shortName.isShortEngine()) {
                if (car.getEngineNo().length() < 6) {
                    Toast.makeText(MainActivity.this, "请输入发动机号后6位", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                if (car.getEngineNo().length() < 6) {
                    Toast.makeText(MainActivity.this, "请输入完整发动机号", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        
        //  车架校验
        if (shortName.isNeedchejia()) {
            if (car.getChejiaNo().equals("")) {
                Toast.makeText(MainActivity.this, "输入车架号不为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (shortName.isShortchejia()) {
                if (car.getChejiaNo().length() < 5) {
                    Toast.makeText(MainActivity.this, "请输入车架号后5位", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                if (car.getChejiaNo().length() < 5) {
                    Toast.makeText(MainActivity.this, "请输入完整车架号", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
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
    @SuppressLint("ClickableViewAccessibility")
    private class popOnTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            popXSZ.setVisibility(View.GONE);
            return true;
        }
    }
}
