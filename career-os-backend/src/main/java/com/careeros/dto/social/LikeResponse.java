package com.careeros.dto.social;

/**
 * DTO for like response 
 */
public class LikeResponse {
    private boolean liked;
    private boolean isLiked;
    private int totalLikes;
    private String message;
    
    public LikeResponse() {
    }
    
    public LikeResponse(boolean liked, int totalLikes, String message) {
        this.liked = liked;
        this.isLiked = liked;
        this.totalLikes = totalLikes;
        this.message = message;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setIsLiked(boolean isLiked) {
        this.liked = isLiked;
        this.isLiked = isLiked;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
        this.isLiked = liked;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
