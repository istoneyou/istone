package com.stone.njubbs.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.stone.njubbs.NJUBBSApplication;
import com.stone.njubbs.R;
import com.stone.njubbs.Utils.UrlUtils;
import com.stone.njubbs.data.Article;
import com.stone.njubbs.data.BBSDataConverterFactory;
import com.stone.njubbs.data.NJUBBSApiStore;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopTenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopTenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopTenFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_QUERY_URL = "query_url";


    private static RequestQueue mQueue;

    private OnFragmentInteractionListener mListener;
    public RecyclerView mList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    MyAdapter adapter;

    public TopTenFragment() {
        // Required empty public constructor
    }


    public static TopTenFragment newInstance() {
        TopTenFragment fragment = new TopTenFragment();
        mQueue = NJUBBSApplication.getRequestQueue();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_top_ten, container, false);
        mList = (RecyclerView) mView.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mList.setLayoutManager(mLayoutManager);
        mList.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new MyAdapter();
        mList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTopTenData(getActivity());
    }

    @Override
    public void onRefresh() {
        loadTopTenData(getActivity());

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRefresh();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Article> mDataset = new ArrayList<>();

        // Provide a reference to the type of views that you are using
        // (custom viewholder)
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mAuthorView;
            public TextView mTitleView;
            public TextView mBoardView;
            public TextView mNumView;
            public View itemView;

            public ViewHolder(View v) {
                super(v);
                itemView = v;
                mAuthorView = (TextView) v.findViewById(R.id.author);
                mTitleView = (TextView) v.findViewById(R.id.title);
                mBoardView = (TextView) v.findViewById(R.id.board);
                mNumView = (TextView) v.findViewById(R.id.num);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter() {

        }

        private void setData(List<Article> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_topic, parent, false);
            // set the view's size, margins, paddings and layout parameters
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            v.setBackgroundResource(typedValue.resourceId);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Article article = mDataset.get(position);
            holder.mAuthorView.setText(article.getAuthor());
            holder.mTitleView.setText(article.getTitle());
            holder.mBoardView.setText(article.getBoard());
            holder.mNumView.setText(article.getNum());

            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TopicAndCommentsActivity.class);
                    intent.putExtra(ARG_QUERY_URL, article);
                    startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private void loadTopTenData(Context context) {
//        if (NetworkUtils.isNetworkAvailable(context)) {
//            final StringRequest topTenRequest = new StringRequest(Request.Method.GET, UrlUtils.URL_TOP_TEN,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            adapter.setData(jsoupTopTenParser(response));
//                            adapter.notifyDataSetChanged();
//                            stopRefresh();
//                            Snackbar.make(mList, "network ok reflash success", Snackbar.LENGTH_LONG).show();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            adapter.setData(jsoupTopTenParser(getFromDiskCache(UrlUtils.URL_TOP_TEN)));
//                            adapter.notifyDataSetChanged();
//                            stopRefresh();
//                            Snackbar.make(mList, "network ok reflash error", Snackbar.LENGTH_LONG).show();
//                        }
//                    });
//            topTenRequest.setShouldCache(true);
//            mQueue.add(topTenRequest);
//        } else {
//            adapter.setData(jsoupTopTenParser(getFromDiskCache(UrlUtils.URL_TOP_TEN)));
//            adapter.notifyDataSetChanged();
//            stopRefresh();
//            Snackbar.make(mList, "no network get data from disk", Snackbar.LENGTH_LONG).show();
//        }
        NJUBBSApiStore.NJUBBSApi topTenApi = new NJUBBSApiStore().buildGetArticleApi(BBSDataConverterFactory.CONVERTTER_GET_TOP_TEN);
        Call<List<Article>> call = topTenApi.getTopTen();
        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, retrofit2.Response<List<Article>> response) {
                adapter.setData(response.body());
                adapter.notifyDataSetChanged();
                stopRefresh();
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {

            }
        });
    }

    private String getFromDiskCache(String url) {
        if (mQueue.getCache().get(url) != null) {
            try {
                String response = new String(mQueue.getCache().get(url).data, "gb2312");
                return response;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<Article> jsoupTopTenParser(String htmlStr) {
        List<Article> topTenData = new ArrayList();
        if (htmlStr == null || htmlStr.isEmpty()) {
            return topTenData;
        }
        Document doc = Jsoup.parse(htmlStr);
        Elements trs = doc.select("tr");
        for (int i =1; i < trs.size(); i ++ ) {
            Elements cols = trs.get(i).children();
            String board = cols.get(1).text();
            String title = cols.get(2).text();
            String uri = UrlUtils.BASE_BBS_URL + cols.get(2).select("a").attr("href").trim();
            String author = cols.get(3).text();
            String num = cols.get(4).text();
            Article article = new Article(board, title, uri, author, num);
            topTenData.add(article);
        }
        return topTenData;
    }

    public void stopRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
