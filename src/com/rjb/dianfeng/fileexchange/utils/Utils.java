package com.rjb.dianfeng.fileexchange.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.rjb.dianfeng.fileexchange.entity.StorageInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.storage.StorageManager;

public class Utils {
	public static String getNameFromPath(String s) {
		int index = s.lastIndexOf("/");
		String name = s.substring(index + 1);
		return name;
	}

	public static String newPath(String s, String newName) {
		int index = s.lastIndexOf("/");
		String newPath = s.substring(0, index) + newName;
		return newPath;
	}

	/**
	 * 获取后缀名
	 * 
	 * @param s
	 * @return
	 */
	public static String getExtensionFromPath(String s) {
		int index = s.lastIndexOf(".");
		String name = s.substring(index + 1);
		return name;
	}

	/**
	 * 获取前一个path
	 * 
	 * @param s
	 * @return
	 */
	public static String getPrePath(String s) {
		int index = s.lastIndexOf("/");
		String name = s.substring(0, index);
		return name;
	}

	public static int separatorNum(String s){
		char[] string = s.toCharArray();
		int num = 0;
		for (int i = 0; i < string.length; i++) {
			if(string[i] == '/'){
				num++;
			}
		}
		return num;
	}
	/***
	 * 大小格式化
	 * @param size
	 * @return
	 */
	public static String getSizeKB(int size) {
		DecimalFormat format = new DecimalFormat("#0.00");
		double size1 = size / 1024.0f;
		if(size1>1024.0){
			size1 = size1/1024.0f;
			return format.format(size1) + "  Mb";
		}
		return format.format(size1) + "  kb";
	}
	

	/**
	 * 反馈 得到android设备的所有挂载
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static List<StorageInfo> listAvaliableStorage(Context context) {
		ArrayList<StorageInfo> storagges = new ArrayList<StorageInfo>();
		StorageManager storageManager = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		try {
			Class<?>[] paramClasses = {};
			Method getVolumeList = StorageManager.class.getMethod(
					"getVolumeList", paramClasses);
			getVolumeList.setAccessible(true);
			Object[] params = {};
			Object[] invokes = (Object[]) getVolumeList.invoke(storageManager,
					params);
			if (invokes != null) {
				StorageInfo info = null;
				for (int i = 0; i < invokes.length; i++) {
					Object obj = invokes[i];
					Method getPath = obj.getClass().getMethod("getPath",
							new Class[0]);
					String path = (String) getPath.invoke(obj, new Object[0]);
					info = new StorageInfo(path);
					File file = new File(info.path);
					if ((file.exists()) && (file.isDirectory())
							&& (file.canWrite())) {
						Method isRemovable = obj.getClass().getMethod(
								"isRemovable", new Class[0]);
						String state = null;
						try {
							Method getVolumeState = StorageManager.class
									.getMethod("getVolumeState", String.class);
							state = (String) getVolumeState.invoke(
									storageManager, info.path);
							info.state = state;
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (info.isMounted()) {
							info.isRemoveable = ((Boolean) isRemovable.invoke(
									obj, new Object[0])).booleanValue();
							storagges.add(info);
						}
					}
				}
			}
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		storagges.trimToSize();

		return storagges;
	}
}
