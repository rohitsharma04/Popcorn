package com.bitshifters.rohit.popcorn.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 2/4/16.
 */
public class ReviewServiceResponse implements Parcelable {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("page")
    @Expose
    public Integer page;

    @SerializedName("results")
    @Expose
    public List<Review> reviews = new ArrayList<>();

    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;

    @SerializedName("total_results")
    @Expose
    public Integer totalResults;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.page);
        dest.writeTypedList(reviews);
        dest.writeValue(this.totalPages);
        dest.writeValue(this.totalResults);
    }

    public ReviewServiceResponse() {
    }

    protected ReviewServiceResponse(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.reviews = in.createTypedArrayList(Review.CREATOR);
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ReviewServiceResponse> CREATOR = new Parcelable.Creator<ReviewServiceResponse>() {
        @Override
        public ReviewServiceResponse createFromParcel(Parcel source) {
            return new ReviewServiceResponse(source);
        }

        @Override
        public ReviewServiceResponse[] newArray(int size) {
            return new ReviewServiceResponse[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
}
