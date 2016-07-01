package com.rjb.dianfeng.fileexchange.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.rjb.dianfeng.fileexchange.adapter.ClassificationAdapter;
import com.rjb.dianfeng.fileexchange.entity.AppProvider;
import com.rjb.dianfeng.fileexchange.entity.AudioProvider;
import com.rjb.dianfeng.fileexchange.entity.Doc_etc_Provider;
import com.rjb.dianfeng.fileexchange.entity.PictureProvider;
import com.rjb.dianfeng.fileexchange.entity.VideoProvider;
import com.rjb.dianfeng.fileexchange.entity.ZipProvider;

/**
 * 获取各个分类文件的文件数
 * 
 * @author 龙
 * 
 */
public class FileNumAsyncTask extends AsyncTask<Integer, Void, String> {
	private Context mContext;
	private TextView fileNum;

	public FileNumAsyncTask(Context mContext, TextView fileNum) {
		this.mContext = mContext;
		this.fileNum = fileNum;
	}

	@Override
	protected String doInBackground(Integer... params) {
		int num = 0;
		switch (params[0]) {
		case ClassificationAdapter.FILE_APP:
			num = AppProvider.getInstance(mContext).getAppList().size();
			break;
		case ClassificationAdapter.FILE_MUSIC:
			num = AudioProvider.getInstance(mContext).getAudioList().size();
			break;
		case ClassificationAdapter.FILE_PIC:
			num = PictureProvider.getInstance(mContext).getPictureList().size();
			break;
		case ClassificationAdapter.FILE_TXT:
			num = Doc_etc_Provider.getInstance(mContext).getDoc_etcList()
					.size();
			break;
		case ClassificationAdapter.FILE_VIDEO:
			num = VideoProvider.getInstance(mContext).getVideoList().size();
			break;
		case ClassificationAdapter.FILE_ZIPS:
			num = ZipProvider.getInstance(mContext).getList().size();
			break;
		}
		return String.valueOf(num);
	}

	@Override
	protected void onPostExecute(String result) {
		fileNum.setText("(" + result + ")");
		super.onPostExecute(result);
	}
}
