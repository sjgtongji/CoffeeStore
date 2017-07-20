package com.store.buzztime.coffee_store

import android.util.Log
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import com.store.buzztime.coffee_store.http.*

/**
 * Created by sjg on 2017/7/19.
 */
class HttpUtils {
    fun <T> post(url : String, params : String , callback: HttpCallback<T>){
        var body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), params)
        var baseResp : HttpBaseResp = HttpBaseResp();
        url.request().post(body).rxExecute()
                .map({r -> r.body().string()})
                .observeOnMain()
                .subscribeSafeNext { result ->
                    Log.d(TAG, result)
                    if(Settings.TEST_REST){
                        callback.onSuccess(callback.onTestRest());
                    }else{
                        baseResp = toResp(result);
                        when(baseResp.code){
                            200 -> {
                                var resp : T = gson.fromJson(baseResp.value , callback.claze) as T;
                                callback.onSuccess(resp);
                            }
                            else -> {
                                callback.onFail(baseResp)
                            }
                        }
                    }
                }
    }

    fun <T> get(url : String  , callback: HttpCallback<T>){
        var baseResp : HttpBaseResp = HttpBaseResp();
        url.request().get().rxExecute()
                .map({r -> r.body().string()})
                .observeOnMain()
                .subscribeSafeNext { result ->
                    Log.d(TAG, result)
                    if(Settings.TEST_REST){
                        callback.onSuccess(callback.onTestRest());
                    }else{
                        baseResp = toResp(result);
                        when(baseResp.code){
                            200 -> {
                                var resp : T = gson.fromJson(baseResp.value , callback.claze) as T;
                                callback.onSuccess(resp);
                            }
                            else -> {
                                callback.onFail(baseResp)
                            }
                        }
                    }
                }
    }
}