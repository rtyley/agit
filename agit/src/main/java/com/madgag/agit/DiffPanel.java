/**
 * 
 */
package com.madgag.agit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

public class DiffPanel extends View {

	private ShapeDrawable mDrawable;

	
	int width = 300;
	int height = 50;
	
	public DiffPanel(Context context) {
		super(context);
		setupDrawable();
	}
	
	public DiffPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawable();
	}

	private void setupDrawable() {
		float or=3.0f;
		float[] outerRadii = {or, or, or, or, or, or, or, or};
		mDrawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
		mDrawable.getPaint().setColor(0xffff0000);
		
		Bitmap bitmap;
		//Canvas c = new Canvas(bitmap);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int gridSize=40;
		canvas.drawColor(Color.WHITE);
		for (int x=0;x<400;x+=gridSize) {
			for (int y=0;y<400;y+=gridSize) {
				mDrawable.setBounds(x, y, x + width, y + gridSize - 4);
				mDrawable.draw(canvas);
			}
		}

	}

	public void setProgress(int progress) {
		width=progress;
	}
}