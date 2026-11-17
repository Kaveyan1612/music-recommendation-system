package com.recommendation.model;

public class Rating {
    private final int userId;
    private final int trackId;
    private final float rating; // 1.0 to 5.0

    public Rating(int userId, int trackId, float rating) {
        this.userId = userId;
        this.trackId = trackId;
        this.rating = rating;
    }

    public int getUserId() { return userId; }
    public int getTrackId() { return trackId; }
    public float getRating() { return rating; }
}
