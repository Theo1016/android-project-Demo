package com.theo.sdk.ui.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.theo.sdk.R;
import com.theo.sdk.ui.activity.view.TitleHeaderBar;


public abstract class TitleBaseFragment extends SDKBaseFragment {

    protected TitleHeaderBar mTitleHeaderBar;
    protected LinearLayout mContentContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(getFrameLayoutId(), null);
        LinearLayout contentContainer = (LinearLayout) view.findViewById(R.id.sdk_content_frame_content);

        mTitleHeaderBar = (TitleHeaderBar) view.findViewById(R.id.sdk_content_frame_title_header);
        if (enableDefaultBack()) {
            mTitleHeaderBar.setLeftOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            mTitleHeaderBar.getLeftViewContainer().setVisibility(View.INVISIBLE);
        }

        mContentContainer = contentContainer;
        View contentView = createView(inflater, view, savedInstanceState);
        contentView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        contentContainer.addView(contentView);
        return view;
    }

    protected int getFrameLayoutId() {
        return R.layout.sdk_base_content_frame_with_title_header;
    }

    private void onBackPressed() {
        getContext().onBackPressed();
    }

    protected boolean enableDefaultBack() {
        return true;
    }

    protected void setHeaderTitle(int id) {
        mTitleHeaderBar.getTitleTextView().setText(id);
    }

    protected void setHeaderTitle(String title) {
        mTitleHeaderBar.getTitleTextView().setText(title);
    }

    public TitleHeaderBar getTitleHeaderBar() {
        return mTitleHeaderBar;
    }
}
