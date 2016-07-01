package com.rjb.dianfeng.fileexchange.utils;

import android.util.Log;

/**
 * compute transmission speed
 * 
 * @author 龙
 * 
 */
public class TransmissionSpeed {
	private static long startTime;// 传输开始时间
	private static long lastTime;
	private static int lastSize;

	public static void init() {// 每次使用都要调用此方法
		startTime = System.currentTimeMillis();
		lastTime = startTime;
		Log.i("SPEED", "TransmissionSpeed init");
	}

	public static void destroy() {// 每次使用完后调用
		startTime = 0;
		lastSize = 0;
		lastTime = 0l;
		Log.i("SPEED", "TransmissionSpeed destroy");
	}

	public static double getSpeed(int currentSize) {// 获取传输速率 以字符串形式返回
		long currentTime = System.currentTimeMillis();
		double dTime = (currentTime - lastTime) / 1000.0f;
		int dSize = (currentSize - lastSize);
		Log.i("SPEED", "currentTime" + currentTime + "    lastTime" + lastTime);
		Log.i("SPEED", "dTime:" + dTime + "   dSize:" + dSize);
		double speed = 0;
		if (dTime == 0) {
			dTime = 0.001d;
		}
		speed = dSize / dTime;
		lastTime = currentTime;
		lastSize = currentSize;

		// double result = (speed / 1024 / 1024) / (currentTime - lastTime);
		Log.i("SPEED", (speed / 1024 / 1024) + "");
		return speed / 1024 / 1024;
	}
}
