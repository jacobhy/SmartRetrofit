package com.jacob.www.smartretrofit.http;

/**
 * @className: RetrofitCallback
 * @classDescription: 回调接口，数据统一转换成String输出
 * @author: jacobHy
 * @createTime: 2017/10/31
 */
public interface RetrofitCallback<String> {

    // 请求数据成功
    void onSuccess(String response);

    // 请求数据错误
    void onError(String err_msg);

    // 网络请求失败或其他错误
    void onFailure(String exception);

    // 请求开始
    void onStart();

    // 请求结束
    void onFinish();
}
