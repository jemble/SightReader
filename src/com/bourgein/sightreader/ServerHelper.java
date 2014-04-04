package com.bourgein.sightreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ServerHelper {
	
	public static final String LOADING_FROM_SERVER = "LOADING_FROM_SERVER"; 
	public static final String CUR_STATUS = "CUR STATUS";
	public static final String LOADING_FROM_NOTICATION = "LOADING_FROM_NOTIFICATION";
	private static final String SERVER_ADD = "54.229.110.104";
	private static final int SERVER_PORT = 1238;
	public static final int STATUS_OK = 100;
	public static final int STATUS_AUDIVERIS_PROBLEM = 101;
	public static final int STATUS_XML_PROBLEM = 102;
	public static final int STATUS_GENERAL_PROBLEM = 103;
	
	private int curStatus = STATUS_OK;
	
	private Context context;
	private ResultsListener listeningActivity;
	private Song song;
	
	public ServerHelper(Context context, Song song, ResultsListener resListener){
		this.listeningActivity = resListener;
		this.context = context;
		this.song = song;
	}
	
	public void startComms(){
		new CommsTask().execute();
	}
	
	private class CommsTask extends AsyncTask<Void, Integer, Integer>{

		private Socket connection;
		private ObjectOutputStream objOutputStream;
		private ObjectInputStream objInputStream;
		private Integer msgFromServer;
		
		@Override
		protected void onPreExecute(){
			listeningActivity.onServerStart();
		}
		@Override
		protected Integer doInBackground(Void... params) {
			//make the socket
			try {
				connection = new Socket(InetAddress.getByName(SERVER_ADD),SERVER_PORT);	
			} catch (UnknownHostException e) {
				curStatus = STATUS_GENERAL_PROBLEM;
				return curStatus;
			} catch (IOException e) {
				curStatus = STATUS_GENERAL_PROBLEM;
				return curStatus;
			}
			
			// create outputstream
			try {
				objOutputStream = new ObjectOutputStream(connection.getOutputStream());
			} catch (IOException e) {
				curStatus = STATUS_GENERAL_PROBLEM;;
			}
			
			//create inputstream
			try {
				objInputStream = new ObjectInputStream(connection.getInputStream());
			} catch (StreamCorruptedException e) {
				curStatus = STATUS_GENERAL_PROBLEM;
			} catch (IOException e) {
				curStatus = STATUS_GENERAL_PROBLEM;
			}
			
			//send teh filename
			try{
				sendMessage(objOutputStream, song.getFileName());
			}
			catch(IOException ex){
				curStatus = STATUS_GENERAL_PROBLEM;
			}
			
			//send the tempo
			try{
				sendMessage(objOutputStream,Integer.toString(song.getTempo()));
			}
			catch(IOException ex){
				curStatus = STATUS_GENERAL_PROBLEM;
			}
			
			//get the current status
			curStatus = receiveMessage(objInputStream);
			if(curStatus != STATUS_OK){
				return curStatus;
			}
			
			//send the jpg file to the server
			try {
				sendFile(song.getImageFileName(), objOutputStream);
			} catch (IOException e) {
				Toast.makeText(context, "couldn't send jpg to server", Toast.LENGTH_LONG).show();
			}
			
			//get file receive status
			curStatus = receiveMessage(objInputStream);	
			if(curStatus != STATUS_OK){
				return curStatus;
			}
			
			//get file processed message
			curStatus = receiveMessage(objInputStream);
			if(curStatus != STATUS_OK){
				return curStatus;
			}
			
			//get the MIDI file from server
			try {
				getFile(objInputStream);
			} catch (IOException e) {
				Toast.makeText(context, "couldn't get midi from server", Toast.LENGTH_LONG).show();
			} catch (ClassNotFoundException e) {
				Toast.makeText(context, "couldn't send midi from server", Toast.LENGTH_LONG).show();
			}
			return curStatus;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			
			String msg = "conversion done!";
			if(result!= STATUS_OK){
				msg = "conversion error";
			}
			
			Intent midiIntent = new Intent(context,MidiPlayerActivity.class);
			midiIntent.putExtra(LOADING_FROM_SERVER, false);
			midiIntent.putExtra(Song.SONG_PARCEL, song);
			midiIntent.putExtra(CUR_STATUS, curStatus);
			midiIntent.putExtra(LOADING_FROM_NOTICATION, true);
			PendingIntent midiPendingIntent = PendingIntent.getActivity(context, 0, midiIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("finished")
			.setContentText(msg)
			.setContentIntent(midiPendingIntent)
			.setAutoCancel(true);
			
			NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			mNotifyMgr.notify(0, mBuilder.build());
			
			listeningActivity.onServerResponse(song,result);
		}
	}

	public void sendMessage(ObjectOutputStream objOutputStream, String message) throws IOException {
		objOutputStream.writeObject(message);
	}

	public int receiveMessage(ObjectInputStream objInputStream) {
		int msg = -1;
		try {
			msg = objInputStream.readInt();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	public void sendFile(String imageFileName,ObjectOutputStream objOutputStream) throws IOException {
		FileInputStream fileIn;
		try{
			fileIn = new FileInputStream(song.imageFileName);
			long fileLen = new File(song.imageFileName).length();
			int intFileLen = (int)fileLen;
			byte[] byteArray = new byte[intFileLen];
			fileIn.read(byteArray);
			fileIn.close();
			objOutputStream.writeObject(byteArray);
		}
		catch(FileNotFoundException ex){
			Toast.makeText(context, "couldn'e send file to server", Toast.LENGTH_LONG).show();
		}
		objOutputStream.flush();
		
	}
	
	public void getFile(ObjectInputStream objInStream) throws IOException, ClassNotFoundException {
		byte[] byteArray = (byte[])objInStream.readObject();
        File midiFile = new File(song.getMidiFileName());
        FileOutputStream mediaStream = new FileOutputStream(midiFile.getPath());
        mediaStream.write(byteArray);
        mediaStream.close();
	}
}
