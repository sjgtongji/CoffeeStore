package com.store.buzztime.coffee_store.http

/**
 * Created by jigangsun on 2017/6/21.
 */

class Settings{

    var SERVER_DEBUG = "http://waimaitest.buzztimecoffee.com/"
    var SERVER_RELEASE = "http://waimai.buzztimecoffee.com/";
    var LOGIN = "login";
    companion object{
        var DEBUG : Boolean = true;
        lateinit var LOGIN_URL : String;
    }

    init {
        LOGIN_URL = if(DEBUG){
            SERVER_DEBUG + LOGIN;
        }else{
            SERVER_RELEASE + LOGIN;
        }
    }
}