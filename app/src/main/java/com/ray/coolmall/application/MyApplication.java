package com.ray.coolmall.application;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ray.coolmall.serialport.ChannelInfo;
import com.ray.coolmall.serialport.FrameOrder;
import com.ray.coolmall.serialport.FrameUtil;
import com.ray.coolmall.serialport.SerialPortUtil;
import com.ray.coolmall.service.PollingService;
import com.ray.coolmall.util.CommonUtil;
import com.ray.coolmall.util.Constants;
import com.ray.coolmall.util.FileUtils;
import com.ray.coolmall.util.LogWriterUtil;
import com.ray.coolmall.util.PollingUtils;
import com.ray.coolmall.util.SpUtil;
import com.ray.coolmall.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by en on 2016/11/30.
 */

public class MyApplication extends Application {
    private SerialPortUtil serialport = null;
    private Map<String,ChannelInfo> channelInfoMap= new ConcurrentHashMap<String,ChannelInfo>();
    private List<String> channels;
    private String returnStr = "";
    private String backStr = "";
    private LogWriterUtil mLogWriter;
    private String filePath = "";
    private String fileName = "";
    private static String  orderCode;
    //同步时间
    private static final int iSynTime = 101;
    //测试连接
    private static final int TEST_LINK = 1;
    //轮询主板
    private static final int ROLL_PANEL = 2;
    //获取主板货道数据
    private static final int DATA_PANEL = 30;
    //上位机发送货道数据给主板
    private static final int SYN_DATA_PANEL = 31;
    //上位机控制出货
    private static final int CONTROL_OUT_GOODS = 34;
    //上位机发送交易数据给主板
    private static final int TRADE_DATA_PANEL = 36;
    //上位机同步主板时间
    private static final int SYN_TIME = 37;
    //上位机清除上货事件标志
    private static final int CLEAN_UPGOODS_EVENT = 38;
    //上位机通知主板写货道数据到存储器
    private static final int WRITE_DATA_STORAGE = 39;
    private String comPath;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TEST_LINK:
                    backStr = "";
                    break;
                case ROLL_PANEL:
                    break;
                case DATA_PANEL:
                    setChannelData();
                    break;
                case SYN_DATA_PANEL:

                    break;
                case CONTROL_OUT_GOODS:

                    break;
                case TRADE_DATA_PANEL:

                    break;
                case SYN_TIME:

                    break;
                case CLEAN_UPGOODS_EVENT:

                    break;
                case WRITE_DATA_STORAGE:

                    break;
                case iSynTime:
                    synTime();
                    break;
                default:
                    Log.i(TAG, "测试");
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        // 程序创建的时候执行
        Log.d(TAG, "onCreate");
        super.onCreate();

