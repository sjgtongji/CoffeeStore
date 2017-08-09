package com.store.buzztime.coffee_store

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import com.store.buzztime.coffee_store.http.Settings
var ACTION_KEY : String = "com.store.buzztime.coffee_store.NewOrderReceiver.orderId"
var ACTION_START : Int = 1
var ACTION_STOP : Int = 2
var ORDER_ID_KEY : String = "com.store.buzztime.coffee_store.NewOrderReceiver.orderId"
/**
 * Created by sjg on 2017/7/19.
 */
class NewOrderReceiver : BroadcastReceiver() {
    var speechHelper : SpeechHelper? = null;
    var DELAY : Long = 10000
    var START_SPEECH : Int = 0
    var handler : Handler = object  : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                START_SPEECH -> {
                    if(msgs.size > 0){
                        speechHelper!!.startSpeaking("您有新的订单")
                        msgs.removeAt(0)
                        if(msgs.size > 0){
                            sendEmptyMessageDelayed(START_SPEECH , DELAY)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    var msgs : MutableList<String> = mutableListOf()
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(speechHelper == null){
            speechHelper = SpeechHelper(p0!!)
        }
        var action = p1!!.getIntExtra(ACTION_KEY , -1)
        var orderId = p1!!.getStringExtra(ORDER_ID_KEY)
        if(orderId != null && !orderId.isEmpty()){
            when(action){
                ACTION_START -> {
                    var i : Int = 0
                    while (i < 3){
                        msgs.add(orderId)
                    }
                    handler.sendEmptyMessageDelayed(START_SPEECH , DELAY)
                }
                ACTION_STOP -> {
                    if(msgs.contains(orderId)){
                        msgs.remove(orderId)
                    }
                }
                else ->{}
            }

        }


//        p0!!.sendBroadcast(Intent(Settings.ACTION_ORDER))

    }
}