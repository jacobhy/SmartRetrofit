package com.jacob.www.smartretrofit.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jacob.www.smartretrofit.http.RetrofitCallback;
import com.jacob.www.smartretrofit.http.SmartRetrofit;

import java.util.HashMap;
import java.util.Map;

public class HttpDemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // post方法示例一：链式调用
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        new SmartRetrofit.Builder()
                .setActivity(this)
                .setUrl("https://baidu.com")
                .setParams(params)
                .build()
                .postRequest(new RetrofitCallback<String>() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onError(String err_msg) {

                    }

                    @Override
                    public void onFailure(String exception) {

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
        // post方法示例二：统一传参调用
        Map<String, String> headers = new HashMap<>();
        headers.put("key", "value");
        SmartRetrofit.createSmartRetrofit(
                this,
                "https://baidu.com",
                headers,
                params,
                null)
                .postRequest(new RetrofitCallback<String>() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onError(String err_msg) {

                    }

                    @Override
                    public void onFailure(String exception) {

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }
}
