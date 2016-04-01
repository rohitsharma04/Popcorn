package com.bitshifters.rohit.popcorn.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 29/3/16.
 */
public class MovieServiceResponse implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.page);
        dest.writeTypedList(movies);
        dest.writeValue(this.totalResults);
        dest.writeValue(this.totalPages);
    }

    public MovieServiceResponse() {
    }

    protected MovieServiceResponse(Parcel in) {
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.movies = in.createTypedArrayList(Movie.CREATOR);
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<MovieServiceResponse> CREATOR = new Parcelable.Creator<MovieServiceResponse>() {
        @Override
        public MovieServiceResponse createFromParcel(Parcel source) {
            return new MovieServiceResponse(source);
        }

        @Override
        public MovieServiceResponse[] newArray(int size) {
            return new MovieServiceResponse[size];
        }
    };
}
