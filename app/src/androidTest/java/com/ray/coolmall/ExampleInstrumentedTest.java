package com.ray.coolmall;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ray.coolmall.serialport.FrameUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.ray.coolmall", appContext.getPackageName());
    }
    @Test
    public void  comOrder()throws Exception {
        String str="45 46 CB C3 00 02 24 00 01 00 00 00 06 00 00 00 EE 02 00 00 03 00 00 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 06";
//        if (str.indexOf(FrameOrder.comHead)==0)
//            str="";
       boolean b= FrameUtil.checkBack(str);
      System.out.println(b);
    }
    @Test
    public  void  test(){
        String[] strings4=new String[]{"A8", "16","00","00" };
        String[] strings2=new String[]{"A8", "16","00","00" };
        int i= FrameUtil.hiInt4String(strings2);
        int i2= FrameUtil.hiInt4String(strings4);
        System.out.println(i+" "+i2);
    }
}
