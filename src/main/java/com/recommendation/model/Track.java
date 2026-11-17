package com.recommendation.model;

public class Track {
    private final int id;
    private final String title;
    private final String artist;
    private final String genre;

    public Track(int id, String title, String artist, String genre) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getGenre() { return genre; }

    @Override
    public String toString() {
        return String.format("[%d] %s — %s (%s)", id, title, artist, genre);
    }
}
