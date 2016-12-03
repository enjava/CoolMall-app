package com.ray.coolmall.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.ray.coolmall.application.MyApplication;
import com.ray.coolmall.serialport.FrameOrder;

import java.util.List;


/**
 * Created by en on 2016/11/26.
 * 轮询服务
 */

public class PollingService extends Service {
    public static final String ACTION = "com.ray.coolmall.service.PollingService";
   private List<String> channels;
    private int listSize=-1;
    private static int rollTimes = 28000;
    private MyApplication myApplication;
    private PollReceiver pollReceiver;  //广播实例
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        myApplication = (MyApplication) getApplication();

        // 注册广播接收
        pollReceiver = new PollReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("tenray.outgoods.success");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(pollReceiver, filter);
    }
    @Override
    public boolean onUnbind(Intent intent)
    {
         unregisterReceiver(pollReceiver);
        System.out.println("Service:onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new PollingThread().start();
        return super.onStartCommand(intent, flags, startId);
    }


    //初始化通知栏配置
    private void initNotifiManager() {

    }

    int num;
    private void sendRollCommand() {
        rollTimes++;
        if (rollTimes > 65500)
            rollTimes = 0;
        try {
            if (listSize==-1||channels.size()>listSize){
                channels=  myApplication.getChannels();
                if (channels!=null){
                    listSize++;
                    byte[] bytes = FrameOrder.getBytesPanel(rollTimes,channels.get(listSize));
                    myApplication.sendToPort(bytes, "30");

                }
            }
            else {
                    //发送交易数据给主板
                    byte[] bytes = FrameOrder.getBytesTradeDate(rollTimes);
                    myApplication.sendToPort(bytes, "36");
            }
        } catch (Exception e) {

        }
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     *
     * @Author en
     * @Create 2016-11-26 上午 09:18:34
     */
    long count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            sendRollCommand();
            count++;
            //当除计数能被5整时弹出通知
            if (count % 50 == 0) {
                System.out.println("New message!" + count);
            }
        }
    }


    public  class PollReceiver extends BroadcastReceiver//作为内部类的广播接收者
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals("tenray.outgoods.success"))
            {
                String data = intent.getStringExtra("tradedata");
                System.out.println("PollingService:"+data);
                myApplication.log(data);
                //也可以终止广播,权限小的接收者就接收不到广播了
               // abortBroadcast();
            }
        }
    }
}
