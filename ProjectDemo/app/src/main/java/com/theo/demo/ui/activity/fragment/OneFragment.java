package com.theo.demo.ui.activity.fragment;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.theo.demo.R;
import com.theo.demo.requestor.OneFragmentRequestor;
import com.theo.sdk.request.AbstractRequestor;
import com.theo.sdk.ui.activity.fragment.TitleBaseFragment;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Theo on 15/6/19.
 */
public class OneFragment extends TitleBaseFragment{
    private PullToRefreshListView mPullRefreshListView;
    private LinkedList<String> mListItems;
    private ArrayAdapter<String> mAdapter;
    private OneFragmentRequestor requestor;
    private AbstractRequestor.OnRequestListener requestListener;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHeaderTitle(R.string.one);
        final View contentView = inflater.inflate(R.layout.fragment_one, null);
        mPullRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                
                //请求数据
                requestor = new OneFragmentRequestor(getContext());
                requestor.doRequest(requestListener);


            }
        });
        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                Toast.makeText(getContext(), "End of List!", Toast.LENGTH_SHORT).show();
            }
        });
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        registerForContextMenu(actualListView);
        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mListItems);
        actualListView.setAdapter(mAdapter);
        return contentView;
    }

    private String[] mStrings = { "aboard", "abroad", "afterward", "against", "agriculture",
            "ahead", "aid", "aim", "alcohol", "among", "ancient", "angle",
            "apartment", "argue", "arrange", "autumn", "avenue", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler" };

    class  requestListener implements AbstractRequestor.OnRequestListener{

        @Override
        public void onSuccess(AbstractRequestor requestor) {

        }

        @Override
        public void onFailed(AbstractRequestor requestor, int errorCode) {

        }
    }

}
