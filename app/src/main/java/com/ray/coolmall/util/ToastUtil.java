package com.ray.coolmall.util;

import android.content.Context;
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
}
