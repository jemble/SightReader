package com.bourgein.sightreader;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class FullPhotoPreviewActivity extends Activity {

	private CropOverlayView cropOverlay;
	private Song song;
	private Button btnCropOrange, btnCropGreen, btnStop, btnMusic, btnTrash; 
	private float x1,x2,y1,y2;
	private float imgHeight, imgWidth;
	private boolean isInTopLeft = false;
	private boolean isInBottomRight = false;
	Bitmap origBmp,croppedBmp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_photo_preview);
		
		cropOverlay = (CropOverlayView)findViewById(R.id.fullphoto_crop_photo_preview);
		Bundle bundle = getIntent().getExtras();
		song = bundle.getParcelable(SetSongDetailsActivity.SONG_PARCEL);
		
		btnCropOrange = (Button)findViewById(R.id.fullphoto_btn_crop_orange);
		btnCropGreen = (Button)findViewById(R.id.fullphoto_btn_crop_green);
		btnStop = (Button)findViewById(R.id.fullphoto_btn_stop);
		btnMusic = (Button)findViewById(R.id.fullphoto_btn_music);
		btnTrash = (Button)findViewById(R.id.fullphoto_btn_trash);
		
		btnCropGreen.setVisibility(View.INVISIBLE);
		btnStop.setVisibility(View.INVISIBLE);
		
		origBmp = BitmapFactory.decodeFile(song.getImageFileName());
		
		cropOverlay.setAdjustViewBounds(true);
		cropOverlay.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		cropOverlay.setImageBitmap(origBmp);
		
		//must be after bmp has been set
		imgHeight = origBmp.getHeight();
		imgWidth = origBmp.getWidth();
		
		cropOverlay.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(cropOverlay.isDrawingCropBox){
					switch(event.getAction()){
					case (MotionEvent.ACTION_DOWN):
						x1 = event.getX();
						y1 = event.getY();
						if(cropOverlay.topLeftHandle.contains(x1,y1)){
							isInTopLeft = true;
							isInBottomRight = false;
						}
						
						if(cropOverlay.bottomRightHandle.contains(x1,y1)){
							Log.i("JEM","in bottom right");
							isInTopLeft = false;
							isInBottomRight = true;
						}
						break;
					case (MotionEvent.ACTION_MOVE):
						x2 = event.getX();
						y2 = event.getY();
						if(isInTopLeft){
							Log.i("JEM","moving in top left");
							cropOverlay.x1 = x2;
							cropOverlay.y1 = y2;
						}
						else if(isInBottomRight){
							Log.i("JEM","moving in bottom right");
							cropOverlay.x2 = x2;
							cropOverlay.y2 = y2;
						}
						else{
							cropOverlay.x1 = x1;
							cropOverlay.x2 = x2;
							cropOverlay.y1 = y1;
							cropOverlay.y2 = y2;
						}
						cropOverlay.invalidate();
						break;
					case(MotionEvent.ACTION_UP):
						isInBottomRight = false;
						isInTopLeft = false;
//						x2 = event.getX();
//						y2 = event.getY();
					}
				}
				return true;
			}
		});
		
	}

	public void startCropping(View view){
		btnCropOrange.setVisibility(View.INVISIBLE);
		btnMusic.setVisibility(View.INVISIBLE);
		btnTrash.setVisibility(View.INVISIBLE);
		
		btnCropGreen.setVisibility(View.VISIBLE);
		btnStop.setVisibility(View.VISIBLE);
		
		//these are here so that we calculate the picView height once we know the image has finished inflating 
		int cropOverlayHeight = cropOverlay.getHeight();
		int cropOverlayWidth = cropOverlay.getWidth();
		
		cropOverlay.x1 = cropOverlayWidth/4;
		cropOverlay.y1 = cropOverlayHeight/4;
		cropOverlay.x2 = (cropOverlayWidth-(cropOverlayWidth/4));
		cropOverlay.y2 = (cropOverlayHeight-(cropOverlayHeight/4));
		
		cropOverlay.invalidate(); //needed so that onDraw gets called again
		cropOverlay.isDrawingCropBox = true;

	}
	
	public void stopCropping(View view){
		btnCropOrange.setVisibility(View.VISIBLE);
		btnMusic.setVisibility(View.VISIBLE);
		btnTrash.setVisibility(View.VISIBLE);
		
		btnCropGreen.setVisibility(View.INVISIBLE);
		btnStop.setVisibility(View.INVISIBLE);
		
		cropOverlay.invalidate(); //needed so that onDraw gets called again
		cropOverlay.isDrawingCropBox = false;
	}
	
	public void cropPhoto(View view){
		
		btnCropOrange.setVisibility(View.VISIBLE);
		btnMusic.setVisibility(View.VISIBLE);
		btnTrash.setVisibility(View.VISIBLE);
		
		btnCropGreen.setVisibility(View.INVISIBLE);
		btnStop.setVisibility(View.INVISIBLE);
	
		int picViewHeight = cropOverlay.getHeight();
		int picViewWidth = cropOverlay.getWidth();
		
		float scalarY = imgHeight/picViewHeight;
		float scalarX = imgWidth/picViewWidth;
		int intX1 = (int)(cropOverlay.x1*scalarX);
		int intY1 = (int)(cropOverlay.y1*scalarY);
		int intX2 = (int)(cropOverlay.x2*scalarX);
		int intY2 = (int)(cropOverlay.y2*scalarY);
		croppedBmp = Bitmap.createBitmap(origBmp,intX1,intY1,(intX2-intX1),(intY2-intY1));
		cropOverlay.setImageBitmap(croppedBmp);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_photo_preview, menu);
		return true;
	}

}
