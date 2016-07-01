package com.rjb.dianfeng.fileexchange.adapter;

import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rjb.dianfeng.fileexchange.fragment.BaseFragment;
import com.rjb.dianfeng.fileexchange.fragment.ClassificationFragment;
import com.rjb.dianfeng.fileexchange.fragment.FileExchangeFragment;
import com.rjb.dianfeng.fileexchange.fragment.FolderFragment;

public class ExchangePagerAdapter extends FragmentPagerAdapter {
	/**
	 * 
	 */

	private HashMap<String, BaseFragment> mapFragment = new HashMap<String, BaseFragment>();// fragmentÈÝÆ÷

	public ExchangePagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		BaseFragment fragment = null;
		switch (arg0) {
		case 0:
			fragment = new FileExchangeFragment();
			mapFragment.put("fileExchange", fragment);
			break;
		case 1:
			fragment = new ClassificationFragment();
			mapFragment.put("classification", fragment);
			break;
		case 2:
			fragment = new FolderFragment();
			mapFragment.put("folder", fragment);
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	public HashMap<String, BaseFragment> getMapFragment() {
		return mapFragment;
	}
}
