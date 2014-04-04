package com.bourgein.sightreader;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

	public static final String SONG_PARCEL = "SONG_PARCEL";
	
	String name;
	int tempo;
	String fileName;
	String imageFileName;
	String midiFileName;
	
	public Song(String name, int tempo){
		this.name = name;
		this.tempo = tempo;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
		createFileName();
		createMidiFileName();
	}

	public String getMidiFileName() {
		return midiFileName;
	}

	public void setMidiFileName(String midiFileName) {
		this.midiFileName = midiFileName;
	}
	
	private void createFileName(){
		String[] splitOne = imageFileName.split("srf");
		String[] splitTwo = splitOne[1].split(".jpg");
		fileName = "srf"+splitTwo[0];
	}
	
	private void createMidiFileName(){
		String[] fileSplit = imageFileName.split(".jpg");
		this.midiFileName = fileSplit[0]+".midi"; 
	}

	/********************PARCELABLE SPECIFIC*************************/
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(tempo);
		dest.writeString(fileName);
		dest.writeString(imageFileName);
		dest.writeString(midiFileName);
	}
	
	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {

		@Override
		public Song createFromParcel(Parcel source) {
			return new Song(source);
		}

		@Override
		public Song[] newArray(int size) {
			return new Song[size];
		}
		
	};
	
	private Song(Parcel in){
		//same order as writeToParcel()
		name = in.readString();
		tempo = in.readInt();
		fileName = in.readString();
		imageFileName = in.readString();
		midiFileName = in.readString();
	}

}
