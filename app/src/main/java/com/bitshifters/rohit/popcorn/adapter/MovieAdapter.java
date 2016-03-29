package com.bitshifters.rohit.popcorn.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bitshifters.rohit.popcorn.MainActivity;
import com.bitshifters.rohit.popcorn.MovieDetailActivity;
import com.bitshifters.rohit.popcorn.MovieDetailFragment;
import com.bitshifters.rohit.popcorn.R;
import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.MoviesService;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rohit on 29/3/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

        private final List<Movie> mValues;
        private MainActivity mActivity;

        public MovieAdapter(MainActivity activity, List<Movie> items) {
            mValues = items;
            mActivity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        public void changeDataSet(List<Movie> items){
            //clear first for changes
            mValues.clear();//remove for pagination
            mValues.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            //Loading Image
            Picasso.with(mActivity)
                    .load(MoviesService.IMAGE_BASE_URL +"w185/"+holder.mItem.getPosterPath())
                    .into(holder.mPosterPortrait);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity.mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, holder.mItem);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        mActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_MOVIE, holder.mItem);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public Movie mItem;
            public ImageView mPosterPortrait;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPosterPortrait = (ImageView) view.findViewById(R.id.ivPosterPortrait);
            }

        }
    }

