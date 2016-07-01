package com.rjb.dianfeng.fileexchange.utils;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.rjb.dianfeng.fileexchange.adapter.ClassificationAdapter;
import com.rjb.dianfeng.fileexchange.entity.AppProvider;
import com.rjb.dianfeng.fileexchange.entity.AudioProvider;
import com.rjb.dianfeng.fileexchange.entity.BaseMedia;
import com.rjb.dianfeng.fileexchange.entity.Doc_etc_Provider;
import com.rjb.dianfeng.fileexchange.entity.PictureProvider;
import com.rjb.dianfeng.fileexchange.entity.VideoProvider;
import com.rjb.dianfeng.fileexchange.entity.ZipProvider;
import com.rjb.dianfeng.fileexchange.interfaces.IGetMediaList;

public class MediaListAsyncTask extends
		AsyncTask<Integer, Void, List<? extends BaseMedia>> {
	private Context mContext;
	private IGetMediaList getMediaList;

	public MediaListAsyncTask(Context mContext, IGetMediaList getMediaList) {
		this.mContext = mContext;
		this.getMediaList = getMediaList;
	}

	@Override
	protected List<? extends BaseMedia> doInBackground(Integer... params) {
		List<? extends BaseMedia> mediaList = null;
		switch (params[0]) {
		case ClassificationAdapter.FILE_APP:
			mediaList = AppProvider.getInstance(mContext).getAppList();
			break;
		case ClassificationAdapter.FILE_MUSIC:
			mediaList = AudioProvider.getInstance(mContext).getAudioList();
			break;
		case ClassificationAdapter.FILE_PIC:
			mediaList = PictureProvider.getInstance(mContext).getPictureList();
			break;
		case ClassificationAdapter.FILE_TXT:
			mediaList = Doc_etc_Provider.getInstance(mContext).getDoc_etcList();
			break;
		case ClassificationAdapter.FILE_VIDEO:
			mediaList = VideoProvider.getInstance(mContext).getVideoList();
			break;
		case ClassificationAdapter.FILE_ZIPS:
			mediaList = ZipProvider.getInstance(mContext).getList();
			break;
		}
		return mediaList;
	}

	@Override
	protected void onPostExecute(List<? extends BaseMedia> result) {
		getMediaList.setMediaList(result);
		super.onPostExecute(result);
	}
}
