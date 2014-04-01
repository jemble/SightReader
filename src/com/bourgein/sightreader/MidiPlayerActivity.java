package com.bourgein.sightreader;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MidiPlayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_midi_player);
		Bundle bundle = getIntent().getExtras();
		Song song = bundle.getParcelable(SetSongDetailsActivity.SONG_PARCEL);
		boolean isLoadingFromServer = bundle.getBoolean(ServerHelper.LOADING_FROM_SERVER);
		Log.i("JEM","loading from server: "+isLoadingFromServer);
		if(isLoadingFromServer){
			ServerHelper helper = new ServerHelper(this,song);
			helper.startComms();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.midi_player, menu);
		return true;
	}

}
