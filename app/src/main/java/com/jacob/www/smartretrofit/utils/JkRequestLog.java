package com.jacob.www.smartretrofit.utils;

import android.util.Log;

/**
 * @className: JkRequestLog
 * @classDescription: 请求log
 * @author:
 * @createTime: 2017/5/3
 */
public class JkRequestLog {
    /**
     * 日志打印
     *
     * @author
     * @createTime 2017/5/3
     * @lastModify 2017/5/3
     * @param tag
     * @param msg
     * @return
     */
    public static void printLogs(String tag, String msg){
            Log.e(tag, msg);
    }
}
