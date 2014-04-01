package com.bourgein.sightreader;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CropOverlayView extends ImageView {

	public CropOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
	}

}
