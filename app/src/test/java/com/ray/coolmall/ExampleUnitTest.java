package com.ray.coolmall;

import com.ray.coolmall.serialport.FrameUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public  void  test(){
        String[] strings=new String[]{"A8", "16","00","00" };
       int i= FrameUtil.hiInt4String(strings);
        System.out.println(FrameUtil.hiInt4String(strings));
    }
}