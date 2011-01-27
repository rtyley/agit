package com.madgag.gravatar.android;

import android.graphics.drawable.Drawable;

public interface ImageProcessor<ImageResourceType> {
	public Drawable convert(ImageResourceType imageResource);
}
