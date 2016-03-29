package com.bitshifters.rohit.popcorn.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 29/3/16.
 */
public class Movie implements Serializable {

    @SerializedName("poster_path")
    @Expose
    public String posterPath;

    @SerializedName("adult")
    @Expose
    public Boolean adult;

    @SerializedName("overview")
    @Expose
    public String overview;

    @SerializedName("release_date")
    @Expose
    public String releaseDate;

    @SerializedName("genre_ids")
    @Expose
    public List<Integer> genreIds = new ArrayList<Integer>();

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("original_title")
    @Expose
    public String originalTitle;

    @SerializedName("original_language")
    @Expose
    public String originalLanguage;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;

    @SerializedName("popularity")
    @Expose
    public Float popularity;

    @SerializedName("vote_count")
    @Expose
    public Integer voteCount;

    @SerializedName("video")
    @Expose
    public Boolean video;

    @SerializedName("vote_average")
    @Expose
    public Float voteAverage;

    public String toString(){
        return "Movie : "+ title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

}

