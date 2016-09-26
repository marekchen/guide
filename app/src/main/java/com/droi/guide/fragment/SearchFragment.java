package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.activity.WriteQuestionActivity;
import com.droi.guide.adapter.SimpleAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchFragment extends Fragment {
    private static final String SP_SEARCH_HISTORY_FILE = "search_history";
    private static final String SP_SEARCH_HISTORY_KEY = "search";
    private String mSeparator;

    @BindView(R.id.search_history_layout)
    LinearLayout mSearchHistoryLayout;
    @BindView(R.id.search_result_layout)
    LinearLayout mSearchResultLayout;
    @BindView(R.id.toolbar_search)
    EditText mSearchView;
    @BindView(R.id.search_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.search_history_clear)
    TextView clearHistory;
    @BindView(R.id.toolbar_search_action)
    TextView searchAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        init(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshSearchHistory();
    }

    @OnClick(R.id.create_question)
    void createQuestion() {
        Intent intent = new Intent(this.getActivity(), WriteQuestionActivity.class);
        startActivity(intent);
    }

    private void init(View view) {
        mSeparator = ";";
        clearHistory = (TextView) view.findViewById(R.id.search_history_clear);
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchHistory();
            }
        });
        initUI(view);
    }

    private void initUI(View view) {
        mSearchHistoryLayout = (LinearLayout) view.findViewById(R.id.search_history_layout);
        mSearchResultLayout = (LinearLayout) view.findViewById(R.id.search_result_layout);
        mProgressBar = (ProgressBar) view.findViewById(R.id.search_progress);
        mSearchHistoryLayout.setVisibility(View.GONE);
        mSearchResultLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        refreshSearchHistory(view);
        searchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchContent = mSearchView.getText().toString();
                if (!searchContent.isEmpty())
                    performSearch(searchContent);
            }
        });
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchContent = mSearchView.getText().toString();
                    if (!searchContent.isEmpty()) {
                        performSearch(searchContent);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void refreshSearchHistory(View view) {
        final ArrayList<String> searchHistory = getSearchHistory();
        if (!searchHistory.isEmpty()) {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
            ListView listView = (ListView) view.findViewById(R.id.search_history_list);
            listView.setAdapter(new SimpleAdapter(this.getActivity(), searchHistory));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    performSearch(searchHistory.get(position));
                }
            });
        }
    }

    private void performSearch(String searchContent) {
        hideSoftKeyBoard(mSearchView);
        saveSearchHistory(searchContent);
    }

    private void saveSearchHistory(String searchContent) {
        SharedPreferences sp = getActivity().getSharedPreferences(SP_SEARCH_HISTORY_FILE, 0);
        SharedPreferences.Editor editor = sp.edit();
        String savedContent = sp.getString(SP_SEARCH_HISTORY_KEY, "");
        if (savedContent.isEmpty()) {
            editor.putString(SP_SEARCH_HISTORY_KEY, searchContent + mSeparator);
        } else {
            if (savedContent.contains(searchContent)) {
                savedContent = savedContent.replaceAll(searchContent + mSeparator, "");
            }
            savedContent += searchContent + mSeparator;
            editor.putString(SP_SEARCH_HISTORY_KEY, savedContent);
        }
        editor.apply();
    }

    private ArrayList<String> getSearchHistory() {
        ArrayList<String> searchHistory = new ArrayList<>();
        SharedPreferences sp = getActivity().getSharedPreferences(SP_SEARCH_HISTORY_FILE, 0);
        String savedContent = sp.getString(SP_SEARCH_HISTORY_KEY, "");
        if (!savedContent.isEmpty()) {
            String[] savedArray = savedContent.split(mSeparator);
            for (int i = savedArray.length - 1; i > -1; i--) {
                if (!savedArray[i].isEmpty() && searchHistory.size() < 10) {
                    searchHistory.add(savedArray[i]);
                }
            }
        }
        return searchHistory;
    }

    private void clearSearchHistory() {
        SharedPreferences sp = getActivity().getSharedPreferences(SP_SEARCH_HISTORY_FILE, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
        mSearchHistoryLayout.setVisibility(View.GONE);
        mSearchResultLayout.setVisibility(View.GONE);
    }


    private void hideSoftKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
