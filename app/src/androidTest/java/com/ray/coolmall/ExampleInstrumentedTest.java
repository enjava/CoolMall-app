package com.ray.coolmall;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ray.coolmall.serialport.FrameOrder;

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
      String str= FrameOrder.getWriteToStorage(0);
        System.out.println(str);
        Log.i("abcd",str);
    }
}
