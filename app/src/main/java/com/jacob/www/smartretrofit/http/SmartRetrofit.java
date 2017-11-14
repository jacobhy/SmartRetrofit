package com.jacob.www.smartretrofit.http;

import android.app.Activity;

import com.jacob.www.smartretrofit.utils.AppManager;
import com.jacob.www.smartretrofit.utils.BaseApplication;
import com.jacob.www.smartretrofit.utils.CommonUtility;
import com.jacob.www.smartretrofit.utils.JkRequestLog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @className: SmartRetrofit
 * @classDescription: retrofit封装库
 * @author: jacobHy
 * @createTime 2017/10/31
 */
public class SmartRetrofit {
    public static final  String TAG                                = "SmartRetrofit";
    public static final  String NULL_DATA                          = "无数据";
    // 页面弱引用为空
    private final static String ACTIVITY_WEAK_REF_IS_NULL          = "activity weak ref is null";
    // 回调更新页面非当前页面
    private final static String UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE = "update ui page is not " +
            "request current page";
    //        instance
    private        SmartRetrofit           smartRetrofitInstance;
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
    private final  ApiService              mApiService;
    //        Builder实例
    private        Builder                 mBuilder;
    //        Activity实例
    // 页面弱引用
    private        WeakReference<Activity> mActivityWeakRef;

    //    construct
    private SmartRetrofit(Builder builder) {
//        传入建造的参数
        mBuilder = builder;
        mActivityWeakRef = new WeakReference<Activity>(builder.activity);
        url = builder.url;
        params = builder.params;
        body = builder.body;
        headerParams = builder.headerParams;
        CONNECT_TIMEOUT = builder.connect_timeout;
        READ_TIMEOUT = builder.read_timeout;
        WRITE_TIMEOUT = builder.write_timeout;
//        init
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //设置超时
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        // 错误重连
        clientBuilder.retryOnConnectionFailure(true);
        // 添加拦截器
        OkHttpClient client = clientBuilder.addInterceptor(new OkHttpIntercepter()).build();
        // 构建
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JkApiConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .baseUrl("https://acgi.jianke.com/")
                .client(client)
                .build();
        // createService
        mApiService = retrofit.create(ApiService.class);
        // createInstance
        smartRetrofitInstance = this;
    }

    /**
     * get请求
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2017/11/9
     * @lastModify 2017/11/9
     */
    public void getRequest(final ApiCallback<String> callback) {
        if (smartRetrofitInstance == null) return;
        Observable<String> call = mApiService.getRequest(url, params);
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
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            JkRequestLog.printLogs(TAG, "#" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            JkRequestLog.printLogs(TAG, "#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        if (callback != null)
                            if (s != null && !"".equals(s)) {
                                // 成功回调
                                callback.onSuccess(s);
                            } else {
                                // 无数据回调
                                callback.onError(SmartRetrofit.NULL_DATA);
                            }
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            JkRequestLog.printLogs(TAG, "#" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            JkRequestLog.printLogs(TAG, "#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        // 错误回调
                        if (callback != null) {
                            callback.onFailure(e.toString());
                            callback.onFinish();
                        }
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        if (callback != null)
                            callback.onFinish();
                        clearRefer();
                    }

                    // 清除引用
                    private void clearRefer() {
                        if (mDisposable != null) mDisposable.dispose();
                        mBuilder.activity = null;
                        mBuilder = null;
                        if (mActivityWeakRef != null)
                            mActivityWeakRef = null;
                        if (smartRetrofitInstance != null)
                            smartRetrofitInstance = null;
                    }
                });
    }

    /**
     * post请求
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2017/11/9
     * @lastModify 2017/11/9
     */
    public void postRequest(final ApiCallback<String> callback) {
        if (smartRetrofitInstance == null) return;
        Observable<String> call;
        if (body != null) {
            call = mApiService.postRequest(url, body);
        } else {
            call = mApiService.postRequest(url, params);
        }
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
                    }

                    // 成功返回操作
                    @Override
                    public void onNext(String s) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            JkRequestLog.printLogs(TAG, "#" + ACTIVITY_WEAK_REF_IS_NULL);
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            JkRequestLog.printLogs(TAG, "#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            return;
                        }
                        if (callback != null)
                            if (s != null && !"".equals(s)) {
                                // 成功回调
                                callback.onSuccess(s);
                            } else {
                                // 无数据回调
                                callback.onError(SmartRetrofit.NULL_DATA);
                            }
                    }

