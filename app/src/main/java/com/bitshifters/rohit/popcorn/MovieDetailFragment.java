package com.bitshifters.rohit.popcorn;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitshifters.rohit.popcorn.adapter.ReviewAdapter;
import com.bitshifters.rohit.popcorn.adapter.VideoAdapter;
import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MovieDbOrgApiService;
import com.bitshifters.rohit.popcorn.api.Review;
import com.bitshifters.rohit.popcorn.api.ReviewServiceResponse;
import com.bitshifters.rohit.popcorn.api.Video;
import com.bitshifters.rohit.popcorn.api.VideoServiceResponse;
import com.bitshifters.rohit.popcorn.data.MovieColumns;
import com.bitshifters.rohit.popcorn.data.MovieProvider;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rohit on 29/3/16.
 */

public class MovieDetailFragment extends Fragment {
    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "arg_movie";
    private static final String ARG_VIDEO = "arg_video";
    private static final String ARG_REVIEW = "arg_review";

    private Movie mMovie;
    private Context mContext;
    private ArrayList<Video> videos;
    private ArrayList<Review> reviews;
    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;
    private ShareActionProvider shareActionProvider;
    private boolean isFavorite;

    @Bind(R.id.review_cardview) CardView reviewView;
    @Bind(R.id.videos_cardview) CardView videoView;

    @Bind(R.id.video_list) RecyclerView recyclerViewVideo;
    @Bind(R.id.review_list) RecyclerView recyclerViewReview;

