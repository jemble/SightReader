package com.bourgein.sightreader;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class CapturePictureActivity extends MenuDefiner implements SurfaceHolder.Callback{
	
	private Camera cam;
	private Song song;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_picture);
		
		Bundle bundle = getIntent().getExtras();
		song = bundle.getParcelable(Song.SONG_PARCEL);
		
		SurfaceView surface = (SurfaceView)findViewById(R.id.capPic_surface_preview);
        SurfaceHolder holder = surface.getHolder();
        holder.addCallback(this);
	}

	private boolean hasCameraHardware(Context context){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
    		return true;
    	}
    	else {
    		return false;
    	}
	}
	
	private boolean hasOpenedCamSafely(int id){
		boolean opened = false;
		
		if(hasCameraHardware(getApplicationContext())){
    		try{
	    		releaseCamAndPrev();
	    		cam = Camera.open(id);
	    		
	    		//adapted from http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview
	    		//sorts out file being wrong orientation
	    		cam.setDisplayOrientation(90);
	    		Camera.Parameters cParam = cam.getParameters();
	    		cParam.setRotation(90);
	    		cParam.setJpegQuality(100);
	    		cParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	    		cParam.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//	    		List<String> colorEffects = cParam.getSupportedColorEffects();
//	    		for(String s : colorEffects){
//	    			if(s.equalsIgnoreCase(Camera.Parameters.EFFECT_MONO)){
//	    				Log.i("JEM","hs mono");
//	    				cParam.setColorEffect(Camera.Parameters.EFFECT_MONO);
//	    			}
//	    		}
	    		
	    			
	    		cam.setParameters(cParam);
	    		
	    		opened = (cam != null);
	    		
	    	}catch(Exception ex){
	    		Toast.makeText(getApplicationContext(), "there was a problem opening the camera", Toast.LENGTH_LONG).show();
	    	}
    	}
    	return opened;
	}
	
	
	//adapted from http://developer.android.com/training/camera/cameradirect.html
    private void releaseCamAndPrev(){ 	
    	if(cam != null){
    		cam.release();
    		cam = null;
    	}
    }
    
    //adapted from http://www.vogella.com/tutorials/AndroidCamera/article.html
    private int findRearFacingCamera(){
    	int camId = -1;
    	for (int i=0;i<Camera.getNumberOfCameras();i++){
    		CameraInfo info = new CameraInfo();
    		Camera.getCameraInfo(i, info);
    		if(info.facing == CameraInfo.CAMERA_FACING_BACK){
    			camId = i;
    			Log.i("JEM","camId="+camId);
    			break;
    		}
    	}
    	return camId;
    }
	
    
    public void surfaceClicked(View view){
    	cam.takePicture(null,null,new PhotoHandler(this,song));
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	releaseCamAndPrev();
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	hasOpenedCamSafely(findRearFacingCamera());
    }
    
    @Override
	public void onBackPressed() {
	   Intent intent = new Intent(getApplicationContext(),SetSongDetailsActivity.class);
	   startActivity(intent);
	 }

	/******************SURFACEHOLDER SPECIFIC******************************/
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try{
			cam.setPreviewDisplay(holder);
			cam.startPreview();
		}
		catch(IOException ioex){
			Toast.makeText(getApplicationContext(), "problem showing the camera preview", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamAndPrev();
	}

}
