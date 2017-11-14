package com.jacob.www.smartretrofit.utils;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @className: ParamUtil
 * @classDescription: 参数工具类
 * @author: jacobHy
 * @createTime: 2017/11/7
 */
public class ParamUtil {
    /**
     * 将参数集合装换成RequestBody
     *
     * @param map
     * @return RequestBody
     * @author jacobHy
     * @createTime 2017/11/7
     * @lastModify 2017/11/7
     */
    public static RequestBody getRequestBody(Map<String, String> map) {
        // 非空判断
        if (map == null || map.size() == 0) return null;
        JSONObject  jsonObject = new JSONObject(map);
        String      bodyStr    = jsonObject.toString();
        RequestBody body       = RequestBody.create(MediaType.parse("application/json"), bodyStr);
        return body;
    }

    /**
     * 将参数集合封装成签名，项目中暂时签名key有"key"和"token"两种情况
     *
     * @param map
     * @return RequestBody
     * @author jacobHy
     * @createTime 2017/11/7
     * @lastModify 2017/11/7
     */
    public static String getSign(Map<String, String> map, String key, String signValue) {
        // 非空判断
        if (map == null || map.size() == 0) return null;
        // 排序
        SortedMap<String, String> sortedParams = new TreeMap<String, String>();
        for (String k : map.keySet()) {
            if (null != k && !"".equals(k) && null != map.get(k) && !"".equals(map.get(k)))
                sortedParams.put(k, map.get(k));
        }
        // 重新组合集合
        map.clear();
        for (String k : sortedParams.keySet()) {
            map.put(k, sortedParams.get(k));
        }
        // 签名key和value
        if (null != key && !"".equals(key) && null != signValue && !"".equals(signValue)) {
            map.put(key, signValue);
        }
        JSONObject jsonObject = new JSONObject(map);
        String     jsonStr    = jsonObject.toString();
        String     sign       = MD5Util.MD5Encode(jsonStr, "UTF-8").toUpperCase();
        return sign;
    }
}
