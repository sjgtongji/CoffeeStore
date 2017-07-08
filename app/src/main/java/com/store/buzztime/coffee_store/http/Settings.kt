package com.store.buzztime.coffee_store.http

/**
 * Created by jigangsun on 2017/6/21.
 */

class Settings{
    companion object{
        var DEBUG : Boolean = true;
        var TEST_REST : Boolean = true;
        var SERVER_DEBUG = "http://139.196.228.248:52072/Rest/CoffeeService/getManager"
        var SERVER_RELEASE = "http://waimai.buzztimecoffee.com/";
        var LOGIN = "login";
        var GET_UNRECEIVE_ORDERS = "getUnreceiveOrders";
        lateinit var LOGIN_URL : String;
        lateinit var GET_UNRECEIVE_ORDERS_URL : String;

        init {
            if(DEBUG){
                LOGIN_URL = SERVER_DEBUG
                GET_UNRECEIVE_ORDERS_URL = SERVER_DEBUG
            }else{
                LOGIN_URL = SERVER_RELEASE + LOGIN
                GET_UNRECEIVE_ORDERS_URL = SERVER_RELEASE + GET_UNRECEIVE_ORDERS
            }
        }
    }
}