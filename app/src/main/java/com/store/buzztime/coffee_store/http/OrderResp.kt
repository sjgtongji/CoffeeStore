package com.store.buzztime.coffee_store.http

import com.store.buzztime.coffee_store.Bean.Order

/**
 * Created by sjg on 2017/7/17.
 */
class OrderResp{
    var Items : List<Order>? = null;
    var TotalCount : Int = 0;
}