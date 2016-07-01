package com.rjb.dianfeng.fileexchange.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FileRecordDB extends SQLiteOpenHelper {

	public FileRecordDB(Context context) {
		super(context, "file_record.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_create = "create table file_sent_record(_id integer primary key autoincrement,_path varchar(40),_size varchar(20))";
		String sql_create2 = "create table file_received_record(_id integer primary key autoincrement,_path varchar(40),_size varchar(20))";
		db.execSQL(sql_create);
		db.execSQL(sql_create2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
