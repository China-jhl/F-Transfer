package com.rjb.dianfeng.fileexchange.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.rjb.dianfeng.fileexchange.MainActivity;

public class CleanView extends ImageView {

	private boolean isFirst = true;

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	public CleanView(Context context) {
		super(context);
		if (isFirst) {
			createWM(context);
			setListener();
			isFirst = false;
		}
	}

	private void createWM(Context context) {
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		layoutParams = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0,
				PixelFormat.TRANSPARENT);

		layoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
		layoutParams.type = 99;
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		layoutParams.x = MainActivity.width / 3 * 2;
		;
		layoutParams.y = MainActivity.height / 2;
		setImageResource(com.rjb.dianfeng.fileexchange.R.drawable.clean);// …Ë÷√Õº±Í
		windowManager.addView(this, layoutParams);// ÃÌº”view
	}

	private void setListener() {
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int rawX = (int) event.getRawX();
				int rawY = (int) event.getRawY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					layoutParams.x = rawX;
					layoutParams.y = rawY;
					windowManager
							.updateViewLayout(CleanView.this, layoutParams);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	public void removeView() {
		if (windowManager != null) {
			windowManager.removeView(this);
			windowManager = null;
		}
	}
}
