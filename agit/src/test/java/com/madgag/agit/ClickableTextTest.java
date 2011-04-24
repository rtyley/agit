package com.madgag.agit;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
public class ClickableTextTest {

    @Test
    public void shouldBeGood() {
        //ClickableText.addLinks(new SpannableStringBuilder("Enter a url to clone from- or use a #suggest_repo[suggestion]?"));
    }

    private class Boo implements Spannable {
           private final CharSequence delegate;

        public Boo(CharSequence delegate) {
            this.delegate = delegate;
        }


        public void setSpan(Object what, int start, int end, int flags) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void removeSpan(Object what) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> T[] getSpans(int start, int end, Class<T> type) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getSpanStart(Object tag) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getSpanEnd(Object tag) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getSpanFlags(Object tag) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int nextSpanTransition(int start, int limit, Class type) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int length() {
            return delegate.length();  //To change body of implemented methods use File | Settings | File Templates.
        }

        public char charAt(int index) {
            return delegate.charAt(index);  //To change body of implemented methods use File | Settings | File Templates.
        }

        public CharSequence subSequence(int start, int end) {
            return delegate.subSequence(start,end);  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
