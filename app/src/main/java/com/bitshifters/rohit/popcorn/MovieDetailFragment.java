package com.bitshifters.rohit.popcorn;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohit on 29/3/16.
 */

public class MovieDetailFragment extends Fragment {
    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "arg_movie";

    private Movie mMovie;

    @Bind(R.id.tvTitle) TextView title;
    @Bind(R.id.tvReleaseDate) TextView releaseDate;
    @Bind(R.id.tvOverview) TextView overview;
    @Bind(R.id.tvVote) TextView voteAverageText;
    @Bind(R.id.rbVote) RatingBar voteAverage;
    @Bind(R.id.ivPosterPortrait) ImageView posterPortrait;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {

            mMovie = (Movie) getArguments().getParcelable(ARG_MOVIE);

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

        //Binding Views
        ButterKnife.bind(this, rootView);

        //Setting values
        if (mMovie != null) {
            title.setText(mMovie.getTitle());
            releaseDate.setText(Utility.getFormattedDate(mMovie.getReleaseDate()));
            overview.setText(mMovie.getOverview());
            voteAverage.setRating(mMovie.getVoteAverage() / 2);
            voteAverageText.setText(getResources().getString(R.string.rating, mMovie.getVoteAverage()));

            Picasso.with(rootView.getContext())
                    .load(Utility.getPortraitPosterUrl(getActivity(),mMovie.getPosterPath()))
                    .error(R.drawable.portrait_poster_not_found)
                    .into(posterPortrait);

        }
        return rootView;
    }

}
