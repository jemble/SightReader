package com.bourgein.sightreader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {
	
	Button btnStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		btnStart = (Button)findViewById(R.id.btn_start);
	}

	/**
	 * Handles start button clicked
	 * Starts the SetSongDetailsActivity
	 * @param view
	 */
	public void btnStartClicked(View view){
		Intent songDetailsIntent = new Intent(getApplicationContext(),SetSongDetailsActivity.class);
		startActivity(songDetailsIntent);
	}
	
	@Override
	public void onBackPressed() {
	   Intent intent = new Intent(Intent.ACTION_MAIN);
	   intent.addCategory(Intent.CATEGORY_HOME);
	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   startActivity(intent);
	 }

}
