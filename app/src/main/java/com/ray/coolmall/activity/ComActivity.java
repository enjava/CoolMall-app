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
import android.widget.CheckBox;
import android.widget.EditText;

import com.ray.coolmall.R;
import com.ray.coolmall.application.MyApplication;
import com.ray.coolmall.serialport.ChannelInfo;
import com.ray.coolmall.serialport.FrameOrder;
import com.ray.coolmall.service.PollingService;
import com.ray.coolmall.util.PollingUtils;
import com.ray.coolmall.util.ToastUtil;

import java.util.Map;

public class ComActivity extends Activity {
    private static final String TAG = ComActivity.class.getSimpleName();
    private EditText mBack_et;
    private EditText mOrder_et;
    private EditText mComName;
    private CheckBox mcb;
    private String comPath = "/dev/ttyS2";
    private static int ROLLTIMES;
    private MyApplication myApplication;
    private  Map<String ,ChannelInfo> map;
    private MyReceiver receiveBroadCast;  //广播实例
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 弹出对话框,提示用户更新
                    //mBack_et.setText(FrameUtil.fomatStr16(backStr).toUpperCase());

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
        myApplication= (MyApplication) this.getApplication();

        // 注册广播接收
        receiveBroadCast = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("tenray.outgoods.success");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
    }

    public void sendComand(View v) {

    }

    //同步时间
    public void synTime(View v) {

    }


    @Override
    protected void onDestroy() {
        myApplication.log("关闭串口");
        //关闭串口
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
        unregisterReceiver(receiveBroadCast);
        super.onDestroy();
    }

    public void setComName(View view) {

        ToastUtil.show(this, "串口更改成功");
    }
    int churat=6;
    public void outGoods(View view) {
        if (map==null)
            map = myApplication.getChannelInfoMap();
        int price =Integer.parseInt(map.get("C2").getPrice());
        String fra= FrameOrder.getOutGoods(myApplication.spFrameNumber(),"C2",price,churat);
        byte[] bytes=FrameOrder.getBytesOutGoods(myApplication.spFrameNumber(),"C2",price,churat);
        myApplication.log("出货调用前");
        myApplication.sendToPort(bytes,"34");
        myApplication.log("出货调用后");
        System.out.println(fra);

    }
    //清除事件
    public void cleanEvent(View view){
        byte[] bytes=FrameOrder.getBytesCleanUpGoods(myApplication.spFrameNumber());
        myApplication.log("清除事件被调用前");
        myApplication.sendToPort(bytes,"38");
        myApplication.log("清除事件被调用后");
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
                ToastUtil.show(ComActivity.this,"出货成功");
            }
        }
    }
}
