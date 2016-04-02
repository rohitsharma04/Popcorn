package com.bitshifters.rohit.popcorn;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

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

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = (Movie) getArguments().getParcelable(ARG_MOVIE);
        }
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
        Log.v(TAG,"fetchVideos");
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
                if(!videos.isEmpty()){
                    videoView.setVisibility(View.VISIBLE);
                    videoAdapter.addDataSet(videos);
                }
            }

            @Override
            public void onFailure(Call<VideoServiceResponse> call, Throwable t) {
                Toast.makeText(mContext, "Failed to fetch videos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchReviews(final int id){
        Log.v(TAG,"fetchReviews");
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
                Toast.makeText(mContext,"Failed to fetch Reviews",Toast.LENGTH_LONG).show();
            }
        });
    }

}
