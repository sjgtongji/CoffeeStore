package com.store.buzztime.coffee_store.Bean

import com.google.gson.annotations.SerializedName

/**
 * Created by jigangsun on 2017/6/23.
 */
class Order{
    var id : Int = 0;
    var createName : String = "";
    var assignCategory : String = "";
    var cityId : String ="";
    var couponName : String ="";
    var couponUUID : String ="";
    var deliveryMaxTime : String ="";
    var deliveryMinTime : String ="";
    var deliveryType : String ="";
    var distributionId : Int = 0;
    var distributionMobile : String ="";
    var isDelete : Boolean = false;
    var isOutOfTime : Boolean = false;
    var managerRemark : String ="";
    var memberAddressUUID : String ="";
    var memberUUID : String ="";
    var messageStatus : String ="";
    var orderId : Int = 0;
    var orderState : Int = 0;
    var orderTime : String ="";
    var orderUUID : String ="";
    var payDateTime : String ="";
    var payMomey : Double = 0.00;
    var payStatus : Int = 0;
    var payType : Int = 0;
    var preferential : String = "";
    var preferentialType : String = "";
    var printDate : String = "";
    var quantity : Int =  0;
    var resUUID : String = "";
    var serverFee : Double = 0.00;
    var telephone : String = "";
    var updateName : String = "";
    var updateTime : String = "";
    var version : Int = 0;
    @SerializedName("memberName")
    var name : String = "王先生";
    var orderMomey : Double = 0.00;
    var amount : String = "0.00";
    var createTime : String = "";
    var deliveryAddress : String = "";
    var distributeTime : String = "";
    var remark : String = "";
    var lat : Double = 0.00;
    var lng : Double = 0.00;
    var distributionName : String = "李某";

    var address : Address? = null;
    var listCOrderCommodityRelation : List<Product> = listOf<Product>();
    var createTimeShow : String = "";
}