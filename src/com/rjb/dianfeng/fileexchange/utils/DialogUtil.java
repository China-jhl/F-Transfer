package com.rjb.dianfeng.fileexchange.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.ClassficationFileActivity;
import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.MainActivity;
import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.adapter.ExchangePagerAdapter;
import com.rjb.dianfeng.fileexchange.fragment.FolderFragment;

public class DialogUtil {

	public interface IDialogMethod {
		public void confirm(String newName);

		public void cancel();
	}

	/**
	 * 
	 * @param context
	 *            要用显示dialog的 类的 类名.this
	 * @param methods
	 * @param flag
	 *            标记是谁在调用dialog
	 */
	public static void showDialog(final Context context,
			final IDialogMethod methods, String name, final int flag) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(context, R.layout.view_dialog, null);
		dialog.setView(view);
		final EditText newname = (EditText) view
				.findViewById(R.id.dialog_et_newname);
		newname.setText(name);
		Button yes = (Button) view.findViewById(R.id.bt_yes_newname);
		Button no = (Button) view.findViewById(R.id.bt_no_newname);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				methods.confirm(newname.getText().toString());
				switch (flag) {
				case Constant.ClASSIFICATION_FILE_ACTIVITY:
					((ClassficationFileActivity) context)
							.afterSucceedFileOperation();
					break;

				case Constant.FOLDER_FRAGMENT:
					ExchangePagerAdapter adpter = ((MainActivity) context)
							.getExchangePagerAdapter();
					if (adpter != null) {
						FolderFragment folderFragment = (FolderFragment) adpter
								.getMapFragment().get("folder");
						folderFragment.afterSucceedFileOperation();
					}
				}

				dialog.dismiss();
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog.show();
	}

	public static void showFileInfo(Context context, String file_info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog dialog = builder.create();

		View view = View.inflate(context, R.layout.view_file_info_dialog, null);
		dialog.setView(view);
		TextView info = (TextView) view.findViewById(R.id.tv_file_info);
		info.setText(file_info);

		dialog.show();
	}

	public static void messageDialog(Context context,
			final IDialogMethod methods, String title, String content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(context, R.layout.view_confirm_send_dialog,
				null);
		dialog.setView(view);
		TextView dialog_title = (TextView) view
				.findViewById(R.id.dialog_tv_title);
		dialog_title.setText(title);
		TextView message = (TextView) view.findViewById(R.id.dialog_tv_message);
		message.setText(content);
		Button yes = (Button) view.findViewById(R.id.bt_yes_newname);
		Button no = (Button) view.findViewById(R.id.bt_no_newname);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				methods.confirm(null);
				dialog.dismiss();
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				methods.cancel();
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public static void showReceiveDialog(final Context context,
			final IDialogMethod methods) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(context, R.layout.view_dialog, null);
		dialog.setView(view);
		final EditText newname = (EditText) view
				.findViewById(R.id.dialog_et_newname);
		newname.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		Button yes = (Button) view.findViewById(R.id.bt_yes_newname);
		Button no = (Button) view.findViewById(R.id.bt_no_newname);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (newname.getText().toString().isEmpty()
						|| newname.getText().toString().equals("0")) {
					Toast.makeText(context, "文件个数不正确", Toast.LENGTH_SHORT)
							.show();
				} else {
					methods.confirm(newname.getText().toString());
					dialog.dismiss();
				}
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog.show();

	}
}
