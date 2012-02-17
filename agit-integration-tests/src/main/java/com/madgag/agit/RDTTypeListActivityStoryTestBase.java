package com.madgag.agit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class RDTTypeListActivityStoryTestBase<T extends RDTypeListActivity> extends ActivityInstrumentationTestCase2<T> {

    private final static String TAG = RDTTypeListActivityStoryTestBase.class.getSimpleName();

    public RDTTypeListActivityStoryTestBase(String pkg, Class<T> activityClass) {
        super(pkg, activityClass);
    }

    protected void checkCanSelectEveryItemInNonEmpty(ListView listView) {
        assertThat(listView.getCount()>0, is(true));
        for (int index=0; index<listView.getCount(); ++index) {
            View itemView=getItemViewBySelecting(listView, index);
            Log.d(TAG, "view=" + itemView);
        }
    }

    protected View getItemViewBySelecting(final ListView listView, final int index) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                listView.setSelection(index);
            }
        });
        getInstrumentation().waitForIdleSync();
        return listView.getSelectedView();
    }
}
