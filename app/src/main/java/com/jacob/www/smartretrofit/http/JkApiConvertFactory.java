package com.jacob.www.smartretrofit.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @className: JkApiConvertFactory
 * @classDescription: this converter decode the response.
 * @author: jacobHy
 * @createTime: 2016/8/30
 */
public class JkApiConvertFactory extends Converter.Factory {

    public static JkApiConvertFactory create() {
        return new JkApiConvertFactory();
    }

    private JkApiConvertFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new JKResponseBodyConverter<>();
    }

    final class JKResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        @Override
        public T convert(ResponseBody value)  {
            try {
                String reString = value.string();
                if ("error".equals(reString))
                    throw new Exception("error");
                return (T) reString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                value.close();
            }
        }
    }
}
