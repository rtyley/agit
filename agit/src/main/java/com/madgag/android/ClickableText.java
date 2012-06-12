package com.madgag.android;

import static android.graphics.Typeface.DEFAULT_BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import android.text.Editable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickableText {

    private static Pattern markup = Pattern.compile("#([\\w_]+)\\[([^\\]]+)\\]");
    private static final String TAG = "ClickableText";

    public static void addLinks(Editable spannable, final CharacterStyle linkStyle, final Listener listener) {
        Matcher matcher;
        while ((matcher = markup.matcher(spannable)).find()) {
            final String action = matcher.group(1);
            spannable.setSpan(new ClickableSpan() {

                public void updateDrawState(TextPaint textPaint) {
                    linkStyle.updateDrawState(textPaint);
                }

                @Override
                public void onClick(View widget) {
                    widget.playSoundEffect(SoundEffectConstants.CLICK);
                    Log.d(TAG, "Clicked " + action);
                    listener.onClick(action, widget);
                }
            }, matcher.start(2), matcher.end(2), SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.delete(matcher.end(2), matcher.end());
            spannable.delete(matcher.start(), matcher.start(2));
        }
    }

    public static CharacterStyle PLAIN_LINK_STYLE = new CharacterStyle() {
        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setColor(paint.linkColor);
        }
    };

    public static CharacterStyle BOLD_LINK_STYLE = new CharacterStyle() {
        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setColor(paint.linkColor);
            paint.setTypeface(DEFAULT_BOLD);
        }
    };

    public static void makeLinksClickableIn(TextView textView) {
        textView.setMovementMethod(LinkMovementMethod.getInstance()); // otherwise links aren't clickable...
    }

    public static interface Listener {
        public void onClick(String command, View widget);
    }
}
