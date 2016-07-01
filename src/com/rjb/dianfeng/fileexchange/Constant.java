package com.rjb.dianfeng.fileexchange;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

public class Constant {
	public static final int FILE_RENAME = 6;
	public static final int FILE_DELETE = 7;
	public static final int FILE_COPY = 8;
	public static final int FILE_INFO = 9;
	public static final int FILE_SEND = 13;

	public static final int ClASSIFICATION_FILE_ACTIVITY = 10;
	public static final int FOLDER_FRAGMENT = 11;

	public static final int START_MAINACTIVITY_FROM_CLASSIFICATIONFILEACTIVITY = 12;

	// 没办法了 只能这样存储activity了
	// 在activity创建啊的时候 add 在destroy的时候remove
	public static Map<String, Activity> activities = new HashMap<String, Activity>();

	public static final int PORT = 8988;
	public static final String DEFAULT_HOST = "192.168.49.1";
	public static final String TAG = "TAG_INFO";

	// 和电脑传输文件
	public static final int COMPUTER_PORT = 51706;
	public static final String COMPUTER_ADDRESS = "192.168.0.1";
	
	//在线发送文件
	public static final String url_upload = "http://192.168.1.112:8080/wifip2p/upload.php";
}
