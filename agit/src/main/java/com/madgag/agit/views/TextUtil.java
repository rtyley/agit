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