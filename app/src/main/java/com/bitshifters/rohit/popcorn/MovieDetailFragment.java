package com.bitshifters.rohit.popcorn;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.bitshifters.rohit.popcorn.data.MovieProvider;
import com.bitshifters.rohit.popcorn.data.MovieTableMeta;
import com.bitshifters.rohit.popcorn.data.ReviewTableMeta;
import com.bitshifters.rohit.popcorn.data.VideoTableMeta;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private boolean hasVideos,hasReviews;

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

        videos = new ArrayList<>();
        reviews = new ArrayList<>();

        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = this.getActivity();

        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        mContext = rootView.getContext();

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && mMovie != null) {
            appBarLayout.setTitle(mMovie.getTitle());
        }

        //Binding Views
        ButterKnife.bind(this, rootView);

        //Setting values
        if (mMovie != null) {

            setRecyclerView();
            setAllViews();

            setFavoriteButton();

            //Restoring State
            if(savedInstanceState != null){
                if(savedInstanceState.containsKey(ARG_VIDEO)){
                    videos =  savedInstanceState.getParcelableArrayList(ARG_VIDEO);
                    videoAdapter.addDataSet(videos);
                    videoView.setVisibility(View.VISIBLE);
                    hasVideos = true;
                }
                if(savedInstanceState.containsKey(ARG_REVIEW)){
                    reviews = savedInstanceState.getParcelableArrayList(ARG_REVIEW);
                    reviewAdapter.addDataSet(reviews);
                    reviewView.setVisibility(View.VISIBLE);
                    hasReviews = true;
                }
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ARG_VIDEO, videos);
        outState.putParcelableArrayList(ARG_REVIEW, reviews);
        super.onSaveInstanceState(outState);
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

    private void setAllViews() {
        title.setText(mMovie.getTitle());
        releaseDate.setText(Utility.getFormattedDate(mMovie.getReleaseDate()));
        overview.setText(mMovie.getOverview());
        voteAverage.setRating(mMovie.getVoteAverage() / 2);
        voteAverageText.setText(getResources().getString(R.string.rating, mMovie.getVoteAverage()));

        Picasso.with(mContext)
                .load(Utility.getPortraitPosterUrl(getActivity(),mMovie.getPosterPath()))
                .error(R.drawable.portrait_poster_not_found)
                .into(posterPortrait);

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

    public void updateShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }else{
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mMovie.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                Utility.getShareActionText(mMovie, videos, mContext));
        shareActionProvider.setShareIntent(shareIntent);
    }

    private void fetchVideos(final int id){
        Call<VideoServiceResponse> call = Utility.getMovieDbOrgApiService()
                .videoList(id, MovieDbOrgApiService.API_KEY);
        call.enqueue(new Callback<VideoServiceResponse>() {
            @Override
            public void onResponse(Call<VideoServiceResponse> call, Response<VideoServiceResponse> response) {
                videos.addAll(response.body().getVideos());
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

        Call<ReviewServiceResponse> call = Utility.getMovieDbOrgApiService()
                .reviewList(id, MovieDbOrgApiService.API_KEY);
        call.enqueue(new Callback<ReviewServiceResponse>() {
            @Override
            public void onResponse(Call<ReviewServiceResponse> call, Response<ReviewServiceResponse> response) {
                reviews.addAll(response.body().getReviews());
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
        isFavorite = !isFavorite;
        if(isFavorite){
            addMovieToDb();
            addVideoToDb(videos);
            addReviewToDb(reviews);
        }else{
            removeMovieFromDb();
            removeVideoFromDb();
            removeReviewFromDb();
        }
    }

    private void setFavoriteButton(){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Cursor movieCursor = getContext().getContentResolver().query(
                        MovieProvider.MOVIES_URI,
                        new String[]{MovieTableMeta.ID},
                        MovieTableMeta.ID + " = " + mMovie.getId(),
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
                if(isFavorite){
                    fetchVideoFromDb();
                    fetchReviewFromDb();
                }
                if (!isFavorite && !hasVideos) {
                    fetchVideos(mMovie.getId());
                }
                if(!isFavorite && !hasReviews){
                    fetchReviews(mMovie.getId());
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void changeFavoriteButtonColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(isFavorite){
                favoriteStar.setBackground(mContext.getDrawable(android.R.drawable.star_big_on));
                favoriteText.setText(mContext.getResources().getText(R.string.remove_from_favorites));
            }else{
                favoriteStar.setBackground(mContext.getDrawable(android.R.drawable.star_big_off));
                favoriteText.setText(mContext.getResources().getText(R.string.add_to_favorites));
            }
        }else{
            if(isFavorite){
                favoriteStar.setBackground(mContext.getResources().getDrawable(android.R.drawable.star_big_on));
                favoriteText.setText(mContext.getResources().getText(R.string.remove_from_favorites));
            }else{
                favoriteStar.setBackground(mContext.getResources().getDrawable(android.R.drawable.star_big_off));
                favoriteText.setText(mContext.getResources().getText(R.string.add_to_favorites));
            }
        }

    }

    private void fetchVideoFromDb(){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Cursor videoCursor = getContext().getContentResolver().query(
                        MovieProvider.VIDEOS_URI,
                        VideoTableMeta.COLUMNS,
                        VideoTableMeta.MOVIE_ID + " = " + mMovie.getId(),
                        null,
                        null);
                if(videoCursor != null) {
                    while (videoCursor.moveToNext()) {
                        String id = videoCursor.getString(VideoTableMeta.ID_ID);
                        String iso6391 = videoCursor.getString(VideoTableMeta.ISO6391_ID);
                        String iso31661 = videoCursor.getString(VideoTableMeta.ISO31661_ID);
                        String key = videoCursor.getString(VideoTableMeta.KEY_ID);
                        String name = videoCursor.getString(VideoTableMeta.NAME_ID);
                        String site = videoCursor.getString(VideoTableMeta.SITE_ID);
                        Integer size = videoCursor.getInt(VideoTableMeta.SITE_ID);
                        String type = videoCursor.getString(VideoTableMeta.TYPE_ID);
                        videos.add(new Video(id,iso6391,iso31661,key,name,site,size,type));
                    }
                    videoCursor.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(!videos.isEmpty()) {
                    videoView.setVisibility(View.VISIBLE);
                    videoAdapter.addDataSet(videos);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void fetchReviewFromDb(){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Cursor reviewCursor = getContext().getContentResolver().query(
                        MovieProvider.REVIEWS_URI,
                        ReviewTableMeta.COLUMNS,
                        ReviewTableMeta.MOVIE_ID + " = " + mMovie.getId(),
                        null,
                        null);
                if(reviewCursor != null) {
                    while (reviewCursor.moveToNext()) {
                        String id = reviewCursor.getString(ReviewTableMeta.ID_ID);
                        String author = reviewCursor.getString(ReviewTableMeta.AUTHOR_ID);
                        String content= reviewCursor.getString(ReviewTableMeta.CONTENT_ID);
                        String url= reviewCursor.getString(ReviewTableMeta.URL_ID);
                        reviews.add(new Review(id,author,content,url));
                    }
                    reviewCursor.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(!reviews.isEmpty()) {
                    reviewView.setVisibility(View.VISIBLE);
                    reviewAdapter.addDataSet(reviews);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addVideoToDb(final List<Video> videos){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<ContentValues> vidoesValues = new ArrayList<>();
                for(Video video: videos){
                    ContentValues values = new ContentValues();

                    values.put(VideoTableMeta.MOVIE_ID,mMovie.getId());
                    values.put(VideoTableMeta.ID, video.getId());
                    values.put(VideoTableMeta.ISO6391, video.getIso6391());
                    values.put(VideoTableMeta.ISO31661, video.getIso31661());
                    values.put(VideoTableMeta.KEY, video.getKey());
                    values.put(VideoTableMeta.NAME, video.getName());
                    values.put(VideoTableMeta.SITE, video.getSite());
                    values.put(VideoTableMeta.SIZE, video.getSize());
                    values.put(VideoTableMeta.TYPE, video.getType());

                    vidoesValues.add(values);
                }
                getContext().getContentResolver().bulkInsert(MovieProvider.VIDEOS_URI,
                        vidoesValues.toArray(new ContentValues[vidoesValues.size()]));

                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addReviewToDb(final List<Review> reviews){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<ContentValues> reviewValues = new ArrayList<>();
                for(Review review: reviews){
                    ContentValues values = new ContentValues();

                    values.put(ReviewTableMeta.MOVIE_ID,mMovie.getId());
                    values.put(ReviewTableMeta.ID, review.getId());
                    values.put(ReviewTableMeta.AUTHOR, review.getAuthor());
                    values.put(ReviewTableMeta.CONTENT, review.getContent());
                    values.put(ReviewTableMeta.URL, review.getUrl());

                    reviewValues.add(values);
                }
                getContext().getContentResolver().bulkInsert(MovieProvider.REVIEWS_URI,
                        reviewValues.toArray(new ContentValues[reviewValues.size()]));

                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addMovieToDb(){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ContentValues moviesContent = new ContentValues();
                moviesContent.put(MovieTableMeta.ID, mMovie.getId());
                moviesContent.put(MovieTableMeta.POSTER_PATH, mMovie.getPosterPath());
                moviesContent.put(MovieTableMeta.OVERVIEW, mMovie.getOverview());
                moviesContent.put(MovieTableMeta.RELEASE_DATE, mMovie.getReleaseDate());
                moviesContent.put(MovieTableMeta.TITLE, mMovie.getTitle());
                moviesContent.put(MovieTableMeta.BACKDROP, mMovie.getBackdropPath());
                moviesContent.put(MovieTableMeta.POPULARITY, mMovie.getPopularity());
                moviesContent.put(MovieTableMeta.VOTE_COUNT, mMovie.getVoteCount());
                moviesContent.put(MovieTableMeta.VOTE_AVERAGE, mMovie.getVoteAverage());
                getContext().getContentResolver().insert(MovieProvider.MOVIES_URI, moviesContent);

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
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                getContext().getContentResolver().delete(MovieProvider.MOVIES_URI,
                        MovieTableMeta.ID + " = " + mMovie.getId(), null);


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(mContext,"Removed from Favorites",Toast.LENGTH_SHORT).show();
                changeFavoriteButtonColor();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeVideoFromDb(){
        new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                getContext().getContentResolver().delete(MovieProvider.VIDEOS_URI,
                        VideoTableMeta.MOVIE_ID + " = " + mMovie.getId(), null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeReviewFromDb(){
       new AsyncTask<Void,Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                getContext().getContentResolver().delete(MovieProvider.REVIEWS_URI,
                        ReviewTableMeta.MOVIE_ID + " = " + mMovie.getId(), null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
