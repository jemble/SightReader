package com.bourgein.sightreader;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FullPhotoPreviewActivity extends Activity {

	private CropOverlayView cropOverlay;
	private Song song;
	private Button btnCropOrange, btnCropGreen, btnStop, btnMusic, btnTrash; 
	
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
		
		Bitmap origBmp = BitmapFactory.decodeFile(song.getImageFileName());
		cropOverlay.setAdjustViewBounds(true);
		cropOverlay.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		cropOverlay.setImageBitmap(origBmp);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_photo_preview, menu);
		return true;
	}

}
