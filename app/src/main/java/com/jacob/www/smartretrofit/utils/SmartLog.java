package com.jacob.www.smartretrofit.utils;

import android.util.Log;

import com.jacob.www.smartretrofit.BuildConfig;


/**
 * @className: SmartLog
 * @classDescription: 日志答应
 * @author: jacobHy
 * @createTime: 2017/10/10
 */
public class SmartLog {

    public final static String TAG = "SmartLog";

    /**
     * 日志打印,gradle可动态设置是否打印
     * @createTime 2017/10/10
     * @lastModify 2017/10/10
     * @param tag
     * @param msg
     * @return
     */
    public static void printLogs(String tag, String msg){
        if (BuildConfig.IS_PRINT_REQUEST_LOG){
            Log.e(tag+"##--->", msg);
        }
    }
}
