package com.store.buzztime.coffee_store.http

/**
 * Created by jigangsun on 2017/6/21.
 */

class Settings{
    companion object{
        //0:未确认；1：已确认；2：取消；3：已配送；4：已完成；5：门店接单；6：骑手取餐；7：骑手送餐中
        var ORDER_INIT : Int = 0
        var ORDER_CONFIRM : Int = 1
        var ORDER_CANCEL : Int = 2
        var ORDER_DISTRIBUTE_FINISH : Int = 3
        var ORDER_FINISH : Int = 4
        var ORDER_STORE_CONFIRM : Int = 5
        var ORDER_RIDER_GET : Int = 6
        var ORDER_RIDER_POST : Int = 7

        var PREF_IS_SERVICE_STARTED : String = "com.store.buzztime.coffee_store.http.isStarted"
        var ACTION_ORDER : String = "com.store.buzztime.coffee_store.http.order"
        var DEBUG : Boolean = true;
        var TEST_REST : Boolean = false;
        var SERVER_DEBUG = "http://139.196.228.248:52072/Rest/CoffeeService/"
        var SERVER_RELEASE = "http://waimai.buzztimecoffee.com/";
        var LOGIN = "getManager";
        var GET_UNRECEIVE_ORDERS = "getOrderByResUUID";
        var GET_BUSINESS_HOURWEEKTYPE = "GetBusinessHourWeekType"
        var POST_BUSINESS_HOUR = "setBusinessHourWeekState" //id=? state=?
        var POST_ORDER_STATE = "setOrderState"
        lateinit var LOGIN_URL : String;
        lateinit var GET_UNRECEIVE_ORDERS_URL : String;
        lateinit var GET_BUSINESS_HOURWEEKTYPE_URL : String
        lateinit var POST_BUSINESS_HOUR_URL : String
        lateinit var POST_ORDER_STATE_URL : String


        init {
            if(DEBUG){
                LOGIN_URL = SERVER_DEBUG + LOGIN
                GET_UNRECEIVE_ORDERS_URL = SERVER_DEBUG + GET_UNRECEIVE_ORDERS
                GET_BUSINESS_HOURWEEKTYPE_URL = SERVER_DEBUG + GET_BUSINESS_HOURWEEKTYPE
                POST_BUSINESS_HOUR_URL = SERVER_DEBUG + POST_BUSINESS_HOUR
                POST_ORDER_STATE_URL = SERVER_DEBUG + POST_ORDER_STATE
            }else{
                LOGIN_URL = SERVER_RELEASE + LOGIN
                GET_UNRECEIVE_ORDERS_URL = SERVER_RELEASE + GET_UNRECEIVE_ORDERS
                GET_BUSINESS_HOURWEEKTYPE_URL = SERVER_RELEASE + GET_BUSINESS_HOURWEEKTYPE
                POST_BUSINESS_HOUR_URL = SERVER_RELEASE + POST_BUSINESS_HOUR
                POST_ORDER_STATE_URL = SERVER_RELEASE + POST_ORDER_STATE
            }
        }
    }
}