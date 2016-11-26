package com.ray.coolmall.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ray.coolmall.serialport.FrameOrder;
import com.ray.coolmall.serialport.SerialPortUtil;


/**
 * Created by en on 2016/11/26.
 * 轮询服务
 */

public class PollingService extends Service {
    public static final String ACTION = "com.ray.coolmall.service.PollingService";
    public static SerialPortUtil serialport = null;
//    private Notification mNotification;
//    private NotificationManager mManager;

    private static int rollTimes =10000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       // initNotifiManager();
        super.onCreate();
    }

    @Override
    public int  onStartCommand(Intent intent, int flags, int startId) {
         new PollingThread().start();
        return super.onStartCommand(intent,flags,startId);
    }


    //初始化通知栏配置
    private void initNotifiManager() {
//        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        int icon = R.mipmap.ic_launcher;
//        mNotification = new Notification();
//        mNotification.icon = icon;
//        mNotification.tickerText = "New Message";
//        mNotification.defaults |= Notification.DEFAULT_SOUND;
//        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    //弹出Notification
    private void sendRollCommand() {
        rollTimes++;
        if (rollTimes>65500)
            rollTimes=0;
        try {
            byte[] bytes = FrameOrder.getBytesRoll(rollTimes);
            if (serialport.getmSerialPort() != null)
                serialport.sendToPort(bytes);
            //Thread.sleep(200);
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
            if (count % 5 == 0) {
                System.out.println("New message!"+count);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

    public void setSerialport(SerialPortUtil serialport) {
        this.serialport = serialport;
    }
}
