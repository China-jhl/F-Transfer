package com.rjb.dianfeng.fileexchange.fragment;

import java.io.File;
import java.util.List;

import android.support.v4.app.Fragment;

import com.rjb.dianfeng.fileexchange.interfaces.ICopyFile2;

public class BaseFragment extends Fragment implements ICopyFile2{



	public boolean onPressBack() {
		return false;
	}

	@Override
	public void commBetween2Fragment(List<File> copyFile, Boolean isCopy) {
		// TODO Auto-generated method stub

	}
}
