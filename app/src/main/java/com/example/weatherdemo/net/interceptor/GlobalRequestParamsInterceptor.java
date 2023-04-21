package com.example.weatherdemo.net.interceptor;


import android.text.TextUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * OkHttp -- 请求全局配置拦截器
 */
public class GlobalRequestParamsInterceptor implements Interceptor {

    public GlobalRequestParamsInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtils.isConnected()) {
            LogUtils.e("当前无网络连接");
//            ThreadManager.getInstance().runOnUIThread(() -> MyToast.showShortToast(Utils.getApp(), "哎呀，网络开小差去了..."));
        }
        Request originRequest = chain.request();
        RequestBody body = originRequest.body();
        Request.Builder builder = originRequest.newBuilder();

        if (originRequest.method().equalsIgnoreCase("POST")) {
            if (body != null) {
                builder.post(body);
            }
        }

        return chain.proceed(builder.build());
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    private RequestBody processApplicationJsonRequestBody(RequestBody requestBody, String token) {
        String customReq = bodyToString(requestBody);
        try {
            JSONObject obj = new JSONObject(customReq);
            if (!TextUtils.isEmpty(token)) {
                obj.put("token", token);
            }
            obj.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return RequestBody.create(requestBody.contentType(), obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RequestBody processFormDataRequestBody(RequestBody requestBody, String token) {
        RequestBody formBody;
        if (!TextUtils.isEmpty(token)) {
            formBody = new FormBody.Builder()
                    .add("token", token)
                    .add("timestamp", String.valueOf(System.currentTimeMillis()))
                    .build();
        } else {
            formBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(System.currentTimeMillis()))
                    .build();
        }
        String postBodyString = bodyToString(requestBody);
        postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
        return RequestBody.create(requestBody.contentType(), postBodyString);
    }




}
