package com.jacob.www.smartretrofit.http;

import com.jacob.www.smartretrofit.utils.SmartLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yuzhenhong on 2017/12/28.
 */

public class OKHttpIntercepter implements Interceptor {
	// utf8
	private static final Charset UTF8 = Charset.forName("UTF-8");
	//        头部参数
	private Map<String, String> headerParams;

	public OKHttpIntercepter setHeaders(Map<String, String> headerParams) {
		this.headerParams = headerParams;
		return this;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request original = chain.request();
		Request.Builder requestBuilder
				= original.newBuilder();
//            设置基础头部
		requestBuilder
				.addHeader("Content-Type", "application/json;charset=UTF-8")
				.addHeader("Accept", "application/json");
//            添加动态头部
		if (headerParams != null && headerParams.size() > 0) {
			for (String key : headerParams.keySet()) {
				requestBuilder.addHeader(key, headerParams.get(key));
			}
		}
		Request request = requestBuilder.build();
//            日志打印
		String   requestStartMessage = "--> " + request.method() + ' ' + request.url();
		SmartLog.printLogs(SmartRetrofit.TAG, "#url = " + requestStartMessage);
		Response response = chain.proceed(request);
		return response;
	}
}
