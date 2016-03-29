package com.bitshifters.rohit.popcorn.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 29/3/16.
 */
public class MovieServiceResponse implements Serializable{

    @SerializedName("page")
    @Expose
    public Integer page;

    @SerializedName("results")
    @Expose
    public List<Movie> movies = new ArrayList<Movie>();

    @SerializedName("total_results")
    @Expose
    public Integer totalResults;

    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}
