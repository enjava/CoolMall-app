package com.ray.coolmall.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

	/**
	 * 显示吐司
	 * 
	 * @param context
	 *            上下文对象
	 * @param text
	 *            显示的内容
	 */
	public static void show(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	//toast = Toast.makeText(getApplicationContext(),
	/*"自定义位置Toast", Toast.LENGTH_LONG);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();*/

	public static void showTop(Context context, String text,int xOffset, int yOffset) {
		Toast	toast=Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP,xOffset,yOffset);
		toast.show();
				//.show();
	}
}
