package com.bourgein.sightreader;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetSongDetailsActivity extends Activity {

	Button btnCamera;
	EditText editName;
	EditText editTempo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_song_details);
		
		btnCamera = (Button)findViewById(R.id.setSongs_btn_camera);
		editName = (EditText)findViewById(R.id.setSongs_editTxt_name);
		editTempo = (EditText)findViewById(R.id.setSongs_editTxt_tempo);
		
	}

	private String getInputValues(View input){
		String val = "";
		val = ((TextView)input).getText().toString();
		return val;
	}
	
	private boolean checkFormValues(){
		String nameVal = getInputValues(editName);
		Log.i("JEM","naeVal: "+nameVal);
		if(nameVal.equalsIgnoreCase("")){
			return false;
		}
		
		String tempoVal = getInputValues(editTempo);
		int tempo;
		try{
			tempo = Integer.parseInt(tempoVal);
		}
		catch(NumberFormatException ex){
			return false;
		}
		
		return true;
	}
	
	/**
	 * handles the camera button click. Gets the forms values, checks them, creates a new Song object, starts CapturePictureActivity
	 * @param view
	 */
	public void btnCameraClicked(View view){
		if(checkFormValues()){
			Log.i("JEM","vals OK");
		}
		else{
			Log.i("JEM","not OK");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_song_details, menu);
		return true;
	}

}
