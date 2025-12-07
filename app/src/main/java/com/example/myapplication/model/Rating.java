package com.example.myapplication.model;

public class Rating {
    private String ratingId;
    private String userId;
    private String userName;
    private float stars;
    private String comment;
    private long timestamp;

    // No-argument constructor for Firebase
    public Rating() {}

    public Rating(String ratingId, String userId, String userName, float stars, String comment, long timestamp) {
        this.ratingId = ratingId;
        this.userId = userId;
        this.userName = userName;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Getters
    public String getRatingId() { return ratingId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public float getStars() { return stars; }
    public String getComment() { return comment; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setStars(float stars) { this.stars = stars; }
    public void setComment(String comment) { this.comment = comment; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
