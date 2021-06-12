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
import com.dzenm.naughty.http.model.HttpBean;
import com.dzenm.naughty.util.Dimens;
import com.dzenm.naughty.util.ViewUtils;

class HttpAdapter extends BaseAdapter<HttpBean> {

    @Override
    protected View getView() {
        return ViewUtils.createHttpItemView(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final HttpBean bean = data.get(position);
        String statusString = bean.getStatus();
        boolean result = false;
        int color = R.attr.colorError;
        if (statusString.startsWith("2")) {
            result = true;
            color = R.attr.colorPrimary;
        } else if (statusString.startsWith("3")) {
            result = true;
            color = R.attr.colorAccent;
        }
        TextView tvResult = holder.getTextView(0);
        boolean isFinished = Naughty.getInstance().isHttpFinished(bean.getLoadingState());
        tvResult.setVisibility(isFinished ? View.VISIBLE : View.GONE);
        tvResult.setText(result ? "Success" : "Failed");
        float radius = Dimens.RADIUS_16F;
        tvResult.setBackground(ViewUtils.getStatusDrawable(ViewUtils.resolveColor(context, color), radius));

        holder.getProgressBar(1).setVisibility(isFinished ? View.INVISIBLE : View.VISIBLE);

        TextView tvStatus = holder.getTextView(2);
        tvStatus.setText(statusString);
        tvStatus.setTextColor(ViewUtils.resolveColor(context, color));

        String url = bean.getRequestUrl();

        String urlString = bean.getMethod() + "  " + Uri.parse(url).getPath();
        int index = urlString.indexOf("/");
        if (index != -1) {
            SpannableString string = new SpannableString(urlString);
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.1f);
            string.setSpan(sizeSpan, 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.getTextView(3).setText(string);
        }

        String baseUrl = Uri.parse(url).getScheme() + "://" + Uri.parse(url).getHost();
        holder.getTextView(4).setText(baseUrl);
        holder.getTextView(5).setText(bean.getTime());
        holder.getTextView(6).setText(bean.getCurrentTime());
        holder.getTextView(7).setText(bean.getResponseSize());

        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, radius));
    }
}
