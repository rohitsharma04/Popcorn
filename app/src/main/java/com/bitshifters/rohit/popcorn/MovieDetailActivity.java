package com.bitshifters.rohit.popcorn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MoviesService;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mPosterLandscape;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState != null && savedInstanceState.containsKey(MovieDetailFragment.ARG_MOVIE)) {
            mMovie = (Movie) savedInstanceState.getSerializable(MovieDetailFragment.ARG_MOVIE);
        }else{
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            mMovie = (Movie) getIntent().getSerializableExtra(MovieDetailFragment.ARG_MOVIE);

            Bundle arguments = new Bundle();
            arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, mMovie);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

        mPosterLandscape = (ImageView) findViewById(R.id.ivPosterLandscape);

        Picasso.with(getApplicationContext())
                .load(MoviesService.IMAGE_BASE_URL + "w342/" + mMovie.getBackdropPath())
                .into(mPosterLandscape);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MovieDetailFragment.ARG_MOVIE,mMovie);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
