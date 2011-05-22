package com.madgag.pinnedheader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import com.madgag.agit.R;

import static android.widget.ExpandableListView.*;
import static java.lang.Math.round;

public class PinnedHeaderLayout extends ViewGroup implements AbsListView.OnScrollListener {

    private Drawable shadowGradient = getResources().getDrawable(R.drawable.black_white_gradient);
    private static final String TAG = "PHL";

    // final ExpandableListView listView;
    private View mHeaderView;
    private int mHeaderViewWidth,mHeaderViewHeight;
    private ExpandableListView listView;


    public PinnedHeaderLayout(Context context) {
        super(context);
        // listView = initListView();
    }

    public PinnedHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // listView = initListView();
    }

    public PinnedHeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // listView = initListView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initListView();
    }

    private void initListView() {
        listView = (ExpandableListView) getChildAt(0);;
        Log.d(TAG, "initListView called elv="+listView);
        listView.setOnScrollListener(this);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        Log.d(TAG, "onLayout called listView=" + listView);
        measureChild(listView, widthMeasureSpec, heightMeasureSpec);
        int maxWidth = listView.getMeasuredWidth();
        int maxHeight = listView.getMeasuredHeight();

        setMeasuredDimension(
                resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
        
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout called");
        // mForegroundBoundsChanged = true;
        listView.layout(0, 0, listView.getMeasuredWidth(), listView.getMeasuredHeight());

        configureHeaderView(listView.getFirstVisiblePosition());
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
        }
    }

    private void configureHeaderView(int firstVisiblePosition) {
        if (listView.getAdapter()==null) {
            return;
        }

        int group = getPackedPositionGroup(listView.getExpandableListPosition(firstVisiblePosition));

        if (firstVisiblePosition < 0 || !listView.isGroupExpanded(group)) {
            Log.d(TAG, "PINNED_HEADER_GONE");
            if (mHeaderView!=null) {
                mHeaderView.setVisibility(GONE);
            }
            return;
        }

        // int nextHeaderGroup = pinnedHeaderGroup + 1; // TODO nextHeaderGroup could be not next, and yet close enough

        // The header should get pushed up if the top item shown is the last item in a group.
//        if (getPackedPositionType(listView.getExpandableListPosition(firstVisiblePosition + 1)) == PACKED_POSITION_TYPE_GROUP) {
//            state = PINNED_HEADER_VISIBLE;
//        }
        mHeaderView = getOrCreateHeaderView(listView, group);

        Log.d(TAG, "PINNED_HEADER_VISIBLE");
        View firstView = getChildAt(0);
        int bottom = firstView.getBottom();
        Log.d(TAG, "PINNED_HEADER_PUSHED_UP firstView="+firstView+" firstView.getBottom()="+bottom);
        int itemHeight = firstView.getHeight();
        int headerHeight = mHeaderView.getHeight();
        int headerHeightWithBuffer = headerHeight+20;
        int y;
        if (bottom < headerHeightWithBuffer) {
            y = round((bottom - headerHeightWithBuffer)*1.1f); // make header 'zoom' away from next header
        } else {
            y = 0;
        }
        Log.d(TAG, "configureHeaderView y="+y);
        Log.d(TAG, "configurePinnedHeader group=" + group);

        if (getChildCount()>1) {
            removeViewAt(1);
        }
        addView(mHeaderView, 1);
//        mHeaderView.requestLayout(); // TODO - is this the right thing to do to get the text to appear correctly?
//        mHeaderView.invalidate();
        Drawable background = new ColorDrawable(Color.WHITE); // header.getBackground()
        mHeaderView.setBackgroundDrawable(background);
        if (mHeaderView.getTop() != y) {
            mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
        }
        mHeaderView.setVisibility(VISIBLE);
    }


    private View getOrCreateHeaderView(ExpandableListView listView, int group) {
        return listView.getExpandableListAdapter().getGroupView(group, false, mHeaderView, this);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "onScroll called");
        configureHeaderView(firstVisibleItem);
    }

}
