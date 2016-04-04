package com.bitshifters.rohit.popcorn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitshifters.rohit.popcorn.R;
import com.bitshifters.rohit.popcorn.api.Review;
import com.bitshifters.rohit.popcorn.util.Utility;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohit on 2/4/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> mValues;

    public ReviewAdapter(List<Review> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.review_list_content, parent, false);

        return new ViewHolder(view);
    }

    public void addDataSet(List<Review> items){
        mValues.clear();
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
        public Review mItem;
        public int position;
        public Boolean isFull;//Used for Toggling Full Review and short review

        @Bind(R.id.tvAuthor) TextView author;
        @Bind(R.id.tvReviewText) TextView reviewText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void setData(){
            author.setText(mItem.getAuthor());
            reviewText.setText(Utility.getReviewExcerpt(mItem.getContent()));
            isFull = false;
        }

        @OnClick(R.id.review)
        public void onReviewClick(){
            isFull = !isFull;
            if(isFull){
                reviewText.setText(Utility.getReviewExcerpt(mItem.getContent()));
            }else{
                reviewText.setText(mItem.getContent());
            }
        }
    }
}
