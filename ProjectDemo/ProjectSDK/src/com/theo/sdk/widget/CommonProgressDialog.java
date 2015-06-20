package com.theo.sdk.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.theo.sdk.R;

/**
 * 通用ProgressDialog
 * @author Theo
 *
 */
public class CommonProgressDialog extends Dialog {
	private ImageView mImageView;
	private TextView mTextView;
	private View view;
	private Context mContext;

	public CommonProgressDialog(final Context context) {
		super(context, R.style.iphone_progress_dialog);
		this.mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.view_progress_dialog, null);
		mTextView = (TextView) view
				.findViewById(R.id.iphone_progress_dialog_txt);
		setContentView(view);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mImageView = (ImageView) view
				.findViewById(R.id.iphone_progress_dialog_img);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.progressbar);
		mImageView.startAnimation(anim);
	}

	public void setMsg(String msg) {
		mTextView.setVisibility(View.VISIBLE);
		mTextView.setText(msg);
	}

	public void setMsg(int msgId) {
		mTextView.setVisibility(View.VISIBLE);
		mTextView.setText(msgId);
	}

}
