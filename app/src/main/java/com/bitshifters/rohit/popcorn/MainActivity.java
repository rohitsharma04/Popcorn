package com.bitshifters.rohit.popcorn;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bitshifters.rohit.popcorn.adapter.InfiniteRecyclerOnScrollListener;
import com.bitshifters.rohit.popcorn.adapter.MovieAdapter;
import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MovieDbOrgApiService;
import com.bitshifters.rohit.popcorn.api.MovieServiceResponse;
import com.bitshifters.rohit.popcorn.data.MovieTableMeta;
import com.bitshifters.rohit.popcorn.data.MovieProvider;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARG_MOVIE_SERVICE_RESPONSE = "arg_movie_service_response";
    private static final int FIRST_PAGE = 1;
    private static final String LIST_POSITION = "listPosition";
    public static final int FAVORITE_MOVIES_LOADER = 0;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.pbProgressBar) ProgressBar progressBar;
    @Bind(R.id.movie_list) RecyclerView recyclerView;
    @Bind(R.id.search_view) MaterialSearchView searchView;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.nav_view) NavigationView navigationView;

    private boolean mTwoPane;
    private int mPosition = 0;
    private boolean isSearch = false;

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
                    (MovieServiceResponse) savedInstanceState.getParcelable(ARG_MOVIE_SERVICE_RESPONSE);
            mMovieAdapter.changeDataSet(mMovieServiceResponse.getMovies());

            if(savedInstanceState.containsKey(ARG_MOVIE_SERVICE_RESPONSE)) {
                mPosition = savedInstanceState.getInt(LIST_POSITION);
            }

        }else{
            if (Utility.getSortPreference(this).equals(MovieDbOrgApiService.SORT_BY_FAVORITE)){
                getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
            }else {
                //fetch Movie from the api
                fetchMoviesBySortType(FIRST_PAGE);
            }
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

        //Setting Search View
        setupSearchView();

        setupDrawerLayout();

    }

    private void setupDrawerLayout() {
        //Setting up the Navigation Drawer
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        switch (Utility.getSortPreference(this)){
            case MovieDbOrgApiService.SORT_BY_POPULAR:
                navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);
                break;
            case MovieDbOrgApiService.SORT_BY_TOP_RATED:
                navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setChecked(true);
                break;
            case MovieDbOrgApiService.SORT_BY_NOW_PLAYING:
                navigationView.getMenu().getItem(0).getSubMenu().getItem(2).setChecked(true);
                break;
            case MovieDbOrgApiService.SORT_BY_UPCOMING:
                navigationView.getMenu().getItem(0).getSubMenu().getItem(3).setChecked(true);
                break;
            case MovieDbOrgApiService.SORT_BY_FAVORITE:
                navigationView.getMenu().getItem(0).getSubMenu().getItem(4).setChecked(true);
                break;
        }
    }

    private void setupSearchView() {
        searchView.setEllipsize(true);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearch = true;
                searchView.closeSearch();
                mMovieServiceResponse.setMovies(new ArrayList<Movie>());
                mMovieAdapter.changeDataSet(new ArrayList<Movie>());
                //Resetting the InfiniteScrollListener
                mInfiniteRecyclerOnScrollListener.resetScrollSettings();
                recyclerView.removeOnScrollListener(mInfiniteRecyclerOnScrollListener);
                searchMovies(query);
                toolbar.setSubtitle("Search Results for " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                searchView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    public void changeMovieList(@MovieDbOrgApiService.SORT_BY String sortBy){
        getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        isSearch = false;
        //Setting old list to null because of preference change
        mMovieServiceResponse.setMovies(new ArrayList<Movie>());
        mMovieAdapter.changeDataSet(new ArrayList<Movie>());
        //Saving the new preference
        Utility.saveSortPreference(this, sortBy);
        //Resetting the InfiniteScrollListener
        mInfiniteRecyclerOnScrollListener.resetScrollSettings();

        if(sortBy.equals(MovieDbOrgApiService.SORT_BY_FAVORITE)){
            recyclerView.removeOnScrollListener(mInfiniteRecyclerOnScrollListener);
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        }else {
            //Fetching Movies for new Preference
            recyclerView.addOnScrollListener(mInfiniteRecyclerOnScrollListener);
            fetchMoviesBySortType(FIRST_PAGE);
        }
        setToolbarSubtitle();
        //resetting position
        setmPosition(0);
    }


    private void setToolbarSubtitle(){

        switch (Utility.getSortPreference(getApplication())){
            case MovieDbOrgApiService.SORT_BY_POPULAR:
                toolbar.setSubtitle(getResources().getString(R.string.sort_popular));
                break;
            case MovieDbOrgApiService.SORT_BY_TOP_RATED:
                toolbar.setSubtitle(getResources().getString(R.string.sort_top_rated));
                break;
            case MovieDbOrgApiService.SORT_BY_NOW_PLAYING:
                toolbar.setSubtitle(getResources().getString(R.string.sort_now_playing));
                break;
            case MovieDbOrgApiService.SORT_BY_UPCOMING:
                toolbar.setSubtitle(getResources().getString(R.string.sort_upcoming));
                break;
            case MovieDbOrgApiService.SORT_BY_FAVORITE:
                toolbar.setSubtitle(getResources().getString(R.string.sort_favorite));
                break;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                changeMovieList(MovieDbOrgApiService.SORT_BY_POPULAR);
                break;
            case R.id.action_sort_top_rated:
                changeMovieList(MovieDbOrgApiService.SORT_BY_TOP_RATED);
                break;
            case R.id.action_sort_now_playing:
                changeMovieList(MovieDbOrgApiService.SORT_BY_NOW_PLAYING);
                break;
            case R.id.action_sort_upcoming:
                changeMovieList(MovieDbOrgApiService.SORT_BY_UPCOMING);
                break;
            case R.id.action_sort_favorite:
//                Toast.makeText(this,"Favorite Selected",Toast.LENGTH_SHORT).show();
                changeMovieList(MovieDbOrgApiService.SORT_BY_FAVORITE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_MOVIE_SERVICE_RESPONSE, mMovieServiceResponse);
        outState.putInt(LIST_POSITION, mPosition);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        GridLayoutManager layoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.movie_list_gridview_rows));

        //Implementing infinite scroll
        mInfiniteRecyclerOnScrollListener = new InfiniteRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                fetchMoviesBySortType(current_page);
            }
        };

        recyclerView.setLayoutManager(layoutManager);

        if (!Utility.getSortPreference(this).equals(MovieDbOrgApiService.SORT_BY_FAVORITE)) {
            recyclerView.addOnScrollListener(mInfiniteRecyclerOnScrollListener);
        }

        mMovieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        recyclerView.setAdapter(mMovieAdapter);
    }

    private void fetchMoviesBySortType(final int page){
        Log.v(TAG, "Fetch Movies");
        //Showing progress bar
        progressBar.setVisibility(View.VISIBLE);

        @MovieDbOrgApiService.SORT_BY
        String sortBy = Utility.getSortPreference(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbOrgApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDbOrgApiService movieDbOrgApiService = retrofit.create(MovieDbOrgApiService.class);

        Call<MovieServiceResponse> call= movieDbOrgApiService.movieList(sortBy, MovieDbOrgApiService.API_KEY, page);
        call.enqueue(new Callback<MovieServiceResponse>() {

            @Override
            public void onResponse(Call<MovieServiceResponse> call, Response<MovieServiceResponse> response) {
                List<Movie> movieList = new ArrayList<Movie>();
                if (response.body() != null) {
                    movieList = response.body().getMovies();
                }
                //Saving the movies data for restoring instance
                mMovieServiceResponse.movies.addAll(movieList);
                if (!movieList.isEmpty()) {
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
                Log.e(TAG, "Failed to Fetch Movies");
            }
        });

    }

    private void searchMovies(String query){
        Log.v(TAG, "Search Movies");
        //Showing progress bar
        progressBar.setVisibility(View.VISIBLE);

        @MovieDbOrgApiService.SORT_BY
        String sortBy = Utility.getSortPreference(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbOrgApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDbOrgApiService movieDbOrgApiService = retrofit.create(MovieDbOrgApiService.class);

        Call<MovieServiceResponse> call= movieDbOrgApiService.searchResult(MovieDbOrgApiService.API_KEY, query);
        call.enqueue(new Callback<MovieServiceResponse>() {

            @Override
            public void onResponse(Call<MovieServiceResponse> call, Response<MovieServiceResponse> response) {
                List<Movie> movieList = new ArrayList<Movie>();
                if (response.body() != null) {
                    movieList = response.body().getMovies();
                }
                //Saving the movies data for restoring instance
                mMovieServiceResponse.movies.clear();
                mMovieServiceResponse.movies.addAll(movieList);
                if (!movieList.isEmpty()) {
                    mMovieAdapter.changeDataSet(movieList);
                }
                //Hiding progress bar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieServiceResponse> call, Throwable t) {
                //Hiding progress bar
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to Search Movies");
            }
        });

    }

    private void fetchMovieFromDb(Cursor cursor) {
        Log.v(TAG,"Fetch Movies From DB");
        //Showing progress bar
        progressBar.setVisibility(View.VISIBLE);

        List<Movie> movies = new ArrayList<>();
        if(cursor != null) {
            while (cursor.moveToNext()) {
                String posterPath = cursor.getString(MovieTableMeta.POSTER_PATH_ID);
                String overview = cursor.getString(MovieTableMeta.OVERVIEW_ID);
                String releaseDate = cursor.getString(MovieTableMeta.RELEASE_DATE_ID);
                Integer id = cursor.getInt(MovieTableMeta.ID_ID);
                String title = cursor.getString(MovieTableMeta.TITLE_ID);
                String backdropPath = cursor.getString(MovieTableMeta.BACKDROP_ID);
                Float popularity = cursor.getFloat(MovieTableMeta.POPULARITY_ID);
                Integer voteCount = cursor.getInt(MovieTableMeta.VOTE_COUNT_ID);
                Float voteAverage = cursor.getFloat(MovieTableMeta.VOTE_AVERAGE_ID);
                movies.add(new Movie(posterPath, overview, releaseDate, id,
                        title, backdropPath, popularity, voteCount, voteAverage));
            }
            //Note to self : Never Close this Cursor Otherwise You'll waste 16 Hours
            //Trying to figure out why data monitoring isn't happening.
//            cursor.close();
        }
        mMovieServiceResponse.setMovies(movies);
        //Updating the adapter
        mMovieAdapter.changeDataSet(movies);
        //Hiding progress bar
        progressBar.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }else if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            hideDrawer();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG,"onCreateLoader");
        return new CursorLoader(this, MovieProvider.buildUri(MovieProvider.Path.MOVIES), MovieTableMeta.COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG,"onLoadFinished");
        fetchMovieFromDb(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "onLoaderReset");
    }

    // Open the Drawer
    private void showDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // Close the Drawer
    private void hideDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.v(TAG, "onNavigationItemSelected");
        hideDrawer();
        if(item.getItemId() != R.id.item_about) {
            for (int i = 0; i < 5; i++) {
                navigationView.getMenu().getItem(0).getSubMenu().getItem(i).setChecked(false);
            }
            item.setChecked(true);
        }
        switch (item.getItemId()){
            case R.id.item_popular:
                changeMovieList(MovieDbOrgApiService.SORT_BY_POPULAR);
                break;
            case R.id.item_top_rated:
                changeMovieList(MovieDbOrgApiService.SORT_BY_TOP_RATED);
                break;
            case R.id.item_now_playing:
                changeMovieList(MovieDbOrgApiService.SORT_BY_NOW_PLAYING);
                break;
            case R.id.item_upcoming:
                changeMovieList(MovieDbOrgApiService.SORT_BY_UPCOMING);
                break;
            case R.id.item_favorite:
//                Toast.makeText(this,"Favorite Selected",Toast.LENGTH_SHORT).show();
                changeMovieList(MovieDbOrgApiService.SORT_BY_FAVORITE);
                break;
            case R.id.item_about:
                Toast.makeText(this, "Open About App Page", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
