package com.madgag.pinnedheader;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.ExpandableListView.getPackedPositionForGroup;
import static android.widget.ExpandableListView.getPackedPositionGroup;
import static java.lang.Math.round;

public class PinnedHeaderTrait implements AbsListView.OnScrollListener {

    private static final String TAG = "PinnedHeaderTrait";
    
    public static final int HEADER_TO_HEADER_BUFFER = 20;

    private final ExpandableListView listView;
    private final ViewGroup parentViewGroup;
    private final HeaderViewGroupAttacher headerViewGroupAttacher;

    private View headerView;

    public PinnedHeaderTrait(ExpandableListView listView, ViewGroup parentViewGroup, HeaderViewGroupAttacher headerViewGroupAttacher) {
        this.listView = listView;
        this.parentViewGroup = parentViewGroup;
        this.headerViewGroupAttacher = headerViewGroupAttacher;
        listView.setOnScrollListener(this);
    }

    public View getHeaderView() {
        return headerView;
    }

    public void configureHeaderView() {
        if (listView.getAdapter()==null) {
            return;
        }

        int firstVisiblePosition = listView.getFirstVisiblePosition();
        long expandableListPosition = listView.getExpandableListPosition(firstVisiblePosition);

        final int group = getPackedPositionGroup(expandableListPosition);

        if (firstVisiblePosition < 0 || !listView.isGroupExpanded(group)
                || (firstVisiblePosition== listView.getFlatListPosition(getPackedPositionForGroup(group)) && listView.getChildAt(0).getTop()>=0)) {
            Log.d(TAG, "PINNED_HEADER_GONE");
            if (headerView !=null) {
                headerView.setVisibility(GONE);
            }
            return;
        }

        headerView = listView.getExpandableListAdapter().getGroupView(group, false, headerView, parentViewGroup);
        headerViewGroupAttacher.attach(headerView);
        headerView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "Got a click...");
                listView.collapseGroup(group);
                listView.setSelectedGroup(group);
            }
        });

        int headerTop = calculatePushOffset(group, firstVisiblePosition);
        Drawable background = new ColorDrawable(Color.WHITE); // header.getBackground()
        headerView.setBackgroundDrawable(background);
        int measuredHeight = headerView.getMeasuredHeight();
        Log.d(TAG, "headerView.getTop()="+headerView.getTop()+" headerTop="+headerTop+"  measuredHeight="+measuredHeight);
        if (headerView.getTop() != headerTop || headerView.getHeight()!= measuredHeight) {
            Log.d(TAG, "Performing headerView layout measuredHeight="+measuredHeight);
            headerView.layout(0, headerTop,
                    headerView.getMeasuredWidth(), measuredHeight + headerTop);
        }
        headerView.setVisibility(VISIBLE);
    }

    private int calculatePushOffset(int group, int firstVisiblePosition) {
        int nextHeaderGroup = group + 1; // TODO nextHeaderGroup could be not next, and yet close enough
        Log.d(TAG, "PINNED_HEADER_VISIBLE");
        int headerHeight = headerView.getHeight();
        int headerHeightWithBuffer = headerHeight + HEADER_TO_HEADER_BUFFER;

        int headerTop=0;
        int nextHeaderFlatListPosition = listView.getFlatListPosition(getPackedPositionForGroup(nextHeaderGroup));
        if (nextHeaderFlatListPosition<=listView.getLastVisiblePosition()) {
            // next header is visible, we may  need to handle partial push-up
            View v = listView.getChildAt(nextHeaderFlatListPosition-firstVisiblePosition);
            Log.d(TAG, "firstVisiblePosition = "+firstVisiblePosition+" nextHeaderFlatListPosition="+nextHeaderFlatListPosition+" v="+v);
            if (v!=null && v.getTop() < headerHeightWithBuffer) {
                headerTop = round((v.getTop() - headerHeightWithBuffer)*1.1f); // make header 'zoom' away from next header
            }
        }
        Log.d(TAG, "configureHeaderView headerTop="+headerTop+" headerHeight="+headerHeight);
        return headerTop;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        configureHeaderView();
    }

    public interface HeaderViewGroupAttacher {
        void attach(View header);
    }
}
