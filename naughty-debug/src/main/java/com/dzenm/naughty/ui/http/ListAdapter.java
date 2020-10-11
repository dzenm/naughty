package com.dzenm.naughty.ui.http;

import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.http.HttpBean;
import com.dzenm.naughty.util.ViewUtils;

class ListAdapter extends BaseAdapter<HttpBean> {

    @Override
    protected int layoutId() {
        return R.layout.item_floating;
    }

    @Override
    protected void onBindData(@NonNull BaseAdapter.ViewHolder holder, int position) {
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
        TextView tvStatus = holder.getTextViewId(R.id.tv_status);
        tvStatus.setText(statusString);
        tvStatus.setTextColor(ViewUtils.resolveColor(context, color));

        String url = bean.getRequestUrl();

        String urlString = bean.getMethod() + "  " + Uri.parse(url).getPath();
        int index = urlString.indexOf("/");
        if (index != -1) {
            SpannableString string = new SpannableString(urlString);
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.1f);
            string.setSpan(sizeSpan, 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView tvUrl = holder.getTextViewId(R.id.tv_url);
            tvUrl.setTextColor(ViewUtils.getColor(context, R.color.primary_text_color));
            tvUrl.setText(string);
        }

        String baseUrl = Uri.parse(url).getScheme() + "://" + Uri.parse(url).getHost();
        holder.getTextViewId(R.id.tv_base_url).setText(baseUrl);
        holder.getTextViewId(R.id.tv_current_time).setText(bean.getTime());
        holder.getTextViewId(R.id.tv_time).setText(bean.getCurrentTime());
        holder.getTextViewId(R.id.tv_size).setText(bean.getResponseSize());

        TextView tvResult = holder.getTextViewId(R.id.tv_result);
        boolean isFinished = Naughty.getInstance().isHttpFinished(bean.getLoadingState());
        tvResult.setVisibility(isFinished ? View.VISIBLE : View.GONE);
        tvResult.setText(result ? "Success" : "Failed");
        float radius = 4f;
        tvResult.setBackground(ViewUtils.getStatusDrawable(ViewUtils.resolveColor(context, color), radius));

        holder.getProgressBarId(R.id.progress_bar).setVisibility(isFinished ? View.INVISIBLE : View.VISIBLE);

        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, radius));
    }
}
