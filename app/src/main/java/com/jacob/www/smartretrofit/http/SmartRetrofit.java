package com.jacob.www.smartretrofit.http;

import android.app.Activity;

import com.jacob.www.smartretrofit.utils.AppManager;
import com.jacob.www.smartretrofit.utils.SmartLog;
import com.jacob.www.smartretrofit.utils.StringUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @className: SmartRetrofit
 * @classDescription: retrofit封装库
 * @author: jacobHy
 * @createTime 2017/10/31
 */
public class SmartRetrofit {
    public static final String TAG                                = "SmartRetrofit";
    //        数据为空
    public static final String NULL_DATA                          = "无数据";
    //        页面弱引用为空
    public final static String ACTIVITY_WEAK_REF_IS_NULL          =
            "activity weak ref is null";
    //        回调更新页面非当前页面
    public final static String UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE =
            "update ui page is not request current page";
    //        instance
    private static SmartRetrofit           smartRetrofitInstance;
    //        请求的完整链接
    private        String                  url;
    //        请求参数
    private        Map<String, String>     params;
    //        请求参数：body
    private        RequestBody             body;
    //        头部参数
    private        Map<String, String>     headerParams;
    //        超时时间
    private static int                     CONNECT_TIMEOUT;
    private static int                     READ_TIMEOUT;
    private static int                     WRITE_TIMEOUT;
    //        Api实例
    private static ApiService              mApiService;
    //        Builder实例
    private        Builder                 mBuilder;
    //        页面弱引用
    private        WeakReference<Activity> mActivityWeakRef;
    private static OKHttpIntercepter       mOkHttpIntercepter;
    private static Retrofit                mRetrofit;
    private static OkHttpClient            mClient;
    private static OkHttpClient.Builder    mClientBuilder;
    private        List<Observable>        mCallList;

    // getInstance
    private static SmartRetrofit getInstance(Builder builder) {
        SmartLog.printLogs(SmartLog.TAG, smartRetrofitInstance == null ? "null" : "exist");
        if (smartRetrofitInstance != null) {
            // new
            synchronized (SmartRetrofit.class) {
                // 传入构建的参数`
                smartRetrofitInstance.mBuilder = builder;
                smartRetrofitInstance.mActivityWeakRef = new WeakReference<Activity>(builder.activity);
                smartRetrofitInstance.url = builder.url;
                smartRetrofitInstance.params = builder.params;
                smartRetrofitInstance.body = builder.body;
                smartRetrofitInstance.headerParams = builder.headerParams;
                CONNECT_TIMEOUT = builder.connect_timeout;
                READ_TIMEOUT = builder.read_timeout;
                WRITE_TIMEOUT = builder.write_timeout;
                // rebuild okhttp
                mOkHttpIntercepter = new OKHttpIntercepter().setHeaders(builder.headerParams);
                mClientBuilder.interceptors().clear();
                mClientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
                mClientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
                mClientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
                mClient = mClientBuilder.addInterceptor(mOkHttpIntercepter).build();
                mRetrofit = new Retrofit.Builder()
                        .addConverterFactory(ApiConvertFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                        .baseUrl("https://acgi.jianke.com/")
                        .client(mClient)
                        .build();
                // createService
                mApiService = mRetrofit.create(ApiService.class);
            }
        } else {
            // new
            synchronized (SmartRetrofit.class) {
                if (smartRetrofitInstance == null)
                    smartRetrofitInstance = new SmartRetrofit(builder);
            }
        }
        return smartRetrofitInstance;
    }

    // construct
    private SmartRetrofit(Builder builder) {
        // 传入建造的参数`
        mBuilder = builder;
        mActivityWeakRef = new WeakReference<Activity>(builder.activity);
        url = builder.url;
        params = builder.params;
        body = builder.body;
        headerParams = builder.headerParams;
        CONNECT_TIMEOUT = builder.connect_timeout;
        READ_TIMEOUT = builder.read_timeout;
        WRITE_TIMEOUT = builder.write_timeout;
        // init
        mClientBuilder = new OkHttpClient.Builder();
        // 设置Dispatcher
//        Dispatcher mDispatcher = new Dispatcher();
//        mDispatcher.setMaxRequests(100);
//        mDispatcher.setMaxRequestsPerHost(100);
//        mClientBuilder.dispatcher(mDispatcher);
        // 设置超时
        mClientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        mClientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        mClientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        // 错误重连
        mClientBuilder.retryOnConnectionFailure(true);
        // 添加拦截器
        mOkHttpIntercepter = new OKHttpIntercepter().setHeaders(headerParams);
        mClient = mClientBuilder.addInterceptor(mOkHttpIntercepter).build();
        // 构建
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(ApiConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .baseUrl("https://acgi.jianke.com/")
                .client(mClient)
                .build();
        // createService
        mApiService = mRetrofit.create(ApiService.class);
        //
        mCallList = new ArrayList<>();
    }

    /**
     * 清除引用
     *
     * @param
     * @return
     * @createTime 2017/12/28
     * @lastModify 2017/12/28
     */
    public void clearRefer() {
        if (mBuilder != null) {
            mBuilder.activity = null;
            mBuilder = null;
        }
    }

    /**
     * 集合中所有的请求取消订阅，用于退出页面时
     *
     * @param
     * @return
     * @createTime 2018/1/3
     * @lastModify 2018/1/3
     */
    public void allCallUnsubscribe() {
        if (mCallList != null && mCallList.size() > 0) {
            for (Observable call : mCallList) {
                if (call != null)
                    call.unsubscribeOn(Schedulers.io());
                call = null;
            }
            mCallList.clear();
        }
    }

    /**
     * get请求
     *
     * @param
     * @return
     * @createTime 2017/11/9
     * @lastModify 2017/11/9
     */
    public SmartRetrofit getRequest(final RetrofitCallback<String> callback) {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call = mApiService.getRequest(url, params);
        if (mCallList != null) mCallList.add(call);
        call.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<String>() {

                    private Disposable mDisposable;

                    // 订阅开始
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        if (callback != null)
                            callback.onStart();
                        SmartLog.printLogs(TAG, "#onSubscribe");
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        if (callback != null) {
                            if (StringUtil.isNotEmpty(s)) {
                                // 成功回调
                                callback.onSuccess(s);
                                SmartLog.printLogs(TAG, "#onSuccess#responce=" + s.toString());
                            } else {
                                // 无数据回调
                                callback.onError(SmartRetrofit.NULL_DATA);
                                SmartLog.printLogs(TAG, "#onSuccess#null_data");
                            }
                        }
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onFailure#" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onFailure#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        String errResponse = "";
                        // 错误回调
                        if (e instanceof HttpException) {
                            try {
                                String errBody = ((HttpException) e).response().errorBody().string();
                                errResponse = errBody;
                            } catch (IOException e1) {
                                errResponse = e.toString();
                                errResponse = e.toString();
                                if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                            }
                        } else {
                            errResponse = e.toString();
                            if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                        }
                        SmartLog.printLogs(TAG, "#onFailure#" + errResponse);
                        if (callback != null) {
                            callback.onFailure(errResponse);
                            callback.onFinish();
                        }
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        SmartLog.printLogs(TAG, "#onComplete");
                        if (callback != null)
                            callback.onFinish();
                        clearRefer();
                    }
                });
        return smartRetrofitInstance;
    }

