package com.rjb.dianfeng.fileexchange.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.utils.FileNumAsyncTask;

public class ClassificationAdapter extends BaseAdapter{
	/**
	 * 
	 */
	public static final int FILE_TXT = 0;
	public static final int FILE_PIC = 1;
	public static final int FILE_VIDEO = 2;
	public static final int FILE_MUSIC = 3;
	public static final int FILE_APP = 4;
	public static final int FILE_ZIPS = 5;
	
	private Context mContext;
	private int CELL_NUM = 6;
	private static int[] drawables = new int[] { R.drawable.file,
			R.drawable.picture, R.drawable.movie, R.drawable.music,
			R.drawable.apk, R.drawable.zips };

	private static int[] cellNames = new int[] { R.string.file, R.string.pic,
			R.string.movie, R.string.music, R.string.apk, R.string.zips };

	public ClassificationAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return CELL_NUM;
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
		// 就6个cell 不做优化了
		View view = View.inflate(mContext, R.layout.cell_classification, null);
		ImageView icon = (ImageView) view.findViewById(R.id.iv_cell_icon);
		icon.setImageResource(drawables[position]);

		TextView cellName = (TextView) view.findViewById(R.id.tv_cell_name);
		cellName.setText(cellNames[position], null);
		
		//这有限个item  不会出现乱序问题  这儿就不处理乱序问题了
		TextView fileNum = (TextView) view.findViewById(R.id.tv_file_num);
		new FileNumAsyncTask(mContext, fileNum).execute(position);
		return view;
	}
}
