package com.jacob.www.smartretrofit.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jacob.www.smartretrofit.http.ApiCallback;
import com.jacob.www.smartretrofit.http.SmartRetrofit;
import com.jacob.www.smartretrofit.utils.AppManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpDemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
//        get方法示例
        Map<String, String> map = new HashMap<>();
        map.put("amount", "4");
        new SmartRetrofit.Builder().setUrl("https://acgi.jianke.com/promos/promos/app/11692")
                .setParams(map).setActivity(this).build().getRequest(new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Gson  gson = new Gson();
                DemoBean bean = gson.fromJson(response, DemoBean.class);
                System.out.println("!!!!!!!!!! response=" + bean.getItem().getPromotions());
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

        //        post方法示例
        Map<String, String> map2 = new HashMap<>();
        map2.put("Authorization", "bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTA1NjQxNDksInVzZXJfbmFtZSI6Ijk3NkQ2NzYwLTJDOEYtNDlGRS1BMkNFLUE4QTFCQkVDRjQxRSIsImp0aSI6IjRjYjhmZTYwLWY3MGUtNDIyZi1hMDM0LWEyYmFmNzBlNWVlZSIsImNsaWVudF9pZCI6Im1hbGxfYXBwIiwic2NvcGUiOlsib3BlbmlkIl19.eti1PqAZzQxug5GxqizqWiQ2HwRy60IRgpJYzcbPjfoNWQkF7jsHXAnNJgd376N65I6qfpIMhDjlKBCjUGbNKN1JcX1uyWl0nEP2MQxcpvW_OhChB1g4Smxz5MmBy8JrbgfbeY6ZJWDia5gjM-DSdtPe1eLRmYnyKTRsHdYsCn4");
        map2.put("networkType", "WIFI");
        map2.put("screen", "1080*1920");
        map2.put("userId", "3F90BD3F-4F9D-4ECD-B1F4-DFD50BBC25E4");
        JSONObject          jsonObject = new JSONObject(map2);
        String              body       = jsonObject.toString();
        Map<String, String> bodyMap    = new HashMap<>();
        bodyMap.put("body", body);
        new SmartRetrofit.Builder()
                .setActivity(this)
//                .setUrl("http://172.17.30.115:8089/api/draws/share?activityId=1")
                .setUrl("https://mbp.jianke.com/mbm/mall/api/HomePage/getNavigationBarAndSearchTitle")
                .setParams(bodyMap).build()
                .postRequest(new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println("!!!!!!!!!! response=" + response);
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
