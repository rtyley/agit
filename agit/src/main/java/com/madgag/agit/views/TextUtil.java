package com.madgag.agit.views;

import static android.text.Html.fromHtml;
import static android.text.Layout.Alignment.ALIGN_CENTER;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;

public class TextUtil {
    public static final String ITALIC_CLIPPING_BUFFER = " ";

    public static SpannableString centered(String htmlMessage) {
        SpannableString spannable = new SpannableString(fromHtml(htmlMessage));
        spannable.setSpan(new AlignmentSpan.Standard(ALIGN_CENTER), 0, spannable.length(), SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;
    }
}