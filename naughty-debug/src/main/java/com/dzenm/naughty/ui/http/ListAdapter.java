package com.dzenm.naughty.ui.http;

import android.content.Context;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.R;
import com.dzenm.naughty.util.ViewUtils;

import java.util.List;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private List<HttpBean> data;
    private OnItemClickListener onItemClickListener;

    void setData(List<HttpBean> data) {
        this.data = data;
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_floating, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final HttpBean bean = data.get(position);
        String statusString = bean.getStatus();
        boolean result;
        int color;
        if (statusString.startsWith("2")) {
            result = true;
            color = R.attr.colorPrimary;
        } else if (statusString.startsWith("3")) {
            result = true;
            color = R.attr.colorAccent;
        } else if (statusString.startsWith("4") || statusString.startsWith("5")) {
            result = false;
            color = R.attr.colorError;
        } else {
            result = false;
            color = R.attr.colorError;
        }
        holder.tvStatus.setText(statusString);
        holder.tvStatus.setTextColor(ViewUtils.resolveColor(context, color));

        String url = bean.getRequestUrl();

        String urlString = bean.getMethod() + "  " + Uri.parse(url).getPath();
        int index = urlString.indexOf("/");
        if (index != -1) {
            SpannableString string = new SpannableString(urlString);
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.1f);
            string.setSpan(sizeSpan, 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvUrl.setTextColor(ViewUtils.primaryTextColor());
            holder.tvUrl.setText(string);
        }

        String baseUrl = Uri.parse(url).getScheme() + "://" + Uri.parse(url).getHost();
        holder.tvBaseUrl.setText(baseUrl);
        holder.tvCurrentTime.setText(bean.getTime());
        holder.tvTime.setText(bean.getCurrentTime());
        holder.tvSize.setText(bean.getResponseSize());

        boolean isFinished = Naughty.getInstance().isHttpFinished(bean.getLoadingState());
        holder.tvResult.setVisibility(isFinished ? View.VISIBLE : View.GONE);
        holder.tvResult.setText(result ? "Success" : "Failed");

        float radius = 4f;
        holder.tvResult.setBackground(ViewUtils.getStatusDrawable(ViewUtils.resolveColor(context, color), radius));

        holder.progressBar.setVisibility(isFinished ? View.INVISIBLE : View.VISIBLE);

        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, radius));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(bean, position);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResult;
        TextView tvStatus;
        TextView tvUrl;
        TextView tvBaseUrl;
        TextView tvCurrentTime;
        TextView tvTime;
        TextView tvSize;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_status);
            tvUrl = itemView.findViewById(R.id.tv_url);
            tvBaseUrl = itemView.findViewById(R.id.tv_base_url);
            tvCurrentTime = itemView.findViewById(R.id.tv_current_time);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvResult = itemView.findViewById(R.id.tv_result);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(HttpBean data, int position);
    }
}
