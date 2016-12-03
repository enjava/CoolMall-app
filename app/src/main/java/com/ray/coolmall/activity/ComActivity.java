package com.ray.coolmall.activity;

/**
 * Created by en on 2016/11/8.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ray.coolmall.R;
import com.ray.coolmall.application.MyApplication;
import com.ray.coolmall.serialport.ChannelInfo;
import com.ray.coolmall.serialport.FrameOrder;
import com.ray.coolmall.service.PollingService;
import com.ray.coolmall.util.PollingUtils;
import com.ray.coolmall.util.SpUtil;
import com.ray.coolmall.util.ToastUtil;

import java.util.Set;

public class ComActivity extends Activity {
    private static final String TAG = ComActivity.class.getSimpleName();

    private MyApplication myApplication;
    private Button mbtnEd;
    private  TextView mTv;
    private  int churat=0;
    private String input="";
    private MyReceiver receiveBroadCast;  //广播实例
    private String  mbtnEdText;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    input+="1";
                    mbtnEd.setText(input);
                    break;
                case 2:
                    input+="2";
                    mbtnEd.setText(input);
                    break;
                case 3:
                    input+="3";
                    mbtnEd.setText(input);
                    break;
                case 4:
                    input+="4";
                    mbtnEd.setText(input);
                    break;
                case 5:
                    input+="5";
                    mbtnEd.setText(input);
                    break;
                case 6:
                    input+="6";
                    mbtnEd.setText(input);
                    break;
                case 7:
                    input+="7";
                    mbtnEd.setText(input);
                    break;
                case 8:
                    input+="8";
                    mbtnEd.setText(input);
                    break;
                case 10:
                    input+="A";
                    mbtnEd.setText(input);
                    break;
                case 11:
                    input+="B";
                    mbtnEd.setText(input);
                    break;
                case 12:
                    input+="C";
                    mbtnEd.setText(input);
                    break;
                case 13:
                    input+="D";
                    mbtnEd.setText(input);
                    break;
                case 14:
                    input+="E";
                    mbtnEd.setText(input);
                    break;
                case 15:
                    input+="F";
                    mbtnEd.setText(input);
                    break;
                case 101:
                    churat=4;
                    mTv.setText("支付宝扫码正在出货");
                    outGoods();
                    mbtnEd.setText(mbtnEdText);
                    break;
                case 102:
                    churat=6;
                    mTv.setText("微信扫码正在出货");
                    outGoods();
                    mbtnEd.setText(mbtnEdText);
                    break;
                case 104:
                    mTv.setText("出货失败");
                    mbtnEd.setText("");
                    break;
                case 110:
                    mTv.setText("请选择支付方式");
                    input="";
                    mbtnEd.setText("");
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
        myApplication= (MyApplication) this.getApplication();
        mbtnEd= (Button) findViewById(R.id.btn_ed);
        mTv= (TextView) findViewById(R.id.tv2);
        // 注册广播接收
        receiveBroadCast = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("tenray.outgoods.success");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
    }

    @Override
    protected void onDestroy() {
        myApplication.log("关闭串口");
        //关闭串口
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
        unregisterReceiver(receiveBroadCast);
        super.onDestroy();
    }

    public void outGoods() {

       Set<String> sets= SpUtil.getSet(this,input,null);
        if (sets!=null) {
            ChannelInfo channelInfo = new ChannelInfo(sets);
            int price = channelInfo.getPrice();
            String fra = FrameOrder.getOutGoods(myApplication.spFrameNumber(), input, price, churat);
            byte[] bytes = FrameOrder.getBytesOutGoods(myApplication.spFrameNumber(), input, price, churat);
            myApplication.log("出货调用前");
           boolean reuslt=  myApplication.sendToPort(bytes, "34");
            if (! reuslt){
                Message msg=Message.obtain();
                msg.what=104;
                mHandler.sendMessage(msg);
            }
            myApplication.log("出货调用后");
            System.out.println(fra);
        }
        else {
            ToastUtil.showTop(this,"货道选择错误",0,150);
            Message msg=Message.obtain();
            msg.what=110;
            mHandler.sendMessage(msg);
        }

    }
    //清除事件
    public void cleanEvent(View view){
        byte[] bytes=FrameOrder.getBytesCleanUpGoods(myApplication.spFrameNumber());
        myApplication.log("清除事件被调用前");
        myApplication.sendToPort(bytes,"38");
        myApplication.log("清除事件被调用后");
    }

    public void onClick(View view) {
        Message msg=Message.obtain();
        switch (view.getId()){
            case R.id.buttonA:
                msg.what=10;
                break;
            case R.id.buttonB:
                msg.what=11;
                break;
            case R.id.buttonC:
                msg.what=12;
                break;
            case R.id.buttonD:
                msg.what=13;
                break;
            case R.id.buttonE:
                msg.what=14;
                break;
            case R.id.buttonF:
                msg.what=15;
                break;
            case R.id.button1:
                msg.what=1;
                break;
            case R.id.button2:
                msg.what=2;
                break;
            case R.id.button3:
                msg.what=3;
                break;
            case R.id.button4:
                msg.what=4;
                break;
            case R.id.button5:
                msg.what=5;
                break;
            case R.id.button6:
                msg.what=6;
                break;
            case R.id.button7:
                msg.what=7;
                break;
            case R.id.button8:
                msg.what=8;
                break;
            case R.id.btn_weixin:
                msg.what=102;
                break;
            case R.id.btn_alipay:
                msg.what=101;
                break;
            default:
                Log.i(TAG, "测试onClick");
                break;
        }
        mHandler.sendMessage(msg);
    }

    public  class MyReceiver extends BroadcastReceiver//作为内部类的广播接收者
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals("tenray.outgoods.success"))
            {
                String data = intent.getStringExtra("tradedata");
                System.out.println(TAG+data);
                mTv.setText("出货成功");
                Message msg=Message.obtain();
                msg.what=110;
                mHandler.sendMessageDelayed(msg,2000);
                //ToastUtil.show(ComActivity.this,"出货成功");
            }
        }
    }
}
