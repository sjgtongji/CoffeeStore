package com.store.buzztime.coffee_store.http

/**
 * Created by jigangsun on 2017/6/21.
 */

class Settings{
    companion object{
        var ORDER_INIT : Int = 0
        var ORDER_CONFIRM : Int = 1
        var ORDER_CANCEL : Int = 2
        var ORDER_DISTRIBUTE_FINISH : Int = 3
        var ORDER_FINISH : Int = 4
        var ORDER_STORE_CONFIRM : Int = 5
        var ORDER_RIDER_GET : Int = 6
        var ORDER_RIDER_POST : Int = 7
        var DEBUG : Boolean = true;
        var TEST_REST : Boolean = false;
        var SERVER_DEBUG = "http://139.196.228.248:52072/Rest/CoffeeService/"
        var SERVER_RELEASE = "http://waimai.buzztimecoffee.com/";
        var LOGIN = "getManager";
        var GET_UNRECEIVE_ORDERS = "getOrderByResUUID";
        lateinit var LOGIN_URL : String;
        lateinit var GET_UNRECEIVE_ORDERS_URL : String;

        init {
            if(DEBUG){
                LOGIN_URL = SERVER_DEBUG + LOGIN
                GET_UNRECEIVE_ORDERS_URL = SERVER_DEBUG
            }else{
                LOGIN_URL = SERVER_RELEASE + GET_UNRECEIVE_ORDERS
                GET_UNRECEIVE_ORDERS_URL = SERVER_RELEASE + GET_UNRECEIVE_ORDERS
            }
        }
    }
}