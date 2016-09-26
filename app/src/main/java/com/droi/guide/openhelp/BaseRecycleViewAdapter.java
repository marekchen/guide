package com.droi.guide.openhelp;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droi.guide.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecycleViewAdapter<T> extends RecyclerView.Adapter {
    private static final String TAG = BaseRecycleViewAdapter.class.getSimpleName();

    protected static final int TYPE_HEADER = -1;
    protected static final int TYPE_ITEM = 0;
    protected static final int TYPE_FOOTER = 1;
    protected View headerView;

    protected boolean hasFooter;

    protected boolean hasHeader;

    protected Context mContext;

    protected ArrayList<T> mList = new ArrayList<>();

    protected OnRecycleViewItemClickListener itemClickListener;

    public interface OnRecycleViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnRecycleViewItemClickListener(OnRecycleViewItemClickListener l) {
        this.itemClickListener = l;
    }

    public abstract int getItemResource();

    public abstract void onBindItemViewHolder(BaseViewHolder holder, int position);

    public BaseRecycleViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        Log.i("test", "onCreateViewHolder:" + viewType);
        if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_recycleview_footer, parent, false);
            return new FooterViewHolder(view);
        }

        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(headerView);
        }

        view = LayoutInflater.from(mContext).inflate(getItemResource(), parent, false);
        return new BaseViewHolder(view);
    }

    public void addHeaderView(View view) {
        headerView = view;
        setHasHeader(true);
    }

    public void removeHeaderView() {
        headerView = null;
        setHasHeader(false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == getBasicItemCount() && holder.getItemViewType() == TYPE_FOOTER) {
            return;
        }
        if (position == 0 && holder.getItemViewType() == TYPE_HEADER) {
            return;
        }
        //Log.i("test", "position:" + position);
        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, position - (hasHeader ? 1 : 0));
                }
            });
        }

        onBindItemViewHolder(baseViewHolder, position - (hasHeader ? 1 : 0));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getBasicItemCount() && hasFooter) {
            return TYPE_FOOTER;
        }
        if (position == 0 && hasHeader) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }


    public boolean hasFooter() {
        return hasFooter;
    }

    public void setHasFooter(boolean hasFooter) {
        if (this.hasFooter != hasFooter) {
            this.hasFooter = hasFooter;
            notifyDataSetChanged();
        }
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        if (this.hasHeader != hasHeader) {
            this.hasHeader = hasHeader;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + (hasFooter ? 1 : 0) + (hasHeader ? 1 : 0);
    }

    public int getBasicItemCount() {
        return mList.size();
    }

    private T getItem(int position) {
        if (position > mList.size() - 1) {
            return null;
        }
        return mList.get(position - (hasHeader ? 1 : 0));
    }

    public List<T> getList() {
        return mList;
    }

    public void append(T t) {
        if (null == t) {
            return;
        }
        mList.add(t);
    }

    public void appendToList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (position < mList.size() - 1 && position >= 0) {
            mList.remove(position);
        }
    }

    public void clear() {
        mList.clear();
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        private final SparseArray<View> views;

        private View convertView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            views = new SparseArray<>();
            convertView = itemView;
        }

        public <T extends View> T getView(int viewId) {
            View view = views.get(viewId);
            if (view == null) {
                view = convertView.findViewById(viewId);
                views.put(viewId, view);
            }
            return (T) view;
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
