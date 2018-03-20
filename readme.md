# SmartRetrofit #

## 可能是最灵活的retrofit + okhttp + gson +rxjava： ##
1. url动态传入
2. 参数动态传入
3. 头部参数动态传入
4. 接口响应返回：页面实例在堆栈中才回调相关操作
5. 线程池复用

## 如何使用： ##

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





