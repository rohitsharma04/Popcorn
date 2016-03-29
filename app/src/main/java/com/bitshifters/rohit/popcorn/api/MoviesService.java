package com.bitshifters.rohit.popcorn.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rohit on 29/3/16.
 */
public interface MoviesService {

    public static final String API_URL = "http://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String API_KEY = "API_KEY";

    @GET("movie/{sort_by}")
    Call<MovieServiceResponse> movieList(
            @Path("sort_by") String sortBy,
            @Query("api_key") String apiKey
    );
}
