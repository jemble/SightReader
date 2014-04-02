package com.bourgein.sightreader;

public interface ResultsListener {
	public void onServerStart();
	public void onServerResponse(Song song, int status);
}