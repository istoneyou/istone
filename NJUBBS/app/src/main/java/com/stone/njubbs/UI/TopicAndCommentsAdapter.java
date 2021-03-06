package com.stone.njubbs.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.stone.njubbs.R;
import com.stone.njubbs.UI.TopicAndCommentsActivityFragment.OnListFragmentInteractionListener;
import com.stone.njubbs.data.Article;
import com.stone.njubbs.data.URLImageParser;
import com.stone.njubbs.data.URLImageTagHandler;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Article} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TopicAndCommentsAdapter extends RecyclerView.Adapter<TopicAndCommentsAdapter.ViewHolder>{

    private List<Article> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;
    private  ImageLoader.ImageCache mImageCache;

    public TopicAndCommentsAdapter(Context context, List<Article> items, OnListFragmentInteractionListener listener, ImageLoader.ImageCache imageCache) {
        mValues = items;
        mListener = listener;
        mContext = context;
        mImageCache = imageCache;
    }


    public void setData(List<Article> mData) {
        mValues = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_topic_and_comments_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(position + "");
        holder.mContentView.setText(Html.fromHtml(mValues.get(position).getTitle(),
                new URLImageParser(mContext, holder.mContentView, mImageCache),
                new URLImageTagHandler(mContext)));
//        holder.mContentView.setText(Html.fromHtml("open the         rrrrr          <click size='18'><font color='#ff6c00'><big><big>baidu</big></big></font></click>",
//                new URLImageParser(mContext, holder.mContentView, mImageCache),
//                new URLImageTagHandler(mContext)));
        holder.mContentView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
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
        public final TextView mIdView;
        public final TextView mContentView;
        public Article mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
