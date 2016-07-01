package com.rjb.dianfeng.fileexchange.database;

import com.rjb.dianfeng.fileexchange.entity.MyFile;
import com.rjb.dianfeng.fileexchange.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FileRecordDB_dao {
	private SQLiteDatabase database;

	public FileRecordDB_dao(Context context) {
		FileRecordDB recordDB = new FileRecordDB(context);
		database = recordDB.getWritableDatabase();// 最好不要在主线程中调用
	}

	public MyFile[] queryAllReceivedFilel() {
		String[] columns = new String[] { "_path", "_size" };
		Cursor cursor = database.query("file_received_record", columns, null,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			int i = 0;// 下标
			MyFile[] files = new MyFile[cursor.getCount()];
			while (cursor.moveToNext()) {
				String path = cursor.getString(0);
				String size = cursor.getString(1);
				String name = Utils.getNameFromPath(path);
				files[i] = new MyFile(name, path, Integer.valueOf(size));
				i++;
			}
			return files;
		}
		return null;

	}

	public MyFile[] queryAllSentFilel() {
		String[] columns = new String[] { "_path", "_size" };
		Cursor cursor = database.query("file_sent_record", columns, null, null,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			int i = 0;// 下标
			MyFile[] files = new MyFile[cursor.getCount()];
			while (cursor.moveToNext()) {
				String path = cursor.getString(0);
				String size = cursor.getString(1);
				String name = Utils.getNameFromPath(path);
				files[i] = new MyFile(name, path, Integer.valueOf(size));
				i++;
			}
			return files;
		}
		return null;

	}

	// file received record frr
	public void insertToFRR(String path, String size) {
		ContentValues values = new ContentValues();
		values.put("_path", path);
		values.put("_size", size);
		database.insert("file_received_record", null, values);
	}

	// file SENT record fsr
	public void insertToFSR(String path, String size) {
		ContentValues values = new ContentValues();
		values.put("_path", path);
		values.put("_size", size);
		database.insert("file_sent_record", null, values);
	}

	public int clearFRRAll() {
		return database.delete("file_received_record", null, null);
	}

	public int clearFSRAll() {
		return database.delete("file_sent_record", null, null);
	}
}
