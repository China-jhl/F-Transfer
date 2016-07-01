package com.rjb.dianfeng.fileexchange.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class FileOperation {
	/**
	 * 用这个重命名
	 * 
	 * @param context
	 * @param file
	 * @param newName
	 * @param uri
	 * @param id
	 * @return
	 */
	public static int reName(Context context, File file, String newName,
			Uri uri, String id) {
		String newPath = file.getParentFile().getPath() + "/" + newName;
		File newFile = new File(newPath);
		if (newFile.exists()) {
			Toast.makeText(context, "文件名已存在", Toast.LENGTH_SHORT).show();
			return 0;
		}

		file.renameTo(newFile);
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("_display_name", newName);
		values.put("_data", newPath);
		String where = "_id=?";
		String[] selectionArgs = new String[] { id };

		return contentResolver.update(uri, values, where, selectionArgs);
	}
	
	public static boolean reName(Context context,File file,String newName){
		String newPath = file.getParentFile().getPath() + "/" + newName;
		File newFile = new File(newPath);
		if (newFile.exists()) {
			Toast.makeText(context, "文件名已存在", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return file.renameTo(newFile);
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return
	 */
	public static int delete(Context context, File file, Uri uri, String id) {
		file.delete();
		ContentResolver contentResolver = context.getContentResolver();
		String where = "_id=?";
		String[] selectionArgs = new String[] { id };

		return contentResolver.delete(uri, where, selectionArgs);
	}

	/**
	 * 重载文件删除函数
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delete(File file) {
		return file.delete();
	}

	/**
	 * 
	 * @param file
	 * @param path 当前目录的path
	 * @return
	 */
	public static boolean copy(File file, String path) {
		File newFile = new File(path + "/" + file.getName());
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			if (newFile.createNewFile()) {
				inputStream = new FileInputStream(file);
				outputStream = new FileOutputStream(newFile);

				byte[] buffer = new byte[1024];
				while ((inputStream.read(buffer)) != -1) {
					outputStream.write(buffer);
				}
				outputStream.flush();
				inputStream.close();
				outputStream.close();
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}
}
