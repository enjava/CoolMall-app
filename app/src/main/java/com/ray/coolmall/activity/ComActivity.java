package com.ray.coolmall.activity;

/**
 * Created by en on 2016/11/8.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ray.coolmall.R;
import com.ray.coolmall.serialport.FrameOrder;
import com.ray.coolmall.serialport.FrameUtil;
import com.ray.coolmall.serialport.SerialPortUtil;
import com.ray.coolmall.util.Constants;
import com.ray.coolmall.util.LogWriterUtil;
import com.ray.coolmall.util.SpUtil;
import com.ray.coolmall.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ComActivity extends Activity {
    private LogWriterUtil mLogWriter;
    private ReadThread mReadThread;
    private static final String TAG = ComActivity.class.getSimpleName();
    private SerialPortUtil serialport = null;
    private String backStr = "";
    private EditText mBack_et;
    private EditText mOrder_et;
    private EditText mComName;
    private CheckBox mcb;
    private String comPath = "/dev/ttyS2";
    private static int rollTimes ;
    private boolean isStop = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 弹出对话框,提示用户更新
                    mBack_et.setText(FrameUtil.fomatStr16(backStr).toUpperCase());
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

        File logf = new File(Environment.getExternalStorageDirectory()
                + File.separator + "DemoLog.txt");
        try {
            mLogWriter = LogWriterUtil.open(logf.getAbsolutePath());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        mReadThread = new ReadThread();
        isStop = false;
        mReadThread.start();
        mBack_et = (EditText) findViewById(R.id.editText3);
        mOrder_et = (EditText) findViewById(R.id.editText4);
        mComName = (EditText) findViewById(R.id.dtced);
        mcb = (CheckBox) findViewById(R.id.cb);
    }

    public void sendComand(View v) {
        if (serialport == null) {
            initSerialport(comPath);
        }
        if (serialport.getmSerialPort() == null) {
            initSerialport(comPath);
            ToastUtil.show(this, "串口打开失败，请检查串口设置是否正确");
            return;
        }
        String orderStr = mOrder_et.getText().toString();
        if (FrameUtil.isEmpty(orderStr)) {
            ToastUtil.show(this, "发送命令不能为空");
            return;
        }
        try {
            if (mcb.isChecked())
                serialport.sendToPort(FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(orderStr)));
            else
                serialport.sendToPort(FrameUtil.hexStringToBytes(orderStr));
        } catch (IOException e) {
            ToastUtil.show(this, "发送命令失败");
        }
    }

    //同步时间
    public void synTime(View v) {
        if (serialport == null) {
            initSerialport(comPath);
        }
        if (serialport.getmSerialPort() == null) {
            initSerialport(comPath);
            ToastUtil.show(this, "串口打开失败，请检查串口设置是否正确");
            return;
        }
        int itimes = SpUtil.getInt(this, Constants.FRAME_NUMBER, 0);
        String ml = FrameOrder.getSynTime(itimes, new Date());
        byte[] abc = FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(ml));
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
                String stringBack = FrameUtil.bytesToHexString(byt);
                if (stringBack.indexOf(FrameOrder.comHead)==0)
                    backStr="";
                backStr += stringBack;
                if (FrameUtil.checkBack(backStr)) {
                    String arg=  backStr;
                    Message msg = Message.obtain();
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    String [] args=arg.split(" ");
                    if (args.length>5&&args[5]=="02"){
                        if (mLogWriter!=null)
                        log(arg);
                    }
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        //关闭串口
        if (serialport != null)
            serialport.closeSerialPort();
        isStop=true;
        super.onDestroy();
    }

    public void setComName(View view) {
        comPath = mComName.getText().toString().trim();
        if (serialport != null) {
            serialport.closeSerialPort();
            initSerialport(comPath);
        } else {
            initSerialport(comPath);
        }
        ToastUtil.show(this, "串口更改成功");
    }

    public void log(String msg) {
        //Log.d(TAG, msg);
        try {
            mLogWriter.print(msg);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            while (!isStop) {
                try {
                    rollTimes++;
                    if (rollTimes>65500)
                        rollTimes=0;
                    byte [] bytes=FrameOrder.getBytesRoll(rollTimes);
                    if (serialport.getmSerialPort()!=null)
                        serialport.sendToPort(bytes);
                   Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mLogWriter!=null)
                        log("FrameOrder.getBytesRoll被打断");
                    return;
                }
            }
        }
    }

}
