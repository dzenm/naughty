package com.dzenm.naughty.base;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.naughty.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    protected Context context;
    protected List<T> data = new ArrayList<>();
    protected OnItemClickListener<T> mOnItemClickListener;
    protected OnItemLongClickListener<T> mOnItemLongClickListener;

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.mOnItemLongClickListener = listener;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(data.get(position), position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(data.get(position), position);
                } else {
                    deleteItemAlert(data.get(position), position);
                }
                return false;
            }
        });
    }

    protected void deleteItemAlert(final T bean, final int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(context.getString(R.string.dialog_delete_start) + (position + 1) +
                        context.getString(R.string.dialog_delete_end))
                .setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBefore(bean);
                        data.remove(bean);
                        notifyItemRangeRemoved(position, 1);
                        notifyItemRangeChanged(position, data.size() - position);
                        Toast.makeText(context, R.string.toast_delete_text, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    protected void deleteBefore(T bean) {
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final SparseArray<View> views;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            views = new SparseArray<>();
        }

        public TextView getTextViewId(@IdRes int id) {
            return (TextView) getView(id);
        }

        public TextView getTextView(int position) {
            return (TextView) getViewId(position);
        }

        public ImageView getImageViewId(@IdRes int id) {
            return (ImageView) getView(id);
        }

        public ImageView getImageView(int position) {
            return (ImageView) getViewId(position);
        }

        public ProgressBar getProgressBarId(@IdRes int id) {
            return (ProgressBar) getView(id);
        }

        public ProgressBar getProgressBar(int position) {
            return (ProgressBar) getViewId(position);
        }

        public LinearLayout getLinearLayoutId(@IdRes int id) {
            return (LinearLayout) getView(id);
        }

        public LinearLayout getLinearLayout(int position) {
            return (LinearLayout) getViewId(position);
        }

        public RelativeLayout getRelativeLayoutId(@IdRes int id) {
            return (RelativeLayout) getView(id);
        }

        public RelativeLayout getRelativeLayout(int position) {
            return (RelativeLayout) getViewId(position);
        }

        public FrameLayout getFrameLayoutId(@IdRes int id) {
            return (FrameLayout) getView(id);
        }

        public FrameLayout getFrameLayout(int position) {
            return (FrameLayout) getViewId(position);
        }

        public View getView(@IdRes int id) {
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return view;
        }

        public View getViewId(int position) {
            return ((ViewGroup) itemView).getChildAt(position);
        }
    }


    public interface OnItemClickListener<T> {
        void onItemClick(T data, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(T data, int position);
    }
}
