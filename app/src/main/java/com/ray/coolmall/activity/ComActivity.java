package com.ray.coolmall.activity;

/**
 * Created by en on 2016/11/8.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ray.coolmall.R;
import com.ray.coolmall.serialport.SerialPortUtil;
import com.ray.coolmall.util.Constants;
import com.ray.coolmall.util.SpUtil;
import com.ray.coolmall.util.ThUtil;
import com.ray.coolmall.util.ToastUtil;

import java.io.IOException;
import java.util.Date;

import static android.content.ContentValues.TAG;




public class ComActivity extends Activity {
    private SerialPortUtil serialport = null;
    private String backStr = "";
    private EditText mBack_et;
    private EditText mOrder_et;
    private EditText mComName;
    private CheckBox mcb;
    private  String comPath="/dev/ttyS2";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 弹出对话框,提示用户更新
                    mBack_et.setText(ThUtil.fomatStr16(backStr).toUpperCase());
                    backStr = "";
                    break;
                default:

                    Log.i(TAG, "测试");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com);
        mBack_et = (EditText) findViewById(R.id.editText3);
        mOrder_et = (EditText) findViewById(R.id.editText4);
        mComName = (EditText) findViewById(R.id.dtced);
        mcb = (CheckBox) findViewById(R.id.cb);
    }

    public void sendComand(View v) {
        if (serialport == null) {
            initSerialport(comPath);
        }
        if (serialport.getmSerialPort()==null) {
            initSerialport(comPath);
            ToastUtil.show(this, "串口打开失败，请检查串口设置是否正确");
            return;
        }
        String orderStr = mOrder_et.getText().toString();
        if (ThUtil.isEmpty(orderStr)){
            ToastUtil.show(this, "发送命令不能为空");
            return;
        }
        try {
            if (mcb.isChecked())
                serialport.sendToPort(ThUtil.hexString2Bytes(ThUtil.getCRCStr(orderStr)));
            else
                serialport.sendToPort(ThUtil.hexString2Bytes(orderStr));
        } catch (IOException e) {
            ToastUtil.show(this, "发送命令失败");
        }
    }

    //同步时间
    public void synTime(View v) {
        if (serialport == null) {
            initSerialport(comPath);
        }
        if (serialport.getmSerialPort()==null) {
            initSerialport(comPath);
            ToastUtil.show(this, "串口打开失败，请检查串口设置是否正确");
            return;
        }
        int itimes = SpUtil.getInt(this, Constants.FRAME_NUMBER, 0);
        String ml = ThUtil.synTime(new Date(), itimes);
        byte[] abc = ThUtil.hexString2Bytes(ThUtil.getCRCStr(ml));
        if (itimes > 65000)
            itimes = 0;
        SpUtil.putInt(this, Constants.FRAME_NUMBER, ++itimes);
        try {
            backStr = "";
            serialport.sendToPort(abc);
        } catch (IOException e) {
            ToastUtil.show(this, "更改时间失败");
        }

    }

    //初始化串口
    public void initSerialport(String comName) {
        serialport = SerialPortUtil.getInstance(comName);
        serialport.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
                byte[] byt = new byte[size];
                for (int i = 0; i < size; i++) {
                    byt[i] = buffer[i];
                }
                String stringBack = ThUtil.bytesToHexString(byt);
                backStr += stringBack;
                if (ThUtil.checkBack(backStr)) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        //关闭串口
        if (serialport != null)
            serialport.closeSerialPort();
        super.onDestroy();
    }

    public void setComName(View view) {
        comPath=mComName.getText().toString().trim();
        if (serialport != null) {
            serialport.closeSerialPort();
            initSerialport(comPath);
        }
        else {
            initSerialport(comPath);
        }
        ToastUtil.show(this, "串口更改成功");

    }
}
