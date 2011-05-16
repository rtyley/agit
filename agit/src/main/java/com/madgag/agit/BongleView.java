package com.madgag.agit;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class BongleView extends ExpandableListView {
    private View mHeaderView;
    private int mHeaderViewWidth;
    private int mHeaderViewHeight;
    private boolean mHeaderViewVisible;
    private static final String TAG = "BONGLE";

    public BongleView(Context context) {
        super(context);
    }

    public BongleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BongleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        stuffIt();
    }

    private void stuffIt() {
        setPinnedHeaderView(getAdapter().getView(0, null, this));
        //View mHeaderView = inflater.inflate(R.layout.list_section, list, false);
    }

    public void setPinnedHeaderView(View view) {
        mHeaderView = view;

        // Disable vertical fading when the pinned header is present
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }

        requestLayout();
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            // configureHeaderView(getFirstVisiblePosition());
            mHeaderViewVisible = true;
        }
    }
}
