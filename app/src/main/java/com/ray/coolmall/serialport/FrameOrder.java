package com.ray.coolmall.serialport;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by en on 2016/11/25.
 */

public class FrameOrder {
    //起始标志1   起始标志2  版本号  为常量
    public static final String comHead = "45 46 CB ";

    /**
     * 1 .测试驱动板链接
     *
     * @param frameId
     * @return
     */
    public static String getLink(int frameId) {
        if (frameId > 65535)
            frameId = 0;

        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 01 ";
        //3. DATA
        String dataFrame = "";

        //4. DATA  Length
        String dataLengthFrame = "00 00";
        //5. return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 1 .测试驱动板链接
     *
     * @param frameId
     * @return
     */
    public static byte[] getBytesLink(int frameId) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getLink(frameId)));
    }

    /**
     * 2 主板状态轮询,获取状态更新标志
     *
     * @param frameId
     * @return
     */
    public static String getRoll(int frameId) {
        if (frameId > 65535)
            frameId = 0;

        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 02 ";
        //3. DATA
        String dataFrame = "";

        //4. DATA  Length
        String dataLengthFrame = "00 00";
        //5. return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 2 主板状态轮询,获取状态更新标志
     *
     * @param frameId
     * @return
     */
    public static byte[] getBytesRoll(int frameId) {

        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getRoll(frameId)));
    }

    /**
     * 3 获取主板货道数据  如：45 46 CB 11 00 30 04 00 A1 00 00 00
     *
     * @param frameId 帧编号
     * @param channel 货道编号（两位16进制，如A1  C3等）
     * @return 获得主板信息
     */
    public static String getPanel(int frameId, String channel) {
        if (frameId > 65535)
            frameId = 0;
        if (channel.length() > 2)
            return "";
        //帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //命令号
        String orderNum = " 30 ";
        //DATA
        String dataFrame = channel + " 00 00 00";

        //DATA  Length
        String dataLengthFrame = getDatalength(dataFrame) + " ";
        // 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
        //return "";
    }

    /**
     * 3 获取主板货道数据  如：45 46 CB 11 00 30 04 00 A1 00 00 00
     *
     * @param frameId 帧编号
     * @param channel 货道编号（两位16进制，如A1  C3等）
     * @return 获得主板信息
     */
    public static byte[] getBytesPanel(int frameId, String channel) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getPanel(frameId, channel)));
    }

    /**
     * 4  上位机发送货道数据给主板
     *
     * @param frameId 帧编号
     * @param channel 货道
     * @param price   价格
     * @param amount  库存
     * @param volume  容量
     * @return
     */
    public static String getWriteToChannelData(int frameId, String channel, int price, int amount, int volume) {
        if (frameId > 65535)
            frameId = 0;
        if (amount > volume)
            return "";
        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 31 ";
        //3. DATA
        //4字节货道编号
        String channelFrame = " " + channel + " 00 00 00 ";
        //4字节容量
        String volumeFrame = FrameUtil.hiString4Bytes(volume);
        //4字节货道故障码
        String faultCodeFrame = " 00 00 00 00 ";
        //4字节货道价格
        String priceFrame = FrameUtil.hiString4Bytes(price) + " ";
        //4字节货道库存
        String stockFrame = FrameUtil.hiString4Bytes(amount) + " ";
        //4字节货道商品ID
        String productIDFrame = channel + " 00 00 00";

        String dataFrame = channelFrame + volumeFrame + faultCodeFrame + priceFrame + stockFrame + productIDFrame;

        //4. DATA  Length
        String dataLengthFrame = getDatalength(dataFrame);
        //5. return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 4  上位机发送货道数据给主板
     *
     * @param frameId 帧编号
     * @param channel 货道
     * @param price   价格
     * @param amount  库存
     * @param volume  容量
     * @return
     */
    public static byte[] getBytesWriteToChannelData(int frameId, String channel, int price, int amount, int volume) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getWriteToChannelData(frameId, channel, price, amount, volume)));
    }

    /**
     * 4 上位机发送货道数据给主板
     *
     * @param frameId 帧编号
     * @param channel 货道
     * @param price   价格
     * @param amount  库存
     * @return
     */
    public static String getWriteToChannelData(int frameId, String channel, int price, int amount) {

        return getWriteToChannelData(frameId, channel, price, amount, amount);
    }

    /**
     * 4 上位机发送货道数据给主板
     *
     * @param frameId 帧编号
     * @param channel 货道
     * @param price   价格
     * @param amount  库存
     * @return
     */
    public static byte[] getBytesWriteToChannelData(int frameId, String channel, int price, int amount) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getWriteToChannelData(frameId, channel, price, amount)));
    }

    /**
     * 5 上位机控制出货
     *
     * @param frameId 帧编号
     * @param channel 货道编号（两位16进制，如A1  C3等）
     * @param price   价格
     * @param payMode 支付方式
     * @return 出货命令
     */
    public static String getOutGoods(int frameId, String channel, int price, int payMode) {
        if (frameId > 65535)
            frameId = 0;
        //帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //命令号
        String orderNum = " 34 ";
        //DATA
        /* 1. 4 字节货道编号 2.  4 字节货道价格 3. 4 字节支付方式 4.交易编号，长度不能超过31 个字节，以null 结尾*/
        String channelFrame = " " + channel + " 00 00 00 ";
        String priceFrame = FrameUtil.hiString4Bytes(price) + " ";
        String payModeFrame = FrameUtil.hiString4Bytes(payMode) + " ";
        String tradeNumFrame = FrameUtil.hiString4Bytes(FrameUtil.nextInt()) + " FF FF FF FF 05 05 05 05";
        if (payMode == 0) {
            priceFrame="00 00 00 00 ";
            tradeNumFrame = "";
        }
        String dataFrame = channelFrame + priceFrame + payModeFrame + tradeNumFrame;
        //DATA  Length
        String dataLengthFrame = getDatalength(dataFrame);
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 5 上位机控制出货
     *
     * @param frameId 帧编号
     * @param channel 货道编号（两位16进制，如A1  C3等）
     * @param price   价格
     * @param payMode 支付方式
     * @return 出货命令
     */
    public static byte[] getBytesOutGoods(int frameId, String channel, int price, int payMode) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getOutGoods(frameId, channel, price, payMode)));
    }

    /**
     * 5 上位机控制出货
     *
     * @param frameId 帧编号
     * @param channel 货道编号（两位16进制，如A1  C3等）
     * @param price   价格
     * @return 出货命令
     */
    public static String getOutGoods(int frameId, String channel, int price) {
        return getOutGoods(frameId, channel, price, 0);
    }

    public static byte[] getBytesOutGoods(int frameId, String channel, int price) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getOutGoods(frameId, channel, price)));
    }

    /**
     * 6 上位机发送交易数据给主板
     *
     * @param frameId 帧编号
     * @return 56       流水号        价格           货道编号       支付类型       否已经成功支付标志     字节故障码                 交易编号
     * 45 46 CB 06 00 36   38 00   03 00 00 00   FA 00 00 00    A2 00 00 00    01 00 00 00    01 00 00 00     04 00 00 00     31 32 33 A5 49 0C 01 00 0A 00 00 00 85 34 01 08 AA 07 00 00 00 86 01 00 FF FF FF FF 05 05 05 05     9A 38
     */
    public static String getTradeDate(int frameId) {
        if (frameId > 65535)
            frameId = 0;

        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 36 ";
        //3. DATA
        String dataFrame = "";

        //4. DATA  Length
        String dataLengthFrame = "00 00";
        //5. return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 6 上位机发送交易数据给主板
     *
     * @param frameId 帧编号
     * @return 56       流水号        价格           货道编号       支付类型       否已经成功支付标志     字节故障码                 交易编号
     * 45 46 CB 06 00 36   38 00   03 00 00 00   FA 00 00 00    A2 00 00 00    01 00 00 00    01 00 00 00     04 00 00 00     31 32 33 A5 49 0C 01 00 0A 00 00 00 85 34 01 08 AA 07 00 00 00 86 01 00 FF FF FF FF 05 05 05 05     9A 38
     */
    public static byte[] getBytesTradeDate(int frameId) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getTradeDate(frameId)));
    }

    /**
     * 7 上位机同步主板时间
     *
     * @param time    要更新的时间
     * @param frameId 帧编号
     * @return 同步时间
     */
    public static String getSynTime(int frameId, Date time) {
        if (frameId > 65535)
            frameId = 0;
        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 37 ";
        //3. DATA
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(time);
        String year = dateString.substring(0, 4);
        String month = dateString.substring(4, 6);
        String day = dateString.substring(6, 8);
        String h = dateString.substring(8, 10);
        String m = dateString.substring(10, 12);
        String s = dateString.substring(12, 14);
        year = FrameUtil.hiString2Bytes(Integer.parseInt(year)) + " ";
        month = FrameUtil.toHexString(Byte.parseByte(month)) + " ";
        day = FrameUtil.toHexString(Byte.parseByte(day)) + " ";
        h = FrameUtil.toHexString(Byte.parseByte(h)) + " ";
        m = FrameUtil.toHexString(Byte.parseByte(m)) + " ";
        s = FrameUtil.toHexString(Byte.parseByte(s));
        String dataFrame = year + month + day + h + m + s;
        //4. DATA  Length
        String dataLengthFrame = getDatalength(dataFrame);
        //return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 7 上位机同步主板时间
     *
     * @param time    要更新的时间
     * @param frameId 帧编号
     * @return 同步时间
     */
    public static byte[] getBytesSynTime(int frameId, Date time) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getSynTime(frameId, time)));
    }

    /**
     * 8 上位机清除上货事件标志
     *
     * @param frameId
     * @return
     */
    public static String getCleanUpGoods(int frameId) {
        if (frameId > 65535)
            frameId = 0;

        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 38 ";
        //3. DATA
        String dataFrame = "";

        //4. DATA  Length
        String dataLengthFrame = "00 00";
        //5. return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 8 上位机清除上货事件标志
     *
     * @param frameId
     * @return
     */
    public static byte[] getBytesCleanUpGoods(int frameId) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getCleanUpGoods(frameId)));
    }

    /**
     * 9 上位机通知主板写货道数据到存储器
     *
     * @param frameId
     * @return
     */
    public static String getWriteToStorage(int frameId) {
        if (frameId > 65535)
            frameId = 0;

        //1. 帧编号
        String frameNum = FrameUtil.hiString2Bytes(frameId);
        //2. 命令号
        String orderNum = " 39 ";
        //3. DATA
        String dataFrame = "";
        //4. DATA  Length
        String dataLengthFrame = "00 00";
        //5. return 命令常量 comHead,  帧编号, 命令号,  DATA Length,  DATA
        return comHead + frameNum + orderNum + dataLengthFrame + dataFrame;
    }

    /**
     * 9 上位机通知主板写货道数据到存储器
     *
     * @param frameId
     * @return
     */
    public static byte[] getBytesWriteToStorage(int frameId) {
        return FrameUtil.hexStringToBytes(FrameUtil.getCRCStr(getWriteToStorage(frameId)));
    }

    /**
     * 获取数据长度
     *
     * @param data 数据
     * @return
     */
    public static String getDatalength(String data) {
        data = FrameUtil.replase(data);
        return FrameUtil.hiString2Bytes(data.length() / 2);
    }
}