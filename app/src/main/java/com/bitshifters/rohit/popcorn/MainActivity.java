package com.bitshifters.rohit.popcorn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bitshifters.rohit.popcorn.adapter.MovieAdapter;
import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MovieServiceResponse;
import com.bitshifters.rohit.popcorn.api.MoviesService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static final String TAG = MainActivity.class.getSimpleName();
    public boolean mTwoPane;
    MovieAdapter movieAdapter;
    private static final String ARG_MOVIE_SERVICE_RESPONSE = "arg_movie_service_response";
    private MovieServiceResponse mMovieServiceResponse;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final View recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        if(savedInstanceState != null && savedInstanceState.containsKey(ARG_MOVIE_SERVICE_RESPONSE)) {
            mMovieServiceResponse = (MovieServiceResponse) savedInstanceState.getSerializable(ARG_MOVIE_SERVICE_RESPONSE);
            movieAdapter.changeDataSet(mMovieServiceResponse.getMovies());
        }else{
            fetchMovies();
        }

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMovieServiceResponse = (MovieServiceResponse) savedInstanceState.getSerializable(ARG_MOVIE_SERVICE_RESPONSE);
        movieAdapter.changeDataSet(mMovieServiceResponse.getMovies());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_MOVIE_SERVICE_RESPONSE, mMovieServiceResponse);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        List<Movie> emptyList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(movieAdapter);
    }

    private void fetchMovies(){

        mProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesService moviesService = retrofit.create(MoviesService.class);
        Call<MovieServiceResponse> call = moviesService.movieList("popular", MoviesService.API_KEY);
        call.enqueue(new Callback<MovieServiceResponse>() {
            @Override
            public void onResponse(Call<MovieServiceResponse> call, Response<MovieServiceResponse> response) {
                mMovieServiceResponse = response.body();
                Log.v(TAG, "Success");

                Log.v(TAG, "Code :" + response.code());
                if (mMovieServiceResponse != null) {
                    for (Movie movie : mMovieServiceResponse.getMovies()) {
                        Log.v(TAG, movie.toString());
                    }
                    movieAdapter.changeDataSet(mMovieServiceResponse.getMovies());
                }

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieServiceResponse> call, Throwable t) {
                Log.e(TAG, "Failure");
                Log.e(TAG, t.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Failed to Fetch Movies",Toast.LENGTH_LONG).show();
            }
        });
    }


}