    /**
     * post请求
     *
     * @param
     * @return
     * @createTime 2017/12/25
     * @lastModify 2017/12/25
     */
    public SmartRetrofit postRequest(final RetrofitCallback<String> callback) {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call;
        if (body != null) {
            call = mApiService.postRequest(url, body);
        } else {
            call = mApiService.postRequest(url, params);
        }
        if (mCallList != null) mCallList.add(call);
        call.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<String>() {

                    private Disposable mDisposable;

                    // 订阅开始
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        if (callback != null)
                            callback.onStart();
                        SmartLog.printLogs(TAG, "#onSubscribe");
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        if (callback != null)
                            if (StringUtil.isNotEmpty(s)) {
                                // 成功回调
                                callback.onSuccess(s);
                                SmartLog.printLogs(TAG, "#onSuccess#responce=" + s.toString());
                            } else {
                                // 无数据回调
                                callback.onError(SmartRetrofit.NULL_DATA);
                                SmartLog.printLogs(TAG, "#onSuccess#null_data");
                            }
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onFailure#" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onFailure#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        String errResponse = "";
                        // 错误回调
                        if (e instanceof HttpException) {
                            try {
                                String errBody = ((HttpException) e).response().errorBody().string();
                                errResponse = errBody;
                            } catch (IOException e1) {
                                errResponse = e.toString();
                                if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                            }
                        } else {
                            errResponse = e.toString();
                            errResponse = e.toString();
                            if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                        }
                        SmartLog.printLogs(TAG, "#onFailure#" + errResponse);
                        if (callback != null) {
                            callback.onFailure(errResponse);
                            callback.onFinish();
                        }
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        SmartLog.printLogs(TAG, "#onComplete");
                        if (callback != null)
                            callback.onFinish();
                        clearRefer();
                    }
                });
        return smartRetrofitInstance;
    }

    /**
     * delete请求
     *
     * @param
     * @return
     * @createTime 2017/11/21
     * @lastModify 2017/11/21
     */
    public SmartRetrofit deleteRequest(final RetrofitCallback<String> callback) {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call;
        if (body != null) {
            call = mApiService.deleteRequest(url, body);
        } else {
            call = mApiService.deleteRequest(url, params);
        }
        if (mCallList != null) mCallList.add(call);
        call.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<String>() {

                    private Disposable mDisposable;

                    // 订阅开始
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        if (callback != null)
                            callback.onStart();
                        SmartLog.printLogs(TAG, "#onSubscribe");
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        if (callback != null) {
                            if (StringUtil.isNotEmpty(s)) {
                                // 成功回调
                                callback.onSuccess(s);
                                SmartLog.printLogs(TAG, "#onSuccess#responce=" + s.toString());
                            } else {
                                // 无数据回调
                                callback.onError(SmartRetrofit.NULL_DATA);
                                SmartLog.printLogs(TAG, "#onSuccess#null_data");
                            }
                        }
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onFailure#" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onFailure#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        String errResponse = "";
                        // 错误回调
                        if (e instanceof HttpException) {
                            try {
                                String errBody = ((HttpException) e).response().errorBody().string();
                                errResponse = errBody;
                            } catch (IOException e1) {
                                errResponse = e.toString();
                                if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                            }
                        } else {
                            errResponse = e.toString();
                            if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                        }
                        SmartLog.printLogs(TAG, "#onFailure#" + errResponse);
                        if (callback != null) {
                            callback.onFailure(errResponse);
                            callback.onFinish();
                        }
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        SmartLog.printLogs(TAG, "#onComplete");
                        if (callback != null)
                            callback.onFinish();
                        clearRefer();
                    }
                });
        return smartRetrofitInstance;
    }

    /**
     * put请求
     *
     * @param
     * @return
     * @createTime 2017/11/21
     * @lastModify 2017/11/21
     */
    public SmartRetrofit putRequest(final RetrofitCallback<String> callback) {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call;
        if (body != null) {
            call = mApiService.putRequest(url, body);
        } else {
            call = mApiService.putRequest(url, params);
        }
        if (mCallList != null) mCallList.add(call);
        call.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<String>() {

                    private Disposable mDisposable;

                    // 订阅开始
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        if (callback != null)
                            callback.onStart();
                        SmartLog.printLogs(TAG, "#onSubscribe");
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onSuccess#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        if (callback != null) {
                            if (StringUtil.isNotEmpty(s)) {
                                // 成功回调
                                callback.onSuccess(s);
                                SmartLog.printLogs(TAG, "#onSuccess#responce=" + s.toString());
                            } else {
                                // 无数据回调
                                callback.onError(SmartRetrofit.NULL_DATA);
                                SmartLog.printLogs(TAG, "#onSuccess#null_data");
                            }
                        }
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onFailure#" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onFailure#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        String errResponse = "";
                        // 错误回调
                        if (e instanceof HttpException) {
                            try {
                                String errBody = ((HttpException) e).response().errorBody().string();
                                errResponse = errBody;
                            } catch (IOException e1) {
                                errResponse = e.toString();
                                if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                            }
                        } else {
                            errResponse = e.toString();
                            if (StringUtil.isEmpty(errResponse)) errResponse = "请稍后重试";
                        }
                        SmartLog.printLogs(TAG, "#onFailure#" + errResponse);
                        if (callback != null) {
                            callback.onFailure(errResponse);
                            callback.onFinish();
                        }
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        SmartLog.printLogs(TAG, "#onComplete");
                        if (callback != null)
                            callback.onFinish();
                        clearRefer();
                    }
                });
        return smartRetrofitInstance;
    }

    /**
     * 并行请求多个接口
     *
     * @param
     * @return
     * @createTime 2017/12/25
     * @lastModify 2017/12/25
     */
    public SmartRetrofit merge(Observable[] obs) {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable.mergeArrayDelayError(obs).
                subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<String>() {

                    private Disposable mDisposable;

                    // 订阅开始
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        SmartLog.printLogs(TAG, "#onSubscribe");
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onSuccess#merge=" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onSuccess#merge=" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        SmartLog.printLogs(TAG, "#onSuccess#merge=" + s);
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            SmartLog.printLogs(TAG, "#onFailure#merge=" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            SmartLog.printLogs(TAG, "#onFailure#merge=" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        String errResponse = "";
                        // 错误回调
                        if (e instanceof HttpException) {
                            try {
                                String errBody = ((HttpException) e).response().errorBody().string();
                                errResponse = errBody;
                            } catch (IOException e1) {
                                errResponse = e.toString();
                            }
                        }
                        SmartLog.printLogs(TAG, "#onFailure#merge=" + errResponse);
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        SmartLog.printLogs(TAG, "#onComplete");
                        clearRefer();
                    }
                });
        return smartRetrofitInstance;
    }

    /**
     * get请求：获取Observable，用于合并请求
     *
     * @param
     * @return
     * @createTime 2017/12/25
     * @lastModify 2017/12/25
     */
    public Observable getRequestObservable() {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call = mApiService.getRequest(url, params);
        if (mCallList != null) mCallList.add(call);
        return call;
    }

    /**
     * post请求：获取Observable，用于合并请求
     *
     * @param
     * @return
     * @createTime 2017/12/25
     * @lastModify 2017/12/25
     */
    public Observable postRequestObservable() {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call;
        if (body != null) {
            call = mApiService.postRequest(url, body);
        } else {
            call = mApiService.postRequest(url, params);
        }
        if (mCallList != null) mCallList.add(call);
        return call;
    }

    /**
     * put请求：获取Observable，用于合并请求
     *
     * @param
     * @return
     * @createTime 2017/12/25
     * @lastModify 2017/12/25
     */
    public Observable putRequestObservable() {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call;
        if (body != null) {
            call = mApiService.putRequest(url, body);
        } else {
            call = mApiService.putRequest(url, params);
        }
        if (mCallList != null) mCallList.add(call);
        return call;
    }

    /**
     * delete请求：获取Observable，用于合并请求
     *
     * @param
     * @return
     * @createTime 2017/12/25
     * @lastModify 2017/12/25
     */
    public Observable deleteRequestObservable() {
        if (smartRetrofitInstance == null || mApiService == null) return null;
        Observable<String> call;
        if (body != null) {
            call = mApiService.deleteRequest(url, body);
        } else {
            call = mApiService.deleteRequest(url, params);
        }
        if (mCallList != null) mCallList.add(call);
        return call;
    }

    interface ApiService {
        // POST请求：表单
        @FormUrlEncoded
        @POST
        Observable<String> postRequest(@Url String url, @FieldMap Map<String, String> params);

        // POST请求：body
        @POST
        Observable<String> postRequest(@Url String url, @Body RequestBody body);

        // GET请求
        @GET
        Observable<String> getRequest(@Url String url, @QueryMap Map<String, String> params);

        // DELETE请求：表单
        @DELETE
        Observable<String> deleteRequest(@Url String url, @QueryMap Map<String, String> params);

        // DELETE请求：body
        @HTTP(method = "DELETE", hasBody = true)
        Observable<String> deleteRequest(@Url String url, @Body RequestBody body);

        // PUT请求：表单
        @PUT
        Observable<String> putRequest(@Url String url, @QueryMap Map<String, String> params);

        // PUT请求：body
        @PUT
        Observable<String> putRequest(@Url String url, @Body RequestBody body);
    }

    /**
     * @className: Builder
     * @classDescription: 建造者模式构建
     * @createTime: 2017/10/12
     */
    public static class Builder {
        // 请求的完整链接
        private String              url;
        // 请求参数，表单
        private Map<String, String> params;
        // 请求参数，body
        private RequestBody         body;
        // 头部参数
        private Map<String, String> headerParams;
        // 超时时间
        private int connect_timeout = 10;
        private int read_timeout    = 10;
        private int write_timeout   = 10;
        // activity实例
        private Activity activity;

        // construct
        public Builder() {
            // new
            params = new HashMap<>();
            headerParams = new HashMap<>();
        }

        // 设置url
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        // 设置参数
        public Builder setParams(Map<String, String> params) {
            if (params != null) {
                this.params = params;
            }
            return this;
        }

        // 设置参数：body
        public Builder setParams(RequestBody body) {
            if (body != null) {
                this.body = body;
            }
            return this;
        }

        // 设置头部参数
        public Builder setHeaderParams(Map<String, String> headerParams) {
            if (headerParams != null) {
                this.headerParams = headerParams;
            }
            return this;
        }

        // 设置activity
        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        /**
         * 设置时间参数
         *
         * @param connect_timeout，单位秒
         * @param read_timeout，单位秒
         * @param write_timeout，单位秒
         * @return
         * @createTime 2017/10/13
         * @lastModify 2017/10/13
         */
        public Builder setTimeParams(int connect_timeout, int read_timeout, int write_timeout) {
            this.connect_timeout = connect_timeout;
            this.read_timeout = read_timeout;
            this.write_timeout = write_timeout;
            return this;
        }

        // 构建
        public SmartRetrofit build() {
            if (activity == null) return null;
            smartRetrofitInstance = getInstance(this);
            return smartRetrofitInstance;
        }
    }

    /**
     * 创建SmartRetrofit对象
     *
     * @param activity
     * @param url
     * @param headerParams
     * @param params
     * @param body
     * @return
     * @createTime: 2018/2/7
     * @lastModify: 2018/2/7
     */
    public static SmartRetrofit createSmartRetrofit(
            Activity activity,
            String url,
            Map<String, String> headerParams,
            Map<String, String> params,
            RequestBody body) {
        return new SmartRetrofit.Builder()
                .setActivity(activity)
                .setUrl(url)
                .setHeaderParams(headerParams)
                .setParams(params)
                .setParams(body)
                .build();
    }
}
