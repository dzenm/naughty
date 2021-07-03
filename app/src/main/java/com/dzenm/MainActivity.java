package com.dzenm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.crash.ActivityHelper;
import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.util.StringUtils;
import com.dzenm.naughty.view.JSONViewAdapter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView textView;
    private JSONViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityHelper.getInstance().push(this);

        SharedPreferences sp = getSharedPreferences("test_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hello", true);
        editor.putString("this is test", "test");
        editor.apply();

        textView = findViewById(R.id.recycler_view);
        adapter = new JSONViewAdapter(this);
        textView.setAdapter(adapter);

        Button request = findViewById(R.id.btn_request);
        Button crash = findViewById(R.id.btn_crash);
        request.setOnClickListener(v -> provideOkHttpClient());
        crash.setOnClickListener(v -> {
            startActivity(new Intent(this, PersonActivity.class));
        });

        Username username = new Username(1, "dinzhenyan", "sex", 22, "江苏省", true, 21213, 129.87f, 1263.21781);
        username.save();
    }

    /**
     * 创建自定义Ok http Client[createRetrofit]
     */
    private void provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(Naughty.getInstance().get(this))
                .build();

        String url = "https://www.wanandroid.com/article/list/0/json";
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                updateText("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d(TAG, "onResponse: " + string);
                updateText(string);
            }
        });
    }

    private void updateText(String text) {
        runOnUiThread(() -> adapter.bindData(StringUtils.formatJson(text)));
    }
}
