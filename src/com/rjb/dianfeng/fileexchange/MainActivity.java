package com.rjb.dianfeng.fileexchange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.adapter.ExchangePagerAdapter;
import com.rjb.dianfeng.fileexchange.fragment.FileExchangeFragment;
import com.rjb.dianfeng.fileexchange.fragment.FolderFragment;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private ViewPager exchangePager;
	private ImageView leftArrow;
	private View markLine;
	private TextView tab1;
	private TextView tab2;
	private TextView tab3;

	public static WifiP2pManager mManager;
	public static Channel mChannel;
	// private boolean isWifiP2pEnabled = false;

	private ExchangePagerAdapter exchangePagerAdapter;

	private boolean isCleanViewExist = false;// 判断窗体是否已存在

	public ExchangePagerAdapter getExchangePagerAdapter() {
		return exchangePagerAdapter;
	}

	private boolean isFolderPage = false;// 当前是否为文件夹的fragment
	private boolean isFileExchange = true;// 当前是否为传输文件的 fragment
	public static int width;
	public static int height;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取屏幕分辨率
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();
		initWiFi_P2P();// 使用wifi-p2p framework前的一些初始化操作
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		setListener();
	}

	private void initWiFi_P2P() {
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			int flag = intent.getIntExtra("flag_activity", 0);
			switch (flag) {
			case Constant.START_MAINACTIVITY_FROM_CLASSIFICATIONFILEACTIVITY:
				String[] file_path = intent.getStringArrayExtra("file_path");
				copyFile_step1(file_path);
				Toast.makeText(getApplicationContext(), "选择目标文件夹",
						Toast.LENGTH_SHORT).show();
				break;

			}
		}
		super.onNewIntent(intent);
	}

	private void initData() {
		exchangePagerAdapter = new ExchangePagerAdapter(
				getSupportFragmentManager());

	}

	private void setListener() {
		exchangePager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						setFolderPage(position);
						setFileExchange(position);// 没什么用
						setTextColor(position);
						super.onPageSelected(position);
					}

					/**
					 * @position Position index of the first page currently
					 *           being displayed. Page position+1 will be
					 *           visible if positionOffset is nonzero.
					 * 
					 * @positionOffset Value from [0, 1) indicating the offset
					 *                 from the page at position.
					 * 
					 * @positionOffsetPixels Value in pixels indicating the
					 *                       offset from position.
					 * 
					 *                       框架已经为position做了特殊处理了 eg.position =
					 *                       0,然后屏幕向左滑，
					 *                       直到切到下一页（在向左滑一点，使positionOffset不为0
					 *                       ），position
					 *                       变为1，之间position一直为0；但是，当position =
					 *                       1时， 你向右滑动屏幕，position 立马变为0；
					 */
					@SuppressLint("NewApi")
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						super.onPageScrolled(position, positionOffset,
								positionOffsetPixels);
						// Log.e("FILE", positionOffsetPixels + "");
						// markLine.scrollTo(positionOffsetPixels,
						// markLine.getScrollY());
						// markLine.setLeft(positionOffsetPixels);
						// markLine.setScrollX(positionOffsetPixels);
						if (positionOffsetPixels != 0) {
							float x = positionOffsetPixels / 3 + width / 3
									* position + width / 6;
							markLine.setX(x);
							// Log.e("FILE", position+"");
						}
					}

				});
	}

	private void setFileExchange(int position) {
		FileExchangeFragment exchangeFragment = (FileExchangeFragment) exchangePagerAdapter
				.getMapFragment().get("fileExchange");
		if (position == 0) {
			isFileExchange = true;
			if (exchangeFragment != null) {
				exchangeFragment.cleanImage();// 创建clean窗体
			}
		} else {
			isFileExchange = false;
			if (exchangeFragment != null) {
				exchangeFragment.removeCleanView();// 移除clean窗体
				isCleanViewExist = false;
			}
		}
	}

	private void setFolderPage(int position) {
		if (position == 2) {
			isFolderPage = true;
		} else {
			isFolderPage = false;
		}
	}

	@SuppressLint("ResourceAsColor")
	private void setTextColor(int position) {
		tab1.setTextColor(0xffffffff);
		tab2.setTextColor(0xffffffff);
		tab3.setTextColor(0xffffffff);
		switch (position) {
		case 0:
			tab1.setTextColor(0xff07fb29);
			break;
		case 1:
			tab2.setTextColor(0xff07fb29);
			break;
		case 2:
			tab3.setTextColor(0xff07fb29);
			break;

		}
	}

	private void initView() {
		exchangePager = (ViewPager) findViewById(R.id.exchange_pager);
		exchangePager.setAdapter(exchangePagerAdapter);
		leftArrow = (ImageView) findViewById(R.id.iv_left);
		leftArrow.setVisibility(View.GONE);
		markLine = findViewById(R.id.mark_line);
		tab1 = (TextView) findViewById(R.id.tv_tab1);
		tab2 = (TextView) findViewById(R.id.tv_tab2);
		tab3 = (TextView) findViewById(R.id.tv_tab3);
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFolderPage) {
				if (exchangePagerAdapter != null
						&& !exchangePagerAdapter.getMapFragment().isEmpty()) {
					FolderFragment folderFragment = (FolderFragment) exchangePagerAdapter
							.getMapFragment().get("folder");// 得到某个fragment的方法
															// （能想出这样一个方法，还行）
					if (folderFragment.onPressBack()) {
						return true;
					}
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void copyFile_step1(String[] file_path) {
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < file_path.length; i++) {
			files.add(new File(file_path[i]));
		}

		FolderFragment fragment = (FolderFragment) exchangePagerAdapter
				.getMapFragment().get("folder");
		fragment.setMedia_chose(files);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_tab1:
			exchangePager.setCurrentItem(0);
			// setTextColor(1);
			break;
		case R.id.tv_tab2:
			exchangePager.setCurrentItem(1);
			break;
		case R.id.tv_tab3:
			exchangePager.setCurrentItem(2);
			break;

		}
	}

	public boolean isFileExchange() {
		return isFileExchange;
	}

	public boolean isCleanViewExist() {
		return isCleanViewExist;
	}

	public void setCleanViewExist(boolean isCleanViewExist) {
		this.isCleanViewExist = isCleanViewExist;
	}

}
