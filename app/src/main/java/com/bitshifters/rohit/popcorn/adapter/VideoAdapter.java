package com.bitshifters.rohit.popcorn.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitshifters.rohit.popcorn.MainActivity;
import com.bitshifters.rohit.popcorn.MovieDetailActivity;
import com.bitshifters.rohit.popcorn.MovieDetailFragment;
import com.bitshifters.rohit.popcorn.R;
import com.bitshifters.rohit.popcorn.api.Movie;
import com.bitshifters.rohit.popcorn.api.Video;
import com.bitshifters.rohit.popcorn.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohit on 2/4/16.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final List<Video> mValues;
    private Context mContext;

    public VideoAdapter(List<Video> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.video_list_content, parent, false);

        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    public void addDataSet(List<Video> items){
        mValues.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.position = position;

        holder.setData();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public Video mItem;
        public int position;
        @Bind(R.id.ivVideoThumbnail) ImageView mVideoThumbnail;
        @Bind(R.id.tvVideoName) TextView videoName;
        @Bind(R.id.tvVideoType) TextView videoType;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void setData(){
            videoName.setText(mItem.getName());
            videoType.setText(mItem.getType());
            Picasso.with(mContext)
                    .load(Utility.getYoutubeThumbnailUrl(mItem.getKey()))
                    .error(R.drawable.youtube_play)
                    .into(mVideoThumbnail);
        }

        @OnClick(R.id.ivVideoThumbnail)
        public void onClick(View v) {
            Log.v("Adapter","Video Clicked");
            Uri videoUri = Uri.parse(Utility.getYoutubeVideoUrl(mItem.getKey()));
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, videoUri);
            if(videoIntent.resolveActivity(mContext.getPackageManager()) != null){
                mContext.startActivity(videoIntent);
            }else{
                Toast.makeText(mContext,"No Application Found to Launch Video",Toast.LENGTH_LONG).show();
            }
        }
    }
}
