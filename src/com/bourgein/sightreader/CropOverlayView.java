package com.bourgein.sightreader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CropOverlayView extends ImageView {
	
	private static final int HANDLE_SIZE = 30;
	public float x1,x2,y1,y2;
	public boolean isDrawingCropBox;
	public RectF topLeftHandle;
	public RectF bottomRightHandle;
	private Paint paint;
	private Paint handlePaint;

	public CropOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		isDrawingCropBox = false;
		x1 = 10;
		x2 = 110;
		y1 = 50;
		y2 = 220;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(246,136,31));
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		handlePaint.setColor(Color.rgb(246,136,31));		
		topLeftHandle = new RectF();
		bottomRightHandle = new RectF();
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		Log.i("JEM","crop pos: "+x1+" "+y1+" "+x2+" "+y2);
		if(isDrawingCropBox){
			canvas.drawRect(x1,y1,x2,y2, paint);
			topLeftHandle.set(x1-HANDLE_SIZE,y1-HANDLE_SIZE,x1+HANDLE_SIZE,y1+HANDLE_SIZE);
			bottomRightHandle.set(x2-HANDLE_SIZE,y2-HANDLE_SIZE,x2+HANDLE_SIZE,y2+HANDLE_SIZE);
			canvas.drawRect(topLeftHandle,handlePaint);
			canvas.drawRect(bottomRightHandle,handlePaint);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    this.setMeasuredDimension(parentWidth, parentHeight);
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
