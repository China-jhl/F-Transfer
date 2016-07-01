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

	private boolean isCleanViewExist = false;// �жϴ����Ƿ��Ѵ���

	public ExchangePagerAdapter getExchangePagerAdapter() {
		return exchangePagerAdapter;
	}

	private boolean isFolderPage = false;// ��ǰ�Ƿ�Ϊ�ļ��е�fragment
	private boolean isFileExchange = true;// ��ǰ�Ƿ�Ϊ�����ļ��� fragment
	public static int width;
	public static int height;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ȡ��Ļ�ֱ���
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();
		initWiFi_P2P();// ʹ��wifi-p2p frameworkǰ��һЩ��ʼ������
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
				Toast.makeText(getApplicationContext(), "ѡ��Ŀ���ļ���",
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
						setFileExchange(position);// ûʲô��
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
					 *                       ����Ѿ�Ϊposition�������⴦���� eg.position =
					 *                       0,Ȼ����Ļ���󻬣�
					 *                       ֱ���е���һҳ��������һ�㣬ʹpositionOffset��Ϊ0
					 *                       ����position
					 *                       ��Ϊ1��֮��positionһֱΪ0�����ǣ���position =
					 *                       1ʱ�� �����һ�����Ļ��position �����Ϊ0��
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
				exchangeFragment.cleanImage();// ����clean����
			}
		} else {
			isFileExchange = false;
			if (exchangeFragment != null) {
				exchangeFragment.removeCleanView();// �Ƴ�clean����
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
							.getMapFragment().get("folder");// �õ�ĳ��fragment�ķ���
															// �����������һ�����������У�
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
