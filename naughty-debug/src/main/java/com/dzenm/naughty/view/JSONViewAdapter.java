package com.dzenm.naughty.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.naughty.R;
import com.dzenm.naughty.util.StringUtils;
import com.dzenm.naughty.util.ViewUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 可以展开收缩Json的RecyclerView的适配器
 */
public class JSONViewAdapter extends RecyclerView.Adapter<JSONViewAdapter.JsonItemViewHolder> {

    private JSONObject mJsonObject = null;
    private JSONArray mJsonArray = null;
    private final Context mContext;

    private int mTextSize = 14;

    private final int textColor, keyColor, stringColor, numberColor, booleanColor, urlColor, nullColor;

    public JSONViewAdapter(Context context) {
        mContext = context;
        textColor = Color.parseColor("#333333");
        keyColor = Color.parseColor("#92278f");
        stringColor = Color.parseColor("#3ab54a");
        numberColor = Color.parseColor("#25aae2");
        booleanColor = Color.parseColor("#f98280");
        urlColor = Color.parseColor("#61d2d6");
        nullColor = Color.parseColor("#f1592a");
    }

    public void bindData(String json) {
        try {
            Object value = new JSONTokener(json).nextValue();
            if (value != null) {
                if (value instanceof JSONObject) {
                    mJsonObject = (JSONObject) value;
                } else if (value instanceof JSONArray) {
                    mJsonArray = (JSONArray) value;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void bindData(JSONObject jsonObject) {
        this.mJsonObject = jsonObject;
        notifyDataSetChanged();
    }

    public void bindData(JSONArray jsonArray) {
        this.mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    @NonNull
    @Override
    public JsonItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JsonItemViewHolder(new JSONItemView(mContext));
    }

    @Override
    public void onBindViewHolder(@NonNull JsonItemViewHolder holder, int position) {
        holder.itemView.setTextSize(mTextSize);
        holder.itemView.setKeyTextColor(textColor);
        if (mJsonObject != null) {
            handleJsonData(holder.itemView, position, mJsonObject, null);
        }
        if (mJsonArray != null) {
            handleJsonData(holder.itemView, position, null, mJsonArray);
        }
    }

    @Override
    public int getItemCount() {
        return mJsonObject != null
                ? getJSONCount(mJsonObject.names())
                : mJsonArray != null
                ? getJSONCount(mJsonArray)
                : 0;
    }

    /**
     * 得到JSONArray的数量，注意：[和 ] 或 { 和 } 是2
     *
     * @param jsonArray JSONArray对象
     * @return JSONArray对象的数量
     */
    private int getJSONCount(JSONArray jsonArray) {
        return jsonArray != null ? jsonArray.length() + 2 : 2;
    }

    /**
     * 处理JsonItemView右边部分的样式（即展示值的部分）
     *
     * @param value       要在JsonItemView展示的value
     * @param itemView    要处理的JsonItemView对象
     * @param appendComma 是否附加逗号
     * @param hierarchy   View的层次结构数量
     */
    private void handleValueStyle(JSONItemView itemView, Object value,
                                  boolean appendComma, int hierarchy) {
        SpannableStringBuilder string = new SpannableStringBuilder();
        if (value == null || value instanceof String
                || value instanceof Number || value instanceof Boolean) {
            // 处理值为null, String, Number, Boolean类型不能展开的样式
            handleMapValueStyle(string, itemView, value);
        } else if (value instanceof JSONObject) {
            // 处理值为JSONObject类型的样式
            handleJSONObjectValueStyle(string, itemView, (JSONObject) value, appendComma, hierarchy);
        } else if (value instanceof JSONArray) {
            // 处理值为JSONArray类型的样式
            handleJSONArrayValueStyle(string, itemView, (JSONArray) value, appendComma, hierarchy);
        }
        if (appendComma) string.append(",");
        itemView.setValueText(string);
    }

    /**
     * 处理键值对的JsonItemView样式
     *
     * @param sb       需要处理的样式
     * @param itemView 要处理的JsonItemView对象
     * @param value    String类型的值
     */
    private void handleMapValueStyle(SpannableStringBuilder sb, JSONItemView itemView,
                                     final Object value) {
        if (value == null) {
            // 处理值为null的样式
            sb.append("null");
            handleValueStyle(sb, nullColor, 0, sb.length());
        } else if (value instanceof String) {
            // 处理值为字符串的样式
            sb.append("\"").append(value.toString()).append("\"");
            int totalLen = sb.length();
            if (StringUtils.isUrl(value.toString())) {
                // 设置单引号（"）的样式
                handleValueStyle(sb, stringColor, 0, 1);
                // 设置url的样式
                handleValueStyle(sb, urlColor, 1, totalLen - 1);
                // 设置单引号（"）的样式
                handleValueStyle(sb, stringColor, totalLen - 1, totalLen);
                itemView.setRightTextClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToBrowser(value.toString());
                    }
                });
            } else {
                handleValueStyle(sb, stringColor, 0, totalLen);
            }
        } else {
            // 处理值为Number和Boolean的样式
            sb.append(value.toString());
            boolean isBool = value instanceof Boolean;
            handleValueStyle(sb, isBool ? booleanColor : numberColor, 0, sb.length());
        }
        // 对所有不能展开的item view设置长按复制value的事件
        itemView.hideIcon();
        String val = value == null ? "" : value.toString();
        itemView.setOnLongClickListener(new JsonItemViewLongClickListener(val));
    }

    /**
     * 处理值为JSONObject类型的JsonIteView样式
     *
     * @param sb          需要处理的样式
     * @param itemView    要处理的JsonItemView对象
     * @param value       JSONObject类型的值
     * @param appendComma 是否附加逗号
     * @param hierarchy   View的层次结构数量
     */
    private void handleJSONObjectValueStyle(SpannableStringBuilder sb, JSONItemView itemView,
                                            JSONObject value, Boolean appendComma, Integer hierarchy) {
        itemView.showIcon(true);
        sb.append("Object{...}");
        handleValueStyle(sb, textColor, 0, sb.length());
        itemView.setOnClickListener(new JsonItemViewClickListener(
                value,
                appendComma,
                hierarchy + 1
        ));
    }

    /**
     * 处理值为JSONArray类型的JsonItemView样式
     *
     * @param sb          需要处理的样式
     * @param itemView    JsonItemView对象
     * @param value       JSONArray类型的值
     * @param appendComma 是否附加逗号
     * @param hierarchy   View的层次结构数量
     */
    private void handleJSONArrayValueStyle(SpannableStringBuilder sb, JSONItemView itemView,
                                           JSONArray value, Boolean appendComma, Integer hierarchy) {
        itemView.showIcon(true);
        sb.append("Array[").append(String.valueOf(value.length())).append("]");
        // 设置Array[的样式，字符串"Array["的字符数量是6
        handleValueStyle(sb, textColor, 0, 6);
        // 设置数组数量的样式
        handleValueStyle(sb, numberColor, 6, sb.length() - 1);
        // 设置]的样式
        handleValueStyle(sb, textColor, sb.length() - 1, sb.length());
        itemView.setOnClickListener(new JsonItemViewClickListener(
                value,
                appendComma,
                hierarchy + 1
        ));
    }

    /**
     * 处理值的样式
     *
     * @param sb    需要处理的样式
     * @param color 设置的颜色
     * @param start 起始位置
     * @param end   结束位置
     */
    private void handleValueStyle(SpannableStringBuilder sb, int color, int start, int end) {
        sb.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 绑定json数据。
     *
     * @param itemView   展示item数据的view
     * @param position   该item的位置
     * @param jsonObject JSONObject对象
     * @param jsonArray  JSONArray对象
     */
    private void handleJsonData(JSONItemView itemView, int position,
                                JSONObject jsonObject, JSONArray jsonArray) {
        boolean isJsonObject = jsonObject != null;
        boolean isStart = position == 0;
        boolean isEnd = position == getItemCount() - 1;
        if (isStart || isEnd) {
            // 处理第一个和最后一个item，如果是对象展示{}, 是数组展示[]
            itemView.hideKey();
            itemView.hideIcon();
            String str = isStart ? (isJsonObject ? "{" : "[") : (isJsonObject ? "}" : "]");
            SpannableString string = new SpannableString(str);
            string.setSpan(new ForegroundColorSpan(textColor),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            itemView.setValueText(string);
        } else {
            if (isJsonObject) {
                jsonArray = jsonObject.names();
                if (jsonArray != null) {
                    String key = jsonArray.optString(position - 1);
                    Object value = jsonObject.opt(key);
                    handleJSONObject(itemView, key, value,
                            // 最后一个数据不用加逗号
                            position < getItemCount() - 2, 1
                    );
                }
            } else {
                Object value = jsonArray.opt(position - 1);
                handleJSONArray(itemView, value,
                        // 最后一个数据不用加逗号
                        position < getItemCount() - 2, 1
                );
            }
        }
    }

    /**
     * 处理JSONObject类型的JsonItemView样式
     *
     * @param itemView    要处理的JsonItemView对象
     * @param key         要在JsonItemView展示的key
     * @param value       要在JsonItemView展示的value
     * @param appendComma 是否附加逗号
     * @param hierarchy   View的层次结构数量
     */
    private void handleJSONObject(JSONItemView itemView, String key,
                                  Object value, boolean appendComma, int hierarchy) {
        // 处理JsonItemView左边的样式
        SpannableStringBuilder sb = new SpannableStringBuilder(StringUtils.getHierarchyStr(hierarchy));
        // "key":
        sb.append("\"").append(key).append("\"").append(":");
        handleValueStyle(sb, keyColor, 0, sb.length() - 1);
        handleValueStyle(sb, textColor, sb.length() - 1, sb.length());
        itemView.setKeyText(sb);
        // 处理JsonItemView右边的样式
        handleValueStyle(itemView, value, appendComma, hierarchy);
    }

    /**
     * 处理JSONArray类型的JsonItemView样式
     *
     * @param itemView    要处理的JsonItemView对象
     * @param value       要在JsonItemView展示的value
     * @param appendComma 是否附加逗号
     * @param hierarchy   View的层次结构数量
     */
    private void handleJSONArray(JSONItemView itemView, Object value,
                                 boolean appendComma, int hierarchy) {
        // 处理JsonItemView左边的样式
        itemView.setKeyText(new SpannableStringBuilder(StringUtils.getHierarchyStr(hierarchy)));
        // 处理JsonItemView右边的样式
        handleValueStyle(itemView, value, appendComma, hierarchy);
    }

    static class JsonItemViewHolder extends RecyclerView.ViewHolder {
        private final JSONItemView itemView;

        private JsonItemViewHolder(@NonNull JSONItemView itemView) {
            super(itemView);
            this.itemView = itemView;
            // 设置item不可回收
            setIsRecyclable(false);
        }
    }

    /**
     * 导航到浏览器。
     *
     * @param url 一个符合RFC 2396的编码URI
     */
    private void navigateToBrowser(String url) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    class JsonItemViewLongClickListener implements View.OnLongClickListener {

        private final String value;

        JsonItemViewLongClickListener(String value) {
            this.value = value;
        }

        @Override
        public boolean onLongClick(View view) {
            ViewUtils.copy(mContext, value);
            Toast.makeText(mContext, mContext.getString(R.string.toast_copy_text) + value,
                    Toast.LENGTH_SHORT
            ).show();
            return true;
        }
    }

    class JsonItemViewClickListener implements View.OnClickListener {

        private final Object value;
        private Boolean appendComma;
        private final Integer hierarchy;

        JsonItemViewClickListener(Object value, Boolean appendComma, Integer hierarchy) {
            this.value = value;
            this.appendComma = appendComma;
            this.hierarchy = hierarchy;
        }

        // 判断是否展开
        private boolean isExpanded = false;

        public void onClick(View view) {
            // 如果itemView的子View数量是1，就证明这是第一次展开
            JSONItemView itemView = (JSONItemView) view;
            if (itemView.getChildCount() == 1) {
                performFirstExpand(itemView);
            } else {
                performClick(itemView);
            }
        }

        /**
         * 第一次展开JSONObject或者JSONArray对应的itemView。
         */
        private void performFirstExpand(JSONItemView itemView) {
            isExpanded = true;
            itemView.showIcon(false);
            itemView.setTag(itemView.getRightText());
            itemView.setValueText(isJsonObject() ? "{" : "[");

            // 展开该层级以下的视图
            JSONArray array = isJsonObject() ? ((JSONObject) value).names() : (JSONArray) value;
            int length = array == null ? 0 : array.length();
            for (int i = 0; i < length; i++) {
                JSONItemView view = new JSONItemView(itemView.getContext());
                view.setTextSize(mTextSize);
                view.setKeyTextColor(textColor);
                Object childValue = array.opt(i);
                if (isJsonObject()) {
                    handleJSONObject(
                            view,
                            String.valueOf(childValue),
                            ((JSONObject) value).opt(String.valueOf(childValue)),
                            i < length - 1,
                            hierarchy
                    );
                } else {
                    handleJSONArray(
                            view,
                            childValue,
                            appendComma = i < length - 1,
                            hierarchy
                    );
                }
                itemView.addViewNoInvalidate(view);
            }
            // 展示该层级最后的一个视图
            JSONItemView view = new JSONItemView(itemView.getContext());
            view.setTextSize(mTextSize);
            view.setKeyTextColor(textColor);
            String sb = StringUtils.getHierarchyStr(hierarchy - 1)
                    + (isJsonObject() ? "}" : "]")
                    + (appendComma ? "," : "");
            view.setValueText(sb);
            itemView.addViewNoInvalidate(view);

            // 重绘itemView
            itemView.requestLayout();
            itemView.invalidate();
        }

        /**
         * 点击后展开或者收缩。
         */
        private void performClick(JSONItemView itemView) {
            itemView.showIcon(isExpanded);
            CharSequence rightText = itemView.getRightText();
            itemView.setValueText((CharSequence) itemView.getTag());
            itemView.setTag(rightText);
            for (int i = 1; i < itemView.getChildCount(); i++) {
                // 如果展开的话，就把子View都设成可见状态，否则就设为隐藏状态
                itemView.getChildAt(i).setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            }
            isExpanded = !isExpanded;
        }

        // 判断是否为JSONObject对象
        private boolean isJsonObject() {
            return value instanceof JSONObject;
        }
    }
}