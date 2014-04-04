package com.bourgein.sightreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class FullPhotoPreviewActivity extends Activity {

	private CropOverlayView cropOverlay;
	private Song song;
	private ImageButton btnCropOrange, btnCropGreen, btnStop, btnMusic, btnTrash; 
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
		
		btnCropOrange = (ImageButton)findViewById(R.id.fullphoto_btn_crop_orange);
		btnCropGreen = (ImageButton)findViewById(R.id.fullphoto_btn_crop_green);
		btnStop = (ImageButton)findViewById(R.id.fullphoto_btn_stop);
		btnMusic = (ImageButton)findViewById(R.id.fullphoto_btn_music);
		btnTrash = (ImageButton)findViewById(R.id.fullphoto_btn_trash);
		
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
		if(cropOverlay.x1 < cropOverlay.x2 && cropOverlay.y1 < cropOverlay.y2){
			croppedBmp = Bitmap.createBitmap(origBmp,intX1,intY1,(intX2-intX1),(intY2-intY1));
			cropOverlay.setImageBitmap(croppedBmp);
			try{
				writeImageToFile(croppedBmp);
			}
			catch(IOException e){
				Toast.makeText(getApplicationContext(), "problem saving cropped file to sd card",Toast.LENGTH_LONG).show();
			}
			cropOverlay.isDrawingCropBox = false;
		}
	}
	
	public void deletePhoto(View view){
		File file = new File(song.getImageFileName());
		file.delete();
		Intent camera_intent = new Intent(getApplicationContext(),CapturePictureActivity.class);
		camera_intent.putExtra(SetSongDetailsActivity.SONG_PARCEL, song);
		startActivity(camera_intent);
	}
	
	public void uploadPhoto(View view){
		if(isDataConnection()){
			Intent midiIntent = new Intent(getApplicationContext(), MidiPlayerActivity.class);
			midiIntent.putExtra(SetSongDetailsActivity.SONG_PARCEL, song);
			midiIntent.putExtra(ServerHelper.LOADING_FROM_SERVER, true);
			startActivity(midiIntent);
		}
		else{
			Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean isDataConnection(){
		ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}
	
	private void writeImageToFile(Bitmap bmp) throws IOException{
		File pictureFile = new File(song.getImageFileName());
		FileOutputStream fos = new FileOutputStream(pictureFile);
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		fos.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_photo_preview, menu);
		return true;
	}

}
