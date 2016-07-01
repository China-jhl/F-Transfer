package com.rjb.dianfeng.fileexchange.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.entity.MyFile;
import com.rjb.dianfeng.fileexchange.utils.BitmapWorkerTask;
import com.rjb.dianfeng.fileexchange.utils.ImageCache;
import com.rjb.dianfeng.fileexchange.utils.Utils;

public class FileRecordAdapter extends BaseAdapter {
	private Context mContext;
	private MyFile[] files;

	public FileRecordAdapter(Context mContext, MyFile[] files) {
		this.mContext = mContext;
		this.files = files;
	}

	@Override
	public int getCount() {
		if (files != null) {
			return files.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
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
			view = View.inflate(mContext, R.layout.item_files_record_listview,
					null);
			holder = new ViewHolder();
			holder.fileIcon = (ImageView) view.findViewById(R.id.iv_file_icon);
			holder.fileName = (TextView) view.findViewById(R.id.tv_file_name);
			holder.fileSize = (TextView) view.findViewById(R.id.tv_file_size);
			view.setTag(holder);
		}
		holder.fileName.setText(files[position].getName());
		String size = Utils.getSizeKB(files[position].getSize());
		holder.fileSize.setText(size);
		setFileIcon(files[position], holder.fileIcon);
		return view;
	}

	private void setFileIcon(MyFile file, ImageView file_icon) {
		String extension = Utils.getExtensionFromPath(file.getPath());
		if (extension.equals("doc")) {
			file_icon.setImageResource(R.drawable.word_icon);
		} else if (extension.equals("xls")) {
			file_icon.setImageResource(R.drawable.excel_icon);
		} else if (extension.equals("ppt")) {
			file_icon.setImageResource(R.drawable.ppt_icon);
		} else if (extension.equals("txt")) {
			file_icon.setImageResource(R.drawable.txt_icon);
		} else if (extension.equals("mp3") || extension.equals("amr")) {
			file_icon.setImageResource(R.drawable.music_icon);
		} else if (extension.equals("apk")) {
			file_icon.setImageResource(R.drawable.apk_icon);
		} else if (extension.equals("jpg") || extension.equals("jpeg")
				|| extension.equals("png") || extension.equals("gif")) {
			Bitmap bm = ImageCache.getInstance().get(file.getPath());
			if (bm != null) {
				file_icon.setImageBitmap(bm);
			} else {
				new BitmapWorkerTask(file_icon, mContext).loadBitmap(file
						.getPath());
			}
		} else if (extension.equals("zip") || extension.equals("rar")) {
			file_icon.setImageResource(R.drawable.zip_icon);
		} else {
			file_icon.setImageResource(R.drawable.no_type);
		}
	}

	class ViewHolder {
		ImageView fileIcon;
		TextView fileName;
		TextView fileSize;
	}

	public void setFiles(MyFile[] files) {
		this.files = files;
	}
	
	

}
