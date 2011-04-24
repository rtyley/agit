package com.madgag.agit;

import android.nfc.Tag;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class ClickableText {

    private static Pattern markup = Pattern.compile("#([\\w_]+)\\[([^\\]]+)\\]");
    private static final String TAG = "CT";

    public static void addLinks(Editable spannable, final Listener listener) {
        // SpannableStringBuilder builder = new SpannableStringBuilder();
        //Log.d(TAG,"spannable="+spannable);
        Matcher matcher;
        while ((matcher=markup.matcher(spannable)).find()) {
            final String action=matcher.group(1);
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.d(TAG,"Clicked "+action);
                    listener.onClick(action, widget);
                }
            },  matcher.start(2), matcher.end(2), SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.delete(matcher.end(2),matcher.end());
            spannable.delete(matcher.start(),matcher.start(2));
        }
    }

    public static interface Listener {
        public void onClick(String command, View widget);
    }
}
