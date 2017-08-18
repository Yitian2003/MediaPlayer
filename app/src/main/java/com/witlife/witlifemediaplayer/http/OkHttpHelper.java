package com.witlife.witlifemediaplayer.http;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpMethod;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by bruce on 14/08/2017.
 */

public class OkHttpHelper {

    private static OkHttpHelper okHttpHelper;
    private OkHttpClient client;
    private Handler handler;

    public OkHttpHelper() {
        client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);

        handler = new Handler(Looper.getMainLooper());
    }

     public static OkHttpHelper getInstance(){
        if (okHttpHelper == null){
            return new OkHttpHelper();
        } else {
            return okHttpHelper;
        }
    }

    private Request buildRequest(String url, HttpMethod method, Map<String, String> params){

        Request.Builder builder = new Request.Builder();
        if (method == HttpMethod.GET){
            builder.url(url).get();
        } else if(method == HttpMethod.POST){
            RequestBody body = getRequestBody(params);
            builder.url(url).post(body);
        }

        return builder.build();
    }

    private RequestBody getRequestBody(Map<String, String> params) {
        FormEncodingBuilder builder = new FormEncodingBuilder();

        if(params != null){
            for(Map.Entry<String, String> entry: params.entrySet()){
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    public void httpGet(String url, BaseCallback callback){
        Request request = buildRequest(url, HttpMethod.GET, null);
        doRequest(request, callback);

    }

    private void doRequest(final Request request, final BaseCallback callback){

        callback.onBeforeRequest(request);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callbackOnFailure(callback, request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {

                callbackOnResponse(callback, response);

                if(response.isSuccessful()){
                    String result = response.body().string();

                    if(callback.mType == String.class){
                        callbackOnSuccess(callback, response, result);
                    } else {
                        Object object = new Gson().fromJson(result, callback.mType);
                        callbackOnSuccess(callback, response, object);
                    }
                } else {
                    callbackOnError(callback, response, null);
                }
            }
        });
    }

    private void callbackOnSuccess(final BaseCallback callback, final Response response, final Object object){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, object);
            }
        });
    }

    private void callbackOnError(final BaseCallback callback, final Response response, final Exception e){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, response.code(), e);
            }
        });
    }

    private void callbackOnFailure(final BaseCallback callback, final Request request, final Exception e){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }

    private void callbackOnResponse(final BaseCallback callback, final Response response){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }

    enum HttpMethod{
        GET,
        POST
    }
}
