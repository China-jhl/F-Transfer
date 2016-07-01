package com.rjb.dianfeng.fileexchange.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.rjb.dianfeng.fileexchange.MainActivity;

public class IndexPoint extends ImageView {
	private boolean isFirstDraw = true;

	public IndexPoint(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		if (isFirstDraw) {
			setX(MainActivity.width / 6);
		}
		super.onDraw(canvas);
	}

}
