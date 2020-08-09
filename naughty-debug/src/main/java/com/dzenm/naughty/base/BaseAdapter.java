package com.dzenm.naughty.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    protected Context context;
    protected List<T> data;
    protected OnItemClickListener mOnItemClickListener;

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    protected int layoutId() {
        return 0;
    }

    protected View getView() {
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                layoutId() == 0
                        ? getView()
                        : LayoutInflater.from(context).inflate(layoutId(), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        onBindData(holder, position);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(data.get(position), position);
                }
            });
        }
    }

    protected void onBindData(@NonNull ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> views;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            views = new SparseArray<>();
        }

        public TextView getTextViewId(int id) {
            return (TextView) getView(id);
        }

        public TextView getTextView(int position) {
            return (TextView) getView().getChildAt(position);
        }

        public ImageView getImageViewId(int id) {
            return (ImageView) getView(id);
        }

        public ImageView getImageView(int position) {
            return (ImageView) getView().getChildAt(position);
        }

        public View getView(int id) {
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return view;
        }

        public ViewGroup getView() {
            return (ViewGroup) itemView;
        }
    }


    public interface OnItemClickListener {

        void onItemClick(Object data, int position);
    }
}
