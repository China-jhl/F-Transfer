package com.rjb.dianfeng.fileexchange.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class VideoProvider {
	private static VideoProvider videoProvider = new VideoProvider();
	private static Context mContext;
	private static ContentResolver contentResolver;
	private static Uri uri;
	private List<Video> videoList = new ArrayList<Video>();
	public static final String URL_FILE = "content://media/external/video/media/";

	private VideoProvider() {

	}

	public static VideoProvider getInstance(Context context) {
		mContext = context;
		init();
		return videoProvider;
	}

	private static void init() {
		contentResolver = mContext.getContentResolver();
		uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	}

	public List<Video> getVideoList() {
		if (videoList.size() > 0) {
			videoList.removeAll(videoList);
		}
		String[] projection = new String[] { "_data", "_size", "_display_name",
				"mime_type", "_id" };
		Cursor cursor = contentResolver
				.query(uri, projection, null, null, null);
		String name;
		String path;
		int size;
		String mime_type;
		Video video;
		int id;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(2);
				path = cursor.getString(0);
				size = cursor.getInt(1);
				mime_type = cursor.getString(3);
				id = cursor.getInt(4);
				video = new Video(name, size, path, mime_type, id);
				videoList.add(video);
			}
			cursor.close();
		}
		return videoList;
	}
}
