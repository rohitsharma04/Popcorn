package com.bitshifters.rohit.popcorn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bitshifters.rohit.popcorn.adapter.InfiniteRecyclerOnScrollListener;
import com.bitshifters.rohit.popcorn.adapter.MovieAdapter;
import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MovieServiceResponse;
import com.bitshifters.rohit.popcorn.api.MoviesService;
import com.bitshifters.rohit.popcorn.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rohit on 29/3/16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARG_MOVIE_SERVICE_RESPONSE = "arg_movie_service_response";
    private static final int FIRST_PAGE = 1;
    private static final String LIST_POSITION = "listPosition";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.pbLoadingSpinner) ProgressBar progressBar;
    @Bind(R.id.movie_list) RecyclerView recyclerView;

    private boolean mTwoPane;
    private int mPosition = 0;

    private MovieAdapter mMovieAdapter;
    private MovieServiceResponse mMovieServiceResponse;
    private InfiniteRecyclerOnScrollListener mInfiniteRecyclerOnScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initializeEverything();

        //Setting up MasterFlow view for Large Tablet screens
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }

        //Restoring state on configuration change
        if(savedInstanceState != null && savedInstanceState.containsKey(ARG_MOVIE_SERVICE_RESPONSE)) {
            mMovieServiceResponse =
                    (MovieServiceResponse) savedInstanceState.getSerializable(ARG_MOVIE_SERVICE_RESPONSE);
            mMovieAdapter.changeDataSet(mMovieServiceResponse.getMovies());

            if(savedInstanceState.containsKey(ARG_MOVIE_SERVICE_RESPONSE)) {
                mPosition = savedInstanceState.getInt(LIST_POSITION);
            }

        }else{
            //fetch Movie from the api
            fetchMovies(FIRST_PAGE);
        }
    }

    private void initializeEverything(){
        //Initializing MovieServiceResponse Object
        mMovieServiceResponse = new MovieServiceResponse();
        mMovieServiceResponse.movies = new ArrayList<>();

        //Setting up Toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setLogo(R.mipmap.ic_launcher);
        //ToobarSubititle
        setToolbarSubtitle();

        //Setting up recycler view
        setupRecyclerView(recyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void changeMovieList(@MoviesService.SORT_BY String sortBy){
        //Setting old list to null because of preference change
        mMovieServiceResponse.setMovies(new ArrayList<Movie>());
        //Saving the new preference
        Utility.saveSortPreference(this,sortBy);
        //Resetting the InfiniteScrollListener
        mInfiniteRecyclerOnScrollListener.resetScrollSettings();
        //Fetching Movies for new Preference
        fetchMovies(FIRST_PAGE);
        setToolbarSubtitle();
        //resetting position
        setmPosition(0);
    }

    private void setToolbarSubtitle(){
        switch (Utility.getSortPreference(getApplication())){
            case MoviesService.SORT_BY_POPULAR:
                toolbar.setSubtitle(getResources().getString(R.string.sort_popularity));
                break;
            case MoviesService.SORT_BY_TOP_RATED:
                toolbar.setSubtitle(getResources().getString(R.string.sort_rating));
                break;
            case MoviesService.SORT_BY_NOW_PLAYING:
                toolbar.setSubtitle(getResources().getString(R.string.sort_now_playing));
                break;
            case MoviesService.SORT_BY_UPCOMING:
                toolbar.setSubtitle(getResources().getString(R.string.sort_upcoming));
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popularity:
                changeMovieList(MoviesService.SORT_BY_POPULAR);
                break;
            case R.id.action_sort_rating:
                changeMovieList(MoviesService.SORT_BY_TOP_RATED);
                break;
            case R.id.action_sort_now_playing:
                changeMovieList(MoviesService.SORT_BY_NOW_PLAYING);
                break;
            case R.id.action_sort_upcoming:
                changeMovieList(MoviesService.SORT_BY_UPCOMING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_MOVIE_SERVICE_RESPONSE, mMovieServiceResponse);
        outState.putInt(LIST_POSITION, mPosition);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        GridLayoutManager layoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.movie_list_gridview_rows));

        //Implementing infinite scroll
        mInfiniteRecyclerOnScrollListener = new InfiniteRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                fetchMovies(current_page);
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(mInfiniteRecyclerOnScrollListener);

        mMovieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        recyclerView.setAdapter(mMovieAdapter);
    }

    private void fetchMovies(final int page){
        //Showing progress bar
        progressBar.setVisibility(View.VISIBLE);

        @MoviesService.SORT_BY
        String sortBy = Utility.getSortPreference(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesService moviesService = retrofit.create(MoviesService.class);
        Call<MovieServiceResponse> call = moviesService.movieList(sortBy, MoviesService.API_KEY, page);
        call.enqueue(new Callback<MovieServiceResponse>() {

            @Override
            public void onResponse(Call<MovieServiceResponse> call, Response<MovieServiceResponse> response) {
                List<Movie> movieList = new ArrayList<Movie>();
                if (response.body() != null) {
                    movieList = response.body().getMovies();
                }
                //Saving the movies data for restoring instance
                mMovieServiceResponse.movies.addAll(movieList);
                if (movieList != null) {
                    if (page == FIRST_PAGE)
                        mMovieAdapter.changeDataSet(movieList);
                    else
                        mMovieAdapter.addDataSet(movieList);
                }
                //Hiding progress bar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieServiceResponse> call, Throwable t) {
                //Hiding progress bar
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Failed to Fetch Movies", Toast.LENGTH_LONG).show();
            }
        });
    }

    //For Using when state is restored
    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public boolean ismTwoPane() {
        return mTwoPane;
    }

}
