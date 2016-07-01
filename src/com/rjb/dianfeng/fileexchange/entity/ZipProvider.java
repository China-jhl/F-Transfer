package com.rjb.dianfeng.fileexchange.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ZipProvider {
	private static ZipProvider provider = new ZipProvider();
	private static Context mContext;
	private static Uri uri;
	private static ContentResolver contentResolver;
	private List<Zip> zipList = new ArrayList<Zip>();
	
	public static final String URL_FILE = "content://media/external/file/";

	private ZipProvider() {

	}

	public static ZipProvider getInstance(Context context) {
		mContext = context;
		init();
		return provider;
	}

	private static void init() {
		contentResolver = mContext.getContentResolver();
		uri = Uri.parse("content://media/external/file");
	}

	public List<Zip> getList() {
		if (zipList.size() > 0) {
			zipList.removeAll(zipList);
		}
		String[] projection = new String[] { "_data", "_size", "_display_name",
				"mime_type", "_id" };
		String selection = "mime_type=? or mime_type = ?";
		String[] selectionArgs = new String[] { "application/zip","application/x-rar-compressed"};
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, null);
		String name;
		String path;
		int size;
		String mime_type;
		int id;
		Zip zip;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(2);
				path = cursor.getString(0);
				size = cursor.getInt(1);
				mime_type = cursor.getString(3);
				id = cursor.getInt(4);
				zip = new Zip(name, path, size, mime_type, id);
				zipList.add(zip);
			}
			cursor.close();
		}
		return zipList;
	}
}
