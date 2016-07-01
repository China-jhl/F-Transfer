package com.rjb.dianfeng.fileexchange.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class AudioProvider {
	private static AudioProvider audioProvider = new AudioProvider();
	private static Context mContext;
	private static ContentResolver contentResolver;
	private static Uri uri;
	private List<Audio> audioList = new ArrayList<Audio>();

	public static final String URL_FILE = "content://media/external/audio/media/";

	private AudioProvider() {

	}

	public static AudioProvider getInstance(Context context) {
		mContext = context;
		init();
		return audioProvider;
	}

	private static void init() {
		contentResolver = mContext.getContentResolver();
		uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	}

	public List<Audio> getAudioList() {
		if (audioList.size() > 0) {
			audioList.removeAll(audioList);
		}
		String[] projection = new String[] { "_data", "_size", "_display_name",
				"mime_type", "_id" };
		Cursor cursor = contentResolver
				.query(uri, projection, null, null, null);
		String name;
		String path;
		int size;
		String mime_type;
		int id;
		Audio audio;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(2);
				path = cursor.getString(0);
				size = cursor.getInt(1);
				mime_type = cursor.getString(3);
				id = cursor.getInt(4);
				audio = new Audio(name, path, size, mime_type, id);
				audioList.add(audio);
			}
			cursor.close();
		}
		return audioList;

	}
}