        comPath = SpUtil.getString(this, Constants.COM_PATH, "");
        if (TextUtils.isEmpty(comPath)) {
            comPath = "/dev/ttyS2";
            SpUtil.putString(this, Constants.COM_PATH, "/dev/ttyS2");
        }
        if (FileUtils.isSdcardExist()) {
            filePath = Environment.getExternalStorageDirectory() + File.separator + "CoolMall" + File.separator + "log" + File.separator;
            FileUtils.createDirFile(filePath);
            fileName = CommonUtil.formatDate("yyyy-MM-dd") + ".Log";
            initLogWriterUtil();
        }
        //同步时间
        Message msg = Message.obtain();
        msg.what = iSynTime;
        mHandler.sendMessage(msg);
        initChannel();
        PollingUtils.startPollingService(this, 500, PollingService.class, PollingService.ACTION);
    }
    //初始化货道
    public void  initChannel(){
       Set<String> channelSet= SpUtil.getSet(this,Constants.CHANEL_NAME,null);
        if (channelSet==null) {
            String[] mProductChnanels = new String[]{
                    "A1", "A3", "A5", "A7", "B1",
                    "B2", "B3", "B4", "B5", "C1",
                    "C2", "C3", "C4", "C5", "C6",
                    "C7", "C8", "D6", "E3", "E5"
            };
            channelSet = new HashSet<String>();
            for (int i = 0; i < mProductChnanels.length; i++) {
                channelSet.add(mProductChnanels[i]);
                channels.add(mProductChnanels[i]);
            }
            SpUtil.putSet(this,Constants.CHANEL_NAME,channelSet);
        }else
            channels=  new ArrayList<String>(channelSet);

    }

    public void initLogWriterUtil() {
        File logf = new File(filePath + fileName);
        try {
            mLogWriter = LogWriterUtil.open(logf.getAbsolutePath(),true);
            log("---------程序开始执行-------");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
        //关闭串口
        if (serialport != null)
            serialport.closeSerialPort();
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
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
                String stringBack = FrameUtil.fomatStr16(FrameUtil.bytesToHexString(byt)).toUpperCase() + " ";
                if (stringBack.indexOf(FrameOrder.comHead) == 0)
                    backStr = "";
                backStr += stringBack;
                if (FrameUtil.checkBack(backStr)) {
                    returnStr = backStr.trim();
                    backStr = "";
                    String[] args = returnStr.split(" ");
                    if (args.length > 4) {
                        Message msg = Message.obtain();
                        orderCode = args[5];
                        switch (args[5]) {
                            case "01":
                                //1测试驱动板链接 值0x01
                                break;
                            case "02":
                                //2查询主板状态 值0x02
                                msg.what = ROLL_PANEL;
                                break;
                            case "30":
                                msg.what = DATA_PANEL;
                                //3 获取主板货道数据 值0x30
                                break;
                            case "31":
                                //4 上位机发送货道数据给主板 值0x31
                                break;
                            case "34":
                                //5 上位机控制出货 值0x34
                                break;
                            case "36":
                                //6 上位机发送交易数据给主板 值0x36
                                break;
                            case "37":
                                msg.what = 37;
                                //7 同步时间  值0x37
                                break;
                            case "38":
                                //8 上位机清除上货事件标志  值0x38
                                break;
                            case "39":
                                //9 上位机通知主板写货道数据到存储器 值0x39
                                break;
                            default:
                                break;
                        }
                        mHandler.sendMessage(msg);
                    }
                }

            }
        });

    }

    public void synTime() {
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
            serialport.sendToPort(abc);
        } catch (Exception e) {
            ToastUtil.show(this, "更改时间失败");
        }
    }

    public void setComPath(String comPath) {
        this.comPath = comPath;
        SpUtil.putString(this, Constants.COM_PATH, comPath);
        initSerialport(comPath);
    }

    public synchronized boolean sendToPort(byte[] bytes, String order) {
        orderCode = "";
        boolean result=false;
        try {
            if (serialport.getmSerialPort() != null) {
                for (int i = 0; i < 5;i++ ) {
                    if(i>0) {
                        System.out.println(i+"orderCode:"+orderCode+" order:"+order);
                        log(i+"orderCode:"+orderCode+" order:"+order);
                    }
                    if(order.equals("30"))
                        order="30";
                    serialport.sendToPort(bytes);
                    Thread.sleep(200);
                    if (order.equals(orderCode)) {
                        result=true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public void setChannelData(){
        String[] args = returnStr.split(" ");
        System.out.println(returnStr);
        if (args[5].equals("30")){
            String chanelName=args[8];
            String [] prices=new String[]{args[20],args[21],args[22],args[23]};
            String [] stocks=new String[]{args[28],args[29],args[30],args[31]};
            String [] volumes=new String[]{args[12],args[13],args[14],args[15]};
            int price=FrameUtil.hiInt4String(prices);
            int stock=FrameUtil.hiInt4String(stocks);
            int volume=FrameUtil.hiInt4String(volumes);
            ChannelInfo channelInfo=new ChannelInfo();
            channelInfo.setName(chanelName);
            channelInfo.setPrice(price+"");
            channelInfo.setStock(stock);
            channelInfo.setVolume(volume);
            channelInfoMap.put(chanelName,channelInfo);
        }
    }
    public int spFrameNumber(){
        int itimes = SpUtil.getInt(this, Constants.FRAME_NUMBER, 0);
        if (itimes > 65000)
            itimes = 0;
        SpUtil.putInt(this, Constants.FRAME_NUMBER, ++itimes);
        return itimes;
    }

    public void log(String msg) {
        try {
            if (FileUtils.isSdcardExist()) {
                String logFileName = CommonUtil.formatDate("yyyy-MM-dd") + ".Log";
                if (!logFileName.equals(fileName)) {
                    fileName = logFileName;
                    initLogWriterUtil();
                }
                mLogWriter.print(msg);
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public Map<String, ChannelInfo> getChannelInfoMap() {
        return channelInfoMap;
    }

    public List<String> getChannels() {
        return channels;
    }
}