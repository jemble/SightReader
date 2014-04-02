package com.bourgein.sightreader;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MidiPlayerActivity extends Activity implements ResultsListener, SeekBar.OnSeekBarChangeListener {

	private TextView txtViewName;
	private Button btnStart;
	private Button btnStop;
	private SeekBar seekBar;
	private Handler mHandler;
	private MediaPlayer mediaPlayer;
	private Uri midiUri;
	private Song song;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_midi_player);
		Bundle bundle = getIntent().getExtras();
		song = bundle.getParcelable(SetSongDetailsActivity.SONG_PARCEL);
		boolean isLoadingFromServer = bundle.getBoolean(ServerHelper.LOADING_FROM_SERVER);

		btnStart = (Button)findViewById(R.id.midi_btn_play);
		btnStop = (Button)findViewById(R.id.midi_btn_stop);
		txtViewName = (TextView)findViewById(R.id.midi_text_name);
		seekBar = (SeekBar)findViewById(R.id.midi_seek_bar);
		
		mHandler = new Handler();
		
		if(isLoadingFromServer){
			ServerHelper helper = new ServerHelper(this,song,this);
			helper.startComms();
		}
//		onServerResponse(song);
	}
	
	@Override
	public void onServerStart(){
		progressDialog= new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Getting midi ...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}
	
	public boolean checkServerStatus(int status){
		Log.i("JEM","status in midiplayer: "+status);
		switch(status){
		case ServerHelper.STATUS_AUDIVERIS_PROBLEM:
			return false;
		case ServerHelper.STATUS_GENERAL_PROBLEM:
			return false;
		case ServerHelper.STATUS_XML_PROBLEM:
			return false;
		default:
			return true;	
		}
	}
	
	private void setUpMedia(){
		midiUri = Uri.parse(song.getMidiFileName());
		if (midiUri != null){
			mediaPlayer = MediaPlayer.create(getApplicationContext(), midiUri);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			btnStart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try{
						mediaPlayer.start();
						seekBar.setProgress(0);
						int songLength = mediaPlayer.getDuration()/1000;
						seekBar.setMax(songLength);
						updateProgressBar();
					}
					catch(IllegalStateException ex){
						Log.i("JEM","can't play audio: "+ex.getMessage());
					}
					
				}
			});
			
			btnStop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try{
						mediaPlayer.stop();
					}
					catch(IllegalStateException ex){
						Log.i("JEM","can't stop audio: "+ex.getMessage());
					}
					
				}
			});
		}
	}
	
	
	protected void updateProgressBar() {
		mHandler.postDelayed(mUpdate, 100);
	}

	private Runnable mUpdate = new Runnable(){
		public void run(){
			int progress = mediaPlayer.getCurrentPosition()/1000;
			seekBar.setProgress(progress);
			mHandler.postDelayed(this, 100);
		}
	};
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdate);
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdate);
		if(mediaPlayer!=null){
			int totalDuration = mediaPlayer.getDuration();
			int curPos = mediaPlayer.getCurrentPosition();
			mediaPlayer.seekTo(curPos);
			updateProgressBar();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.midi_player, menu);
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServerResponse(Song song, int status) {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		if(checkServerStatus(status)){
			txtViewName.setText(song.getName());
			this.song = song;
			setUpMedia();
		}
		else{
			Toast.makeText(getApplicationContext(), "problem with the conversion. error code: "+status, Toast.LENGTH_LONG).show();
		}
		
	}

}
