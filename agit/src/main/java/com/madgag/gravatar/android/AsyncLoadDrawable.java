package com.madgag.gravatar.android;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public class AsyncLoadDrawable extends Drawable {

	private Drawable delegate;
	
	@Override
	public void draw(Canvas canvas) {
		delegate.draw(canvas);
	}

	@Override
	public void setAlpha(int alpha) {
		delegate.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		delegate.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return delegate.getOpacity();
	}

	public void onLoad(Drawable delegate) {
		this.delegate = delegate;
		invalidateSelf();
	}
	
}
