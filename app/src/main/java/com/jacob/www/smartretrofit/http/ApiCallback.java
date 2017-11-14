package com.jacob.www.smartretrofit.http;

/**
 * @className: ApiCallback
 * @classDescription: 回调接口，数据统一转换成String输出
 * @author: jacobHy
 * @createTime: 2017/10/31
 */
public abstract class ApiCallback<String> {

    // 请求数据成功
    public abstract void onSuccess(String response);

    // 请求数据错误
    public abstract void onError(String err_msg);

    // 网络请求失败
    public abstract void onFailure(String exception);

    // 请求开始
    public abstract void onStart();

    // 请求结束
    public abstract void onFinish();
}
