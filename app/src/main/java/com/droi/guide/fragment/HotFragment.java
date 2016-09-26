package com.droi.guide.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Article;
import com.droi.guide.model.Question;
import com.droi.guide.views.CircleImageView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.ArrayList;
import java.util.List;

public class HotFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Article> datas;
    private HomeAdapter adapter;
    private String mParam1;
    private String mParam2;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public HotFragment() {
        // Required empty public constructor
    }

    public static HotFragment newInstance(String param1, String param2) {
        HotFragment fragment = new HotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.lv);
        if (datas == null) {
            datas = new ArrayList<>();
        }
        if (datas.isEmpty()) {
            refresh();
        }
        adapter = new HomeAdapter(this.getActivity(), datas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    void refresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        DroiQuery droiQuery = DroiQuery.Builder.newBuilder().query(Article.class).build();
        droiQuery.runQueryInBackground(new DroiQueryCallback<Article>() {
            @Override
            public void result(final List<Article> list, DroiError droiError) {
                if (droiError.isOk()) {
                    Log.i("test", droiError.isOk() + "," + list.size());
                    if (list.size() != 0) {
                        datas.clear();
                        datas.addAll(list);
                        Handler mainThread = new Handler(Looper.getMainLooper());
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                datas = list;
                                adapter.notifyDataSetChanged();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    Question getQuestion(String questionId) {
        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, questionId);
        DroiQuery droiQuery = DroiQuery.Builder.newBuilder().where(cond).query(Question.class).build();
        DroiError error = new DroiError();
        List<Question> list = droiQuery.runQuery(error);
        if (error.isOk() && list.size() == 1) {
            Question question = list.get(0);
            return question;
        } else {
            return null;
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        Context mContext;
        private List<Article> mDatas;

        HomeAdapter(Context context, List<Article> datas) {
            mContext = context;
            mDatas = datas;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    mContext).inflate(R.layout.item_answer, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.tvTitle.setText(mDatas.get(position).question.questiontTitle);

            holder.tvContent.setText(mDatas.get(position).brief);
            if (mDatas.get(position).author.avatar != null) {
                mDatas.get(position).author.avatar.getInBackground(new DroiCallback<byte[]>() {
                    @Override
                    public void result(byte[] bytes, DroiError error) {
                        if (error.isOk()) {
                            if (bytes == null) {
                                Log.i("test", "bytes == null");
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.ivAvatar.setImageBitmap(bitmap);
                            }
                        }
                    }
                }, null);
            }
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;
            TextView tvContent;
            CircleImageView ivAvatar;

            public MyViewHolder(View view) {
                super(view);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("test", "onPause");
    }

}
