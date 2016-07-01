package com.rjb.dianfeng.fileexchange.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 照片资源信息提供者
 * 
 * @author 龙
 * 
 */
public class PictureProvider {

	private static PictureProvider pictureProvider = new PictureProvider();// 单例模式
	private static Context mContext;
	private static ContentResolver contentResolver;
	private static Uri uri;
	private List<Picture> pictureList = new ArrayList<Picture>();

	public static final String URL_FILE = "content://media/external/images/media/";

	private PictureProvider() {

	}

	public static PictureProvider getInstance(Context context) {
		mContext = context;
		init();
		return pictureProvider;
	}

	/**
	 * 初始化一些值
	 * 
	 * @param mContext2
	 */
	private static void init() {
		contentResolver = mContext.getContentResolver();
		uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}

	public List<Picture> getPictureList() {
		if (pictureList.size() > 0) {
			pictureList.removeAll(pictureList);
		}
		String[] projection = new String[] { "_data", "_size", "_display_name",
				"mime_type", "_id" };
		Cursor cursor = contentResolver
				.query(uri, projection, null, null, null);
		String name;
		String path;
		int size;
		Picture picture;
		String mime_type;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(2);
				path = cursor.getString(0);
				size = cursor.getInt(1);
				int id = cursor.getInt(4);
				mime_type = cursor.getString(3);
				picture = new Picture(name, path, size, mime_type, id);
				pictureList.add(picture);
			}
			cursor.close();
		}
		return pictureList;

	}

}
