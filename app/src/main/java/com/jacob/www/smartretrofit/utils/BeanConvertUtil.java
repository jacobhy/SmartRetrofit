package com.jacob.www.smartretrofit.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * @className: BeanConvertUtil
 * @classDescription: 数据模型转换工具
 * @author: jacobHy
 * @createTime: 2017/11/7
 */
public class BeanConvertUtil {
    /**
     * 转换成对象输出
     *
     * @param json
     * @param type
     * @return bean
     * @createTime 2017/11/7
     * @lastModify 2017/11/7
     */
    public static <T> T getBean(String json, Type type) {
        Gson gson = new Gson();
        try {
            if (json != null && json.length() > 0)
                return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换成对象集合输出
     *
     * @param json
     * @param type
     * @return BeanArray
     * @createTime 2017/11/7
     * @lastModify 2017/11/7
     */
    public static <T> List<T> getBeanArray(String json, Class<T> type) {
        Gson    gson     = new Gson();
        List<T> listType = gson.fromJson(json, new ListOfJson<T>(type));
        if (listType != null && listType.size() > 0)
            return listType;
        return null;
    }

    /**
     * 转换成对象集合输出
     *
     * @param array
     * @param type
     * @return al
     * @createTime 2018/1/25
     * @lastModify 2018/1/25
     */
    public static <T> List<T> getBeanArray(JSONArray array, Class<T> type) {
        if (array == null || array.length() == 0) return null;
        ArrayList<T> al = new ArrayList<T>();
        for (int i = 0; i < array.length(); i++) {
            Object o = null;
            try {
                o = array.get(i);
                al.add((T) BeanConvertUtil.getBean(o.toString(), type));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (al.size() > 0) return al;
        return null;
    }

    /**
     * 返回数据请求错误信息，格式如{"code": "0100100002","message": "无效的手机号码"}
     *
     * @param json
     * @return errMsg
     * @createTime 2018/1/11
     * @lastModify 2018/1/11
     */
    public static String getErrMsg(String json) {
        String errMsg = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            errMsg = jsonObject.optString("message");
        } catch (JSONException e) {
            errMsg = json;
        }
        return errMsg;
    }

    public static class ListOfJson<T> implements ParameterizedType {

        private Class<?> mType;

        public ListOfJson(Class<T> pType) {
            this.mType = pType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{mType};
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type getRawType() {
            return List.class;
        }
    }
}
