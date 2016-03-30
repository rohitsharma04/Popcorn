package com.bitshifters.rohit.popcorn;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MoviesService;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by rohit on 29/3/16.
 */

public class MovieDetailFragment extends Fragment {
    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "arg_movie";

    private Movie mMovie;

    private TextView mTitle,mReleaseDate,mOverview,mVoteAverageText;
    private RatingBar mVoteAverage;
    private ImageView mPosterPortrait;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
//            Log.v(TAG,"mMovie :"+getArguments().getSerializable(ARG_MOVIE));
            mMovie = (Movie) getArguments().getSerializable(ARG_MOVIE);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mMovie != null) {
                appBarLayout.setTitle(mMovie.getTitle());
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        //Initializing Widgets
        mTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        mReleaseDate = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        mOverview = (TextView) rootView.findViewById(R.id.tvOverview);
        mVoteAverageText = (TextView) rootView.findViewById(R.id.tvVote);
        mVoteAverage = (RatingBar) rootView.findViewById(R.id.rbVote);
        mPosterPortrait = (ImageView) rootView.findViewById(R.id.ivPosterPortrait);

        if (mMovie != null) {
            //Setting values
            mTitle.setText(mMovie.getTitle());
            mReleaseDate.setText(Utility.getFormattedDate(mMovie.getReleaseDate()));
            mOverview.setText(mMovie.getOverview());
            mVoteAverage.setRating(mMovie.getVoteAverage() / 2);
            mVoteAverageText.setText(getResources().getString(R.string.rating, mMovie.getVoteAverage()));

            Picasso.with(rootView.getContext())
                    .load(Utility.getPortraitPosterUrl(getActivity(),mMovie.getPosterPath()))
                    .error(R.drawable.portrait_poster_not_found)
                    .into(mPosterPortrait);

        }

        return rootView;
    }
}
