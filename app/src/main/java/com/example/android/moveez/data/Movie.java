package com.example.android.moveez.data;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Each Movie object holds the details to describe a given movie, including:
 * - Title             (title)        (String)
 * - Status            (status)       (String)
 * - Release date      (release_date) (String)
 * - Home page         (homepage)     (String, or null)
 * - Movie cover image (poster_path)  (String, or null)
 * - Tagline           (tagline)      (String, or null)
 * - ID                (id)           (int)
 * - Budget            (budget)       (int)
 * - Revenue           (revenue)      (int)
 * - Number of ratings (vote_count)   (int)
 * - Duration          (runtime)      (int, or null)
 * - Overall rating    (vote_average) (double)
 * - Is adult film?    (adult)        (boolean)
 */

public final class Movie implements Serializable {

    // Movie title
    @NonNull private String title;

    // Movie's current status
    @NonNull private String status;

    // When the movie was released
    @NonNull private String releaseDate;

    // The movie's home page/site
    private String homePage;

    // The movie's cover image/photo
    private String posterPath;

    // The movie's tagline
    private String overview;

    // The movie's unique ID
    private final int id; // Should never change after set for first time

    // The budget the movie had
    private int budget;

    // The movie's current revenue (how much did it gross?)
    private int revenue;

    // The number of ratings the movie has received
    private int voteCount;

    // The overall rating the movie currently holds
    private double voteAverage;

    // The movie's duration
    private int runtime;

    // Whether the movies is an adult film or not
    private boolean adult;

    public Movie(@NonNull String title, @NonNull String status, @NonNull String releaseDate,
          String homePage, String posterPath, String overview, int id, int budget,
          int revenue, int voteCount, double voteAverage, int runtime, boolean adult) {
        this.title = title;
        this.status = status;
        this.releaseDate = releaseDate;
        this.homePage = homePage;
        this.posterPath = posterPath;
        this.overview = overview;
        this.id = id;
        this.budget = budget;
        this.revenue = revenue;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.runtime = runtime;
        this.adult = adult;
    }

    public Movie(int id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    // Setters for the non-final fields
    public void setTitle(@NonNull String title) { this.title = title; }
    public void setStatus(@NonNull String status) { this.status = status; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setHomePage(String homePage) { this.homePage = homePage; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setOverview(String tagline) { this.overview = tagline; }
    public void setRevenue(int revenue) { this.revenue = revenue; }
    public void setBudget(int budget) { this.budget = budget; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }
    public void setRuntime(int runtime) { this.runtime = runtime; }
    public void setAdult(boolean adult) { this.adult = adult; }

    // Getters
    public @NonNull String getTitle() { return title; }
    public @NonNull String getStatus() { return status; }
    public @NonNull String getReleaseDate() { return releaseDate; }
    public String getHomePage() { return homePage; }
    public String getPosterPath() { return posterPath; }
    public String getOverview() { return overview; }
    public int getId() { return id; }
    public int getBudget() { return budget; }
    public int getRevenue() { return revenue; }
    public int getVoteCount() { return voteCount; }
    public double getVoteAverage() { return voteAverage; }
    public int getRuntime() { return runtime; }

    public boolean isAdult() { return adult; }
}
