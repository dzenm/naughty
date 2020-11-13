package com.dzenm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.crash.ActivityHelper;
import com.dzenm.crash.CrashHelper;
import com.dzenm.naughty.Naughty;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView textView;

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

        textView = findViewById(R.id.text);

        Button request = findViewById(R.id.btn_request);
        Button crash = findViewById(R.id.btn_crash);
        request.setOnClickListener(v -> provideOkHttpClient());
        crash.setOnClickListener(v -> { startActivity(new Intent(this, PersonActivity.class)); });
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
                updateText("onResponse: " + string);
            }
        });
    }

    private void updateText(String text) {
        runOnUiThread(() -> textView.setText(text));
    }
}
