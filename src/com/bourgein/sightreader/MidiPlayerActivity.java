package com.bourgein.sightreader;

import java.io.IOException;

import android.R.color;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff;
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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MidiPlayerActivity extends MenuDefiner implements SeekBar.OnSeekBarChangeListener {

	private TextView txtViewName;
	private ImageButton btnStart;
	private ImageButton btnStop;
	private SeekBar seekBar;
	private Handler mHandler;
	private MediaPlayer mediaPlayer;
	private Uri midiUri;
	private Song song;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_midi_player);
		
		init();
		
	}
	
	public void init(){
		Bundle bundle = getIntent().getExtras();
		song = bundle.getParcelable(Song.SONG_PARCEL);
		boolean isLoadingFromServer = bundle.getBoolean(ServerHelper.LOADING_FROM_SERVER);
		int curStatus = bundle.getInt(ServerHelper.CUR_STATUS);
		boolean isLoadingFromNotification = bundle.getBoolean(ServerHelper.LOADING_FROM_NOTICATION);
		Log.i("JEM","current status in init: "+curStatus);
		
		btnStart = (ImageButton)findViewById(R.id.midi_btn_play);
		btnStop = (ImageButton)findViewById(R.id.midi_btn_stop);
		txtViewName = (TextView)findViewById(R.id.midi_text_name);
		seekBar = (SeekBar)findViewById(R.id.midi_seek_bar);
		
		seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY));
		
		mHandler = new Handler();
		if(checkServerStatus(curStatus)){
			txtViewName.setText(song.getName());
			setUpMedia();
		}
		else{
			ConversionErrorDialog diog = new ConversionErrorDialog();
			diog.show(getFragmentManager(),"error");
		}
	}
	
	public boolean checkServerStatus(int status){
		switch(status){
		case ServerHelper.STATUS_AUDIVERIS_PROBLEM:
			return false;
		case ServerHelper.STATUS_GENERAL_PROBLEM:
			return false;
		case ServerHelper.STATUS_XML_PROBLEM:
			return false;
		case ServerHelper.STATUS_OK:
			return true;
		default:
			return false;	
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
						playMidi();
					}
					catch(IllegalStateException ex){
						Log.i("JEM","can't play audio: "+ex.getMessage());
					} catch (IOException e) {
						Log.i("JEM","can't prepare audio: "+e.getMessage());
					}
					
				}
			});
			
			btnStop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try{
						mediaPlayer.pause();
					}
					catch(IllegalStateException ex){
						Log.i("JEM","can't stop audio: "+ex.getMessage());
					}
					
				}
			});
		}
	}
	
	private void playMidi() throws IllegalStateException, IOException{
		mediaPlayer.start();
		seekBar.setProgress(0);
		int songLength = mediaPlayer.getDuration()/1000;
		seekBar.setMax(songLength);
		updateProgressBar();
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		init();
	}
}
