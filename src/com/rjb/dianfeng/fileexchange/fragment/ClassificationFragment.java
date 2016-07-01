package com.rjb.dianfeng.fileexchange.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.rjb.dianfeng.fileexchange.ClassficationFileActivity;
import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.adapter.ClassificationAdapter;

public class ClassificationFragment extends BaseFragment {


	private GridView file_classification;
	private ClassificationAdapter adapter;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_classification, null);

		return view;
	}

	@Override
	public void onResume() {
		initData();
		initView(view);
		setListener();
		super.onResume();
	}

	private void initView(View view) {
		file_classification = (GridView) view
				.findViewById(R.id.gv_classification);
		file_classification.setAdapter(adapter);

	}

	private void initData() {
		adapter = new ClassificationAdapter(getActivity());
	}

	private void setListener() {
		file_classification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						ClassficationFileActivity.class);
				intent.putExtra("cellNum", position);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.tran_in,
						R.anim.tran_out);
			}
		});

	}

	// 序列化和反序列化
}
