package com.theo.sdk.ui.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theo.sdk.R;


public class TitleHeaderBar extends RelativeLayout {

    private TextView mCenterTitleTextView;
    private ImageView mLeftReturnImageView;
    private RelativeLayout mLeftViewContainer;
    private RelativeLayout mRightViewContainer;
    private RelativeLayout mCenterViewContainer;

    private String mTitle;

    public TitleHeaderBar(Context context) {
        this(context, null);
    }

    public TitleHeaderBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleHeaderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(getHeaderViewLayoutId(), this);
        mLeftViewContainer = (RelativeLayout) findViewById(R.id.ly_title_bar_left);
        mCenterViewContainer = (RelativeLayout) findViewById(R.id.ly_title_bar_center);
        mRightViewContainer = (RelativeLayout) findViewById(R.id.ly_title_bar_right);
        mLeftReturnImageView = (ImageView) findViewById(R.id.iv_title_bar_left);
        mCenterTitleTextView = (TextView) findViewById(R.id.tv_title_bar_title);
    }

    protected int getHeaderViewLayoutId() {
        return R.layout.sdk_base_header_bar_title;
    }

    public ImageView getLeftImageView() {
        return mLeftReturnImageView;
    }

    public TextView getTitleTextView() {
        return mCenterTitleTextView;
    }

    public void setTitle(String title) {
        mTitle = title;
        mCenterTitleTextView.setText(title);
    }

    public String getTitle() {
        return mTitle;
    }

    private RelativeLayout.LayoutParams makeLayoutParams(View view) {
        ViewGroup.LayoutParams lpOld = view.getLayoutParams();
        RelativeLayout.LayoutParams lp = null;
        if (lpOld == null) {
            lp = new RelativeLayout.LayoutParams(-2, -1);
        } else {
            lp = new RelativeLayout.LayoutParams(lpOld.width, lpOld.height);
        }
        return lp;
    }


    public void setCustomizedLeftView(View view) {
        mLeftReturnImageView.setVisibility(GONE);
        RelativeLayout.LayoutParams lp = makeLayoutParams(view);
        lp.addRule(CENTER_VERTICAL);
        lp.addRule(ALIGN_PARENT_LEFT);
        getLeftViewContainer().addView(view, lp);
    }


    public void setCustomizedLeftView(int layoutId) {
        View view = inflate(getContext(), layoutId, null);
        setCustomizedLeftView(view);
    }


    public void setCustomizedCenterView(View view) {
        mCenterTitleTextView.setVisibility(GONE);
        RelativeLayout.LayoutParams lp = makeLayoutParams(view);
        lp.addRule(CENTER_IN_PARENT);
        getCenterViewContainer().addView(view, lp);
    }


    public void setCustomizedCenterView(int layoutId) {
        View view = inflate(getContext(), layoutId, null);
        setCustomizedCenterView(view);
    }


    public void setCustomizedRightView(View view) {
        RelativeLayout.LayoutParams lp = makeLayoutParams(view);
        lp.addRule(CENTER_VERTICAL);
        lp.addRule(ALIGN_PARENT_RIGHT);
        getRightViewContainer().addView(view, lp);
    }

    public RelativeLayout getLeftViewContainer() {
        return mLeftViewContainer;
    }

    public RelativeLayout getCenterViewContainer() {
        return mCenterViewContainer;
    }

    public RelativeLayout getRightViewContainer() {
        return mRightViewContainer;
    }

    public void setLeftOnClickListener(OnClickListener l) {
        mLeftViewContainer.setOnClickListener(l);
    }

    public void setCenterOnClickListener(OnClickListener l) {
        mCenterViewContainer.setOnClickListener(l);
    }

    public void setRightOnClickListener(OnClickListener l) {
        mRightViewContainer.setOnClickListener(l);
    }
}