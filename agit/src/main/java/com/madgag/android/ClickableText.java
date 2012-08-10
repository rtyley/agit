/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

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
