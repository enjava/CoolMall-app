package com.ray.coolmall.serialport;

/**
 * Created by en on 2016/11/5.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android_serialport_api.SerialPort;

/**
 * 串口操作类
 *
 * @author Jerome
 *
 */
public class SerialPortUtil {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    public String path = "/dev/ttyS2";
    private int baudrate = 9600;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;

    public static Date time=null;

    public SerialPortUtil(String path) {
        this.path=path;
    }

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public static SerialPortUtil getInstance(String path) {
        if (null == portUtil) {
            portUtil = new SerialPortUtil(path);
            portUtil.onCreate();
        }
        return portUtil;
    }

    /**
     * 初始化串口信息
     */
    public void onCreate() {
        try {
            mSerialPort = new SerialPort(new File(path), baudrate,0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            //time
            mReadThread = new ReadThread();
            isStop = false;
            mReadThread.start();
            time=new Date();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //initBle();
    }

    /**
     * 往串口发送数据

     * @param order	待发送数据
     */
    public synchronized void sendToPort(byte[] order) throws Exception {

        OutputStream out = null;

        try {
            out = mSerialPort.getOutputStream();
            out.write(order);
            out.flush();


        } catch (IOException e) {
            throw new IOException();
        }
        //Thread.sleep(50);
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isStop && !isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[256];
                    size=   mInputStream.read(buffer);

                    if (size > 0) {
//                        if(MyLog.isDyeLevel()){
//                            MyLog.log(TAG, MyLog.DYE_LOG_LEVEL, "length is:"+size+",data is:"+new String(buffer, 0, size));
//                        }
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceive(buffer, size);
                        }
                    }
                    //Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {

        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
            portUtil=null;
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort=null;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public  SerialPort getmSerialPort() {
        return mSerialPort;
    }
}
