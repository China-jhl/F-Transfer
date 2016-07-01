package com.rjb.dianfeng.fileexchange.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Doc PDF txt 等文本文件
 * 
 * @author 龙
 * 
 */
public class Doc_etc_Provider {
	private static Doc_etc_Provider doc_etc_Provider = new Doc_etc_Provider();
	private static Context mContext;
	private static ContentResolver contentResolver;
	private static Uri uri;
	private List<Doc_etc> doc_etcList = new ArrayList<Doc_etc>();

	public static final String URL_FILE = "content://media/external/file/";

	private Doc_etc_Provider() {

	}

	public static Doc_etc_Provider getInstance(Context context) {
		mContext = context;
		init();
		return doc_etc_Provider;
	}

	private static void init() {
		contentResolver = mContext.getContentResolver();
		uri = Uri.parse("content://media/external/file");
	}

	// "text/plain", "application/vnd.ms-excel"
	public List<Doc_etc> getDoc_etcList() {
		if (doc_etcList.size() > 0) {
			doc_etcList.removeAll(doc_etcList);
		}
		String[] projection = new String[] { "_data", "_size", "_display_name",
				"mime_type", "_id" };
		String selection = "mime_type=? or mime_type=? or mime_type=? or mime_type=?";//) GROUP BY (_data
		String[] selectionArgs = new String[] { "application/msword",
				"text/plain", "application/vnd.ms-excel",
				"application/vnd.ms-powerpoint" };
		String sortOrder = "_display_name desc";
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		String name;
		String path;
		int size;
		String mime_type;
		int id;
		Doc_etc doc_etc;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(2);
				path = cursor.getString(0);
				size = cursor.getInt(1);
				mime_type = cursor.getString(3);
				id = cursor.getInt(4);
				doc_etc = new Doc_etc(name, path, size, mime_type, id);
				doc_etcList.add(doc_etc);
			}
			cursor.close();
		}
		return doc_etcList;

	}

}
