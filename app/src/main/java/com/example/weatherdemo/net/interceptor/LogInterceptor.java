package com.example.weatherdemo.net.interceptor;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;

public class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        String method = request.method();
        if ("POST".equals(method)) {
            try {
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        if (TextUtils.isEmpty(body.encodedValue(i))) continue;
                        if (body.encodedValue(i).contains("%")) {
//                            sb.append(body.encodedName(i) + "=" + HexUtils.hexString2String(body.encodedValue(i).replaceAll("%", ""), "UTF-8", "UTF-8") + ",");
                            sb.append(body.encodedName(i)).append("=").append(URLDecoder.decode(body.encodedValue(i), "UTF-8"));
                        } else {
                            sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                        }
                    }
                    sb.delete(sb.length() - 1, sb.length());

                    LogUtils.d("发送请求: method：" + request.method() + "\n"
                            + "url：" + request.url() + "\n"
                            + "请求头：" + request.headers() + "\n"
                            + "请求参数: " + "{" + sb.toString() + "}");
                }
            } catch (Exception e) {
                LogUtils.e("请注意，三十七行请求参数转换异常");
            }
        }
        LogUtils.d("请求响应: \n request.url() " + request.url() + " \n content：" + content);
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}