                    // 请求过程中发生错误
                    @Override
                    public void onError(Throwable e) {
                        // 若实例被回收，不回调
                        if (mActivityWeakRef == null || mActivityWeakRef.get() == null) {
                            JkRequestLog.printLogs(TAG, "#" + ACTIVITY_WEAK_REF_IS_NULL);
                            onComplete();
                            return;
                        }
                        // 若实例不在堆栈中，不回调
                        if (!AppManager.getInstance().hasActivity(mActivityWeakRef.get())) {
                            JkRequestLog.printLogs(TAG, "#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
                            onComplete();
                            return;
                        }
                        // 错误回调
                        if (callback != null) {
                            callback.onFailure(e.toString());
                            callback.onFinish();
                        }
                        clearRefer();
                    }

                    // 请求结束
                    @Override
                    public void onComplete() {
                        if (callback != null)
                            callback.onFinish();
                        clearRefer();
                    }

                    // 清除引用
                    private void clearRefer() {
                        if (mDisposable != null) mDisposable.dispose();
                        mBuilder.activity = null;
                        mBuilder = null;
                        if (mActivityWeakRef != null)
                            mActivityWeakRef = null;
                        if (smartRetrofitInstance != null)
                            smartRetrofitInstance = null;
                    }
                });
    }

    /**
     * @className: ApiService
     * @classDescription:
     * @author: yuzhenhong
     * @createTime: 2017/11/14
     */
    interface ApiService {
        // POST请求：表单
        @FormUrlEncoded
        @POST
        Observable<String> postRequest(@Url String url, @FieldMap Map<String, String> params);

        // POST请求：body
        @FormUrlEncoded
        @POST
        Observable<String> postRequest(@Url String url, @Body RequestBody body);

        // GET请求
        @GET
        Observable<String> getRequest(@Url String url, @QueryMap Map<String, String> params);
    }

    /**
     * @className: OkHttpIntercepter
     * @classDescription: 拦截器
     * @author: jacobHy
     * @createTime: 2017/10/13
     */
    class OkHttpIntercepter implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder requestBuilder
                    = original.newBuilder();
//            设置基础头部
            requestBuilder
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .addHeader("Accept", "application/json");
//            专用头部
            requestBuilder
                    .addHeader("originType", "43")
                    .addHeader("uniquedId", CommonUtility.getUniqueId(BaseApplication.getInstance()))
                    .addHeader("platform", "APP");
//            添加动态头部
            if (headerParams != null && headerParams.size() > 0) {
                for (String key : headerParams.keySet()) {
                    requestBuilder.addHeader(key, headerParams.get(key));
                }
            }
            Request request = requestBuilder.build();
//            日志打印
            String requestStartMessage = "--> " + request.method() + ' ' + request.url();
            JkRequestLog.printLogs(TAG, "#requestStartMessage = " + requestStartMessage);
            Response response = chain.proceed(request);
            return response;
        }
    }

    /**
     * @className: Builder
     * @classDescription: 建造者模式构建
     * @author: jacobHy
     * @createTime: 2017/10/12
     */
    public static class Builder {
        //        请求的完整链接
        private String              url;
        //        请求参数，表单
        private Map<String, String> params;
        //        请求参数，body
        private RequestBody         body;
        //        头部参数
        private Map<String, String> headerParams;
        //        超时时间
        private int connect_timeout = 10;
        private int read_timeout    = 10;
        private int write_timeout   = 10;
        //        activity实例
        private Activity activity;

        //        construct
        public Builder() {
//            new
            params = new HashMap<>();
            headerParams = new HashMap<>();
        }

        //        设置url
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        //        设置参数
        public Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        //        设置参数：body
        public Builder setParams(RequestBody body) {
            this.body = body;
            return this;
        }

        //        设置头部参数
        public Builder setHeaderParams(Map<String, String> headerParams) {
            this.headerParams = headerParams;
            return this;
        }

        //        设置activity
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
         * @author jacobHy
         * @createTime 2017/10/13
         * @lastModify 2017/10/13
         */
        public Builder setTimeParams(int connect_timeout, int read_timeout, int write_timeout) {
            this.connect_timeout = connect_timeout;
            this.read_timeout = read_timeout;
            this.write_timeout = write_timeout;
            return this;
        }

        //        构建
        public SmartRetrofit build() {
            if (activity == null) return null;
            return new SmartRetrofit(this);
        }
    }
}