    @Bind(R.id.tvTitle) TextView title;
    @Bind(R.id.tvReleaseDate) TextView releaseDate;
    @Bind(R.id.tvOverview) TextView overview;
    @Bind(R.id.tvVote) TextView voteAverageText;
    @Bind(R.id.rbVote) RatingBar voteAverage;
    @Bind(R.id.ivPosterPortrait) ImageView posterPortrait;
    @Bind(R.id.ibtnFavorite) ImageButton favoriteStar;
    @Bind(R.id.tvFavoriteText) TextView favoriteText;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = (Movie) getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        updateShareIntent();

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void updateShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,mMovie.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                Utility.getShareActionText(mMovie, videos, mContext));
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ARG_VIDEO, videos);
        outState.putParcelableArrayList(ARG_REVIEW, reviews);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && mMovie != null) {
            appBarLayout.setTitle(mMovie.getTitle());
        }


        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        mContext = rootView.getContext();
        //Binding Views
        ButterKnife.bind(this, rootView);

        //Setting values
        if (mMovie != null) {

            setRecyclerView();

            //Restoring State
            if(savedInstanceState != null){
                if(savedInstanceState.containsKey(ARG_VIDEO)){
                    videos =  savedInstanceState.getParcelableArrayList(ARG_VIDEO);
                    videoAdapter.addDataSet(videos);
                }else{
                    fetchVideos(mMovie.getId());
                }
                if(savedInstanceState.containsKey(ARG_REVIEW)){
                    reviews = savedInstanceState.getParcelableArrayList(ARG_REVIEW);
                    reviewAdapter.addDataSet(reviews);
                }else{
                    fetchReviews(mMovie.getId());
                }
            }else{
                fetchVideos(mMovie.getId());
                fetchReviews(mMovie.getId());
            }


            title.setText(mMovie.getTitle());
            releaseDate.setText(Utility.getFormattedDate(mMovie.getReleaseDate()));
            overview.setText(mMovie.getOverview());
            voteAverage.setRating(mMovie.getVoteAverage() / 2);
            voteAverageText.setText(getResources().getString(R.string.rating, mMovie.getVoteAverage()));

            Picasso.with(rootView.getContext())
                    .load(Utility.getPortraitPosterUrl(getActivity(),mMovie.getPosterPath()))
                    .error(R.drawable.portrait_poster_not_found)
                    .into(posterPortrait);

            setFavoriteButton();
        }

        return rootView;
    }

    private void setRecyclerView(){
        //Setting Video Adapter
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewVideo.setLayoutManager(manager);
        videoAdapter = new VideoAdapter(new ArrayList<Video>());
        recyclerViewVideo.setAdapter(videoAdapter);

        //Setting Review Adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewReview.setLayoutManager(linearLayoutManager);
        reviewAdapter = new ReviewAdapter(new ArrayList<Review>());
        recyclerViewReview.setAdapter(reviewAdapter);
    }

    private void fetchVideos(final int id){
//        Log.v(TAG, "fetchVideos");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbOrgApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDbOrgApiService movieDbOrgApiService = retrofit.create(MovieDbOrgApiService.class);
        Call<VideoServiceResponse> call = movieDbOrgApiService.videoList(id, MovieDbOrgApiService.API_KEY);
        call.enqueue(new Callback<VideoServiceResponse>() {
            @Override
            public void onResponse(Call<VideoServiceResponse> call, Response<VideoServiceResponse> response) {
                videos = (ArrayList) response.body().getVideos();
                if (!videos.isEmpty()) {
                    videoView.setVisibility(View.VISIBLE);
                    videoAdapter.addDataSet(videos);
                }
                updateShareIntent();
            }

            @Override
            public void onFailure(Call<VideoServiceResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch videos");
            }
        });
    }

    private void fetchReviews(final int id){
//        Log.v(TAG,"fetchReviews");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbOrgApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDbOrgApiService movieDbOrgApiService = retrofit.create(MovieDbOrgApiService.class);

        Call<ReviewServiceResponse> call = movieDbOrgApiService.reviewList(id, MovieDbOrgApiService.API_KEY);
        call.enqueue(new Callback<ReviewServiceResponse>() {
            @Override
            public void onResponse(Call<ReviewServiceResponse> call, Response<ReviewServiceResponse> response) {
                reviews = (ArrayList) response.body().getReviews();
                if(!reviews.isEmpty()){
                    reviewView.setVisibility(View.VISIBLE);
                    reviewAdapter.addDataSet(reviews);
                }
            }

            @Override
            public void onFailure(Call<ReviewServiceResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch Reviews");
            }
        });
    }

    @OnClick(R.id.ibtnFavorite)
    public void onFavoriteClicked(){
        Log.v(TAG,"clicked");
        isFavorite = !isFavorite;
        if(isFavorite){
            addMovieToDb();
        }else{
            removeMovieFromDb();
        }
    }

    private void changeFavoriteButtonColor(){
        if(isFavorite){
            favoriteStar.setBackground(mContext.getResources().getDrawable(android.R.drawable.star_big_on));
            favoriteText.setText(mContext.getResources().getText(R.string.remove_from_favorites));
        }else{
            favoriteStar.setBackground(mContext.getResources().getDrawable(android.R.drawable.star_big_off));
            favoriteText.setText(mContext.getResources().getText(R.string.add_to_favorites));
        }
    }

    private void setFavoriteButton(){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Cursor movieCursor = getContext().getContentResolver().query(
                        MovieProvider.Movies.CONTENT_URI,
                        new String[]{MovieColumns.ID},
                        MovieColumns.ID + " = " + mMovie.getId(),
                        null,
                        null);

                //Is in db
                if (movieCursor != null && movieCursor.moveToFirst()) {
                    movieCursor.close();
                    isFavorite = true;
                } else {//Not in db
                    isFavorite = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                changeFavoriteButtonColor();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addMovieToDb(){
        Log.v(TAG,"Adding");
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ContentValues moviesContent = new ContentValues();
                moviesContent.put(MovieColumns.ID, mMovie.getId());
                moviesContent.put(MovieColumns.POSTER_PATH, mMovie.getPosterPath());
                moviesContent.put(MovieColumns.OVERVIEW, mMovie.getOverview());
                moviesContent.put(MovieColumns.RELEASE_DATE, mMovie.getReleaseDate());
                moviesContent.put(MovieColumns.TITLE, mMovie.getTitle());
                moviesContent.put(MovieColumns.BACKDROP, mMovie.getBackdropPath());
                moviesContent.put(MovieColumns.POPULARITY, mMovie.getPopularity());
                moviesContent.put(MovieColumns.VOTE_COUNT, mMovie.getVoteCount());
                moviesContent.put(MovieColumns.VOTE_AVERAGE, mMovie.getVoteAverage());
                getContext().getContentResolver().insert(MovieProvider.Movies.CONTENT_URI, moviesContent);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(mContext,"Saved to Favorites",Toast.LENGTH_SHORT).show();
                changeFavoriteButtonColor();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeMovieFromDb(){
        Log.v(TAG,"Removing");
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Uri uri = ContentUris.withAppendedId(MovieProvider.Movies.CONTENT_URI,mMovie.getId());
                getContext().getContentResolver().delete(uri,
                        null ,null);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(mContext,"Removed from Favorites",Toast.LENGTH_SHORT).show();
                changeFavoriteButtonColor();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
