package com.bourgein.sightreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;

public class PhotoHandler implements PictureCallback {

	Context context;
	Song song;
	
	public PhotoHandler(Context context, Song song){
		this.context = context;
		this.song = song;
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		File pictureFileDir = getExternalDir();
		File pictureFile;
		
		if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()){
			return;
		}
		
		try {
			pictureFile = File.createTempFile("srf", ".jpg", pictureFileDir);
			song.setImageFileName(pictureFile.getAbsolutePath());
		} catch (IOException e) {
			return;
		}
		
		try{
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		}
		catch(Exception ex){
			return;
		}
		Intent prevIntent = new Intent(context,FullPhotoPreviewActivity.class);
		prevIntent.putExtra(SetSongDetailsActivity.SONG_PARCEL, song);
		context.startActivity(prevIntent);
	}
	
	private File getExternalDir(){
		File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir,"SightReader");
	}

}
