package com.bitshifters.rohit.popcorn.api;

import android.support.annotation.StringDef;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rohit on 29/3/16.
 */
public interface MovieDbOrgApiService {

    public static final String API_KEY = "API_KEY_HERE";

    public static final String API_BASE_URL = "http://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String SORT_BY_NOW_PLAYING = "now_playing";
    public static final String SORT_BY_UPCOMING = "upcoming";

    @StringDef({SORT_BY_POPULAR, SORT_BY_TOP_RATED, SORT_BY_NOW_PLAYING, SORT_BY_UPCOMING})
    public @interface SORT_BY {
    }

    //Provides List of Movies Sorted by Some Category
    @GET("movie/{sort_by}")
    Call<MovieServiceResponse> movieList(
            @Path("sort_by") String sortBy,
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    //Provides Movie Trailer and other video clips related to movie
    @GET("movie/{movie_id}/videos")
    Call<VideoServiceResponse> videoList(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

}
