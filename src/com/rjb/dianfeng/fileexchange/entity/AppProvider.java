package com.rjb.dianfeng.fileexchange.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AppProvider {
	private static AppProvider appProvider = new AppProvider();
	private static Context mContext;
	private static Uri uri;
	private static ContentResolver contentResolver;
	private List<App> appList = new ArrayList<App>();
	
	public static final String URL_FILE = "content://media/external/file/";

	private AppProvider() {
	}

	public static AppProvider getInstance(Context context) {
		mContext = context;
		init();
		return appProvider;
	}

	private static void init() {
		contentResolver = mContext.getContentResolver();
		uri = Uri.parse("content://media/external/file");
	}
	
	public List<App> getAppList(){
		if (appList.size() > 0) {
			appList.removeAll(appList);
		}
		String[] projection = new String[] { "_data", "_size", "_display_name",
				"mime_type", "_id" };
		String selection = "mime_type=?";
		String[] selectionArgs = new String[] { "application/vnd.android.package-archive"};
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, null);
		String name;
		String path;
		int size;
		String mime_type;
		int id;
		App app;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(2);
				path = cursor.getString(0);
				size = cursor.getInt(1);
				mime_type = cursor.getString(3);
				id = cursor.getInt(4);
				app = new App(name, path, size, mime_type, id);
				appList.add(app);
			}
			cursor.close();
		}
		return appList;
	}
}
