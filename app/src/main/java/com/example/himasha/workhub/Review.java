package com.example.himasha.workhub;

/**
 * Created by Himasha on 9/6/2017.
 */

public class Review {
    private String reviewingUser;
    private String reviewedUser;
    private String review;

    public Review() {
    }

    public Review(String reviewingUser, String reviewedUser, String review) {
        this.reviewingUser = reviewingUser;
        this.reviewedUser = reviewedUser;
        this.review = review;
    }

    public String getReviewingUser() {
        return reviewingUser;
    }

    public void setReviewingUser(String reviewingUser) {
        this.reviewingUser = reviewingUser;
    }

    public String getReviewedUser() {
        return reviewedUser;
    }

    public void setReviewedUser(String reviewedUser) {
        this.reviewedUser = reviewedUser;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
