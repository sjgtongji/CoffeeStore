package com.store.buzztime.coffee_store

import android.app.Activity
import android.app.Application
import android.content.Context
import com.store.buzztime.coffee_store.Bean.Order
import com.store.buzztime.coffee_store.http.LoginResp
import java.util.*

/**
 * Created by jigangsun on 2017/6/14.
 */


/**
 * Created by fg114 on 2016/2/22.
 */
class BaseApplication : Application() {
    var speechHelper : SpeechHelper? = null
    var loginResp : LoginResp? = null
    var unReceiveOrders : List<Order>? = null
    override fun onCreate() {
        super.onCreate()
        context = this
        speechHelper = SpeechHelper(this)
    }

    companion object {
        var activityStack = Stack<Activity>()
        lateinit var context: Context;
    }
}
