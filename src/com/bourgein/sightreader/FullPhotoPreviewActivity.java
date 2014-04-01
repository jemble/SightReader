package com.bourgein.sightreader;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class FullPhotoPreviewActivity extends Activity {

	private CropOverlayView cropOverlay;
	private Song song;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_photo_preview);
		
		cropOverlay = (CropOverlayView)findViewById(R.id.fullphoto_crop_photo_preview);
		Bundle bundle = getIntent().getExtras();
		song = bundle.getParcelable(SetSongDetailsActivity.SONG_PARCEL);
		Log.i("JEM","song path: "+song.getImageFileName());
		Bitmap origBmp = BitmapFactory.decodeFile(song.getImageFileName());
		cropOverlay.setAdjustViewBounds(true);
		cropOverlay.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		cropOverlay.setImageBitmap(origBmp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_photo_preview, menu);
		return true;
	}

}
