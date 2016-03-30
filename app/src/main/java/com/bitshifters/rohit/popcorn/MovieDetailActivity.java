package com.bitshifters.rohit.popcorn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MoviesService;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by rohit on 29/3/16.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mPosterLandscape;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initializeEverything();

        //Restoring state of the Activity
        if (savedInstanceState != null && savedInstanceState.containsKey(MovieDetailFragment.ARG_MOVIE)) {
            mMovie = (Movie) savedInstanceState.getSerializable(MovieDetailFragment.ARG_MOVIE);

        }else{
            //Creating Fragment and adding it to the activity
            mMovie = (Movie) getIntent().getSerializableExtra(MovieDetailFragment.ARG_MOVIE);

            Bundle arguments = new Bundle();
            arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, mMovie);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

        if(mMovie != null && mPosterLandscape != null) {
            Picasso.with(getApplicationContext())
                    .load(Utility.getLandscapePosterUrl(this,mMovie.backdropPath))
                    .into(mPosterLandscape);
        }
    }

    private void initializeEverything(){
        //Setting up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
        }
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //connecting posterLandsscape Imageview
        mPosterLandscape = (ImageView) findViewById(R.id.ivPosterLandscape);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MovieDetailFragment.ARG_MOVIE,mMovie);
    }


}
