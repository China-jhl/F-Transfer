package com.rjb.dianfeng.fileexchange.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.entity.BaseMedia;
import com.rjb.dianfeng.fileexchange.utils.BitmapWorkerTask;
import com.rjb.dianfeng.fileexchange.utils.ImageCache;
import com.rjb.dianfeng.fileexchange.utils.Utils;

/**
 * 将文件按类型分类时的fileAdapter
 * 
 * @author 龙
 * 
 */
public class FileAdapter extends BaseAdapter {
	private List<? extends BaseMedia> mediaList;
	private Context mContext;
	private int cellName;

	private boolean isOpretated = false;// 是不是对文件进行操作

	private List<BaseMedia> selectedPosition = new ArrayList<BaseMedia>();// 存放选中的item的位置
																	// 防止view复用时图标的不正确显示.
																	// 完成文件操作后
																	// 清零

	public FileAdapter(List<? extends BaseMedia> mediaList, Context mContext,
			int cellNum) {
		this.mediaList = mediaList;
		this.mContext = mContext;
		this.cellName = cellNum;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mediaList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = View.inflate(mContext, R.layout.item_files_listview, null);
			holder = new ViewHolder();
			holder.file_icon = (ImageView) view.findViewById(R.id.iv_file_icon);
			holder.file_name = (TextView) view.findViewById(R.id.tv_file_name);
			holder.file_size = (TextView) view.findViewById(R.id.tv_file_size);
			holder.chose = (ImageView) view.findViewById(R.id.iv_chose);
			view.setTag(holder);
		}
		if (isOpretated) {
			holder.chose.setVisibility(View.VISIBLE);
			if (!selectedPosition.contains(mediaList.get(position))) {
				holder.chose.setImageResource(R.drawable.unckecked);
			}else{
				holder.chose.setImageResource(R.drawable.checked);
			}
		} else {
			holder.chose.setVisibility(View.GONE);
		}
		BaseMedia media = mediaList.get(position);
		String s = Utils.getNameFromPath(media.getPath());
		holder.file_name.setText(s);
		String size = Utils.getSizeKB(media.getSize());
		holder.file_size.setText(size);
		setFileIcon(holder.file_icon, s, media);
		// 这儿的代码会一大堆。。。。。。
		return view;
	}

	private void setFileIcon(ImageView file_icon, String name, BaseMedia media) {
		switch (cellName) {
		case ClassificationAdapter.FILE_MUSIC:
			file_icon.setImageResource(R.drawable.music_icon);
			break;
		case ClassificationAdapter.FILE_PIC:
			handlePicIco(file_icon, media);
			break;
		case ClassificationAdapter.FILE_TXT:
			handleTxtIcon(file_icon, name);
			break;
		case ClassificationAdapter.FILE_VIDEO:
			file_icon.setImageResource(R.drawable.video_icon);
			break;
		case ClassificationAdapter.FILE_ZIPS:
			file_icon.setImageResource(R.drawable.zip_icon);
			break;
		case ClassificationAdapter.FILE_APP:
			file_icon.setImageResource(R.drawable.apk_icon);
			break;
		}
	}

	private void handlePicIco(ImageView file_icon, BaseMedia media) {
		Bitmap bm = ImageCache.getInstance().get(media.getPath());
		if (bm != null) {
			file_icon.setImageBitmap(bm);
		} else {
			new BitmapWorkerTask(file_icon, mContext).loadBitmap(media
					.getPath());
		}
	}

	private void handleTxtIcon(ImageView file_icon, String name) {
		String extension = Utils.getExtensionFromPath(name);
		if (extension.equals("doc")) {
			file_icon.setImageResource(R.drawable.word_icon);
		} else if (extension.equals("xls")) {
			file_icon.setImageResource(R.drawable.excel_icon);
		} else if (extension.equals("ppt")) {
			file_icon.setImageResource(R.drawable.ppt_icon);
		} else if (extension.equals("txt")) {
			file_icon.setImageResource(R.drawable.txt_icon);
		} else {
			file_icon.setImageResource(R.drawable.txt_icon);
		}
	}

	public class ViewHolder {
		public ImageView file_icon;
		public TextView file_name;
		public TextView file_size;
		public ImageView chose;// 是否选中的图标
	}

	public void setOpretated(boolean isOpretated) {
		this.isOpretated = isOpretated;
	}

	public boolean isOpretated() {
		return isOpretated;
	}

	public void add(BaseMedia media) {
		selectedPosition.add(media);
	}

	public void remove(BaseMedia media) {
		selectedPosition.remove(media);
	}

	public void clear() {
		selectedPosition.clear();
	}

}
