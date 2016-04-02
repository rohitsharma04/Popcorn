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
public class VideoServiceResponse implements Parcelable {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("results")
    @Expose
    public List<Video> videos = new ArrayList<Video>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeTypedList(videos);
    }

    public VideoServiceResponse() {
    }

    protected VideoServiceResponse(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.videos = in.createTypedArrayList(Video.CREATOR);
    }

    public static final Creator<VideoServiceResponse> CREATOR = new Creator<VideoServiceResponse>() {
        @Override
        public VideoServiceResponse createFromParcel(Parcel source) {
            return new VideoServiceResponse(source);
        }

        @Override
        public VideoServiceResponse[] newArray(int size) {
            return new VideoServiceResponse[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
