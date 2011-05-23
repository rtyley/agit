package com.madgag.pinnedheader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.madgag.agit.R;

import static java.lang.Math.round;

public class PinnedHeaderLayout extends ViewGroup implements PinnedHeaderTrait.HeaderViewGroupAttacher {

    private Drawable shadowGradient = getResources().getDrawable(R.drawable.black_white_gradient);
    private static final String TAG = "PHL";

    private PinnedHeaderTrait pinnedHeaderTrait;

    public PinnedHeaderLayout(Context context) {
        super(context);
    }

    public PinnedHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedHeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initListView();
    }

    private void initListView() {
        pinnedHeaderTrait = new PinnedHeaderTrait((ExpandableListView) getChildAt(0), this, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View v = getChildAt(0);
        // measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChild(v, widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(
                resolveSize(v.getMeasuredWidth(), widthMeasureSpec),
                resolveSize(v.getMeasuredHeight(), heightMeasureSpec));

        View headerView = pinnedHeaderTrait == null ? null : pinnedHeaderTrait.getHeaderView();
        if (headerView != null) {
            measureChild(headerView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout called");
        // mForegroundBoundsChanged = true;
        View v = getChildAt(0);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        pinnedHeaderTrait.configureHeaderView();
    }
    
    public void attach(View header) {
        View currentHeader = getChildAt(1);
        if (currentHeader ==null) {
            addView(header, 1);
        } else if (currentHeader!=header) {
            removeViewAt(1);
            addView(header, 1);
        }
    }
}
