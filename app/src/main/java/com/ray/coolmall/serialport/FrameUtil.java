package com.ray.coolmall.serialport;


import java.util.Random;

/**
 * Created by en on 2016/11/2.
 */
public class FrameUtil {
    //java CRC16校验
    private static int get_crc16 (byte[] bufData, int buflen, byte[] pcrc)
    {
        int ret = 0;
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;


        if (buflen == 0)
        {
            return ret;
        }
        for (i = 0; i < buflen; i++)
        {
            CRC ^= ((int)bufData[i] & 0x000000ff);
            for (j = 0; j < 8; j++)
            {
                if ((CRC & 0x00000001) != 0)
                {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                }
                else
                {
                    CRC >>= 1;
                }
            }
        }

        pcrc[0] = (byte)(CRC & 0x00ff);
        pcrc[1] = (byte)(CRC >> 8);

        return ret;
    }

    //获得CRC校验字符串  如  "88 DF"
    public static String getCRC(byte[] bytes){

        byte[] bb = new byte[2];
        get_crc16(bytes, bytes.length, bb);
        return Integer.toHexString((int)bb[0] & 0x000000ff)+" "+Integer.toHexString((int)bb[1] & 0x000000ff);
    }

    //byte转换成16进制的字符串
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] hexStringToBytes(String hexstr) {
        hexstr=replase(hexstr);
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }
    //四字节高低位
    public static String hiString4Bytes(int input){
        // 拼装成 正确的int
        return toHexString((byte) (input & 0xff))+" "+toHexString((byte) ((input & 0xff00) >> 8))+" "
                +toHexString((byte) ((input & 0xff0000) >> 16))+" "+toHexString((byte) ((input & 0xff000000) >> 24));
    }
    //双字节高低位
    public static String hiString2Bytes(int input){
        if(input>=65536)
            return input+"";
        // 拼装成 正确的int
        return toHexString((byte) (input & 0xff))+" "+toHexString((byte) ((input & 0xff00) >> 8));
    }

    public static String toHexString(byte b){
        String s = Integer.toHexString(b & 0xFF).toUpperCase();
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    public static boolean isEmpty(String str){
        if (str!=null&&str.length()>0 )
            return false;
            return true;
    }

    public static String replase(String str){
        if (isEmpty(str))
            return str;
        return str=str.replace("　","").replace("，","").replace(",","").replace(" ","");
    }

    public  static String getCRCStr(String str){
       return str+" "+getCRC(hexStringToBytes(str)).toUpperCase();
   }

    //为16进制字符串中间加空格
    public static String fomatStr16(String str16){
       if (isEmpty(str16))
           return str16;
       String str;
       int length = str16.length();
       int group = length/2;

       if(0==length % 2)
           str="";
       else
           return str16;

       for(int i=0,j=0;i<group;i++,j+=2)
           str+=str16.substring(j, j+2)+" ";

       return str.trim();
   }

   //判断返回的字符串是否完整
    public static boolean checkBack(String str){
        if (isEmpty(str))
            return false;
        String a="";
        String b="";
        str=replase(str);
        b=getCRC(hexStringToBytes(str.substring(0,str .length()-4))).toUpperCase();
        a=str.substring(str.length()-4, str .length());
       if ((a.toUpperCase()).equals(replase(b.toUpperCase())))
           return true;
        return false;
    }

    public static int nextInt() {
        Random rand = new Random();
        int tmp = Math.abs(rand.nextInt());
        return tmp % (99999999 - 10000000 + 1) + 10000000;
    }
}
