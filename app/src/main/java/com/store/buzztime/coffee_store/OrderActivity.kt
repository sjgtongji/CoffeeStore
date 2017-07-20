package com.store.buzztime.coffee_store

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.Gson
import com.store.buzztime.coffee_store.Bean.Order
import com.store.buzztime.coffee_store.databinding.AdapterOrderBinding
import com.store.buzztime.coffee_store.http.HttpBaseResp
import com.store.buzztime.coffee_store.http.HttpCallback
import com.store.buzztime.coffee_store.http.LoginResp
import com.store.buzztime.coffee_store.http.OrderResp
import kotlinx.android.synthetic.main.activity_main.*
import com.store.buzztime.coffee_store.http.*
import kotlinx.android.synthetic.main.activity_order.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * Created by sjg on 2017/6/27.
 */
class OrderActivity : BaseActivity(), View.OnClickListener{
    var orderReceiver : OrderReciver ? = null
    var isUnReceive : Boolean = true
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.rl_unfinish -> {
                isUnReceive = true
                tv_unfinish.textColor = resources.getColor(R.color.text_yellow)
                view_unfinish.backgroundColor = resources.getColor(R.color.text_yellow)
                tv_finish.textColor = resources.getColor(R.color.black)
                view_finish.backgroundColor = resources.getColor(R.color.bg_white)
                getUnreceiveOrders()
            }
            R.id.rl_finish -> {
                isUnReceive = false
                tv_unfinish.textColor = resources.getColor(R.color.black)
                view_unfinish.backgroundColor = resources.getColor(R.color.bg_white)
                tv_finish.textColor = resources.getColor(R.color.text_yellow)
                view_finish.backgroundColor = resources.getColor(R.color.text_yellow)
                getReceiverOrders()
            }
            R.id.activity_frame_title_btn_right -> {
                pushActivity(PeriodsActivity::class.java)
            }
            else -> {

            }
        }
    }

    override fun initViews() {
        navigationBar.setTitle("南京西路店")
        navigationBar.hiddenLeftButton()
        navigationBar.displayRightButton()
        navigationBar.rightBtn.text = "配送时间"
        navigationBar.rightBtn.setOnClickListener(this)
//        rv_orders.adapter = OrderAdapter(orders)
        rv_orders.layoutManager = GridLayoutManager(this, 2)
    }

    override fun initEvents() {
        rl_unfinish.setOnClickListener(this)
        rl_finish.setOnClickListener(this)
    }

    override fun initDatas(view: View) {
//        showDialog()
        getUnreceiveOrders()
        SyncService().actionStart(this)
        orderReceiver = OrderReciver()
        var filter : IntentFilter = IntentFilter();
        filter.addAction(Settings.ACTION_ORDER)
        LocalBroadcastManager.getInstance(this).registerReceiver(orderReceiver , filter)
    }

    fun getUnreceiveOrders(){
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                Log.d(TAG , "success" + Gson().toJson(t))
                var app = getApplication() as BaseApplication
                for(order in t!!.Items!!){
                    formatOrder(order)
                }
                app.unReceiveOrders = t!!.Items!!;
                rv_orders.adapter = OrderAdapter(t.Items!!)

//                pushActivity(OrderActivity::class.java)
            }

            override fun onFail(t: HttpBaseResp?) {
                Log.e(TAG , t!!.message);
            }

        }
        var app = getApplication() as BaseApplication
        var url =
                if(DEBUG){
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=efad271f-6673-4d91-a9f7-abd3d4fe5f87&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10"
                }else{
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=${Settings.ORDER_CONFIRM}&startIndex=1&count=10"
                }
        get(url , callback)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orderReceiver)
        super.onDestroy()
    }
    fun getReceiverOrders(){
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                Log.d(TAG , "success" + Gson().toJson(t))
                var app = getApplication() as BaseApplication
                app.unReceiveOrders = t!!.Items!!;
                rv_orders.adapter = OrderAdapter(t.Items!!)
//                pushActivity(OrderActivity::class.java)
            }

            override fun onFail(t: HttpBaseResp?) {
                Log.e(TAG , t!!.message);
            }

        }
        var app = getApplication() as BaseApplication
        var url =
                if(DEBUG){
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=efad271f-6673-4d91-a9f7-abd3d4fe5f87&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10"
                }else{
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=${Settings.ORDER_STORE_CONFIRM},${Settings.ORDER_RIDER_GET},${Settings.ORDER_RIDER_POST}&startIndex=1&count=10"
                }
        get(url , callback)
    }

    fun formatOrder(order : Order){
        order.amount = String.format("%.2f", order.payMomey)
        order.distributeTime = formatDateTime(order.deliveryMinTime) + "-" + formatDateTime(order.deliveryMaxTime)
        order.createTimeShow = formatDateTime(order.createTime)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_order)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                1 -> {
                    when(data!!.getIntExtra(Settings.ORDER_OPERATION_KEY , -1)){
                        Settings.ORDER_OPERATION_CONFIRM -> receiveOrder(application.order!!)
                        Settings.ORDER_OPERATION_CANCEL -> cancelOrder(application.order!!)
                        else -> {}
                    }
                }
                else ->{}
            }
        }
    }

    fun receiveOrder(order : Order){
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                Log.d(TAG , "success" + Gson().toJson(t))
                rl_unfinish.performClick()
            }

            override fun onFail(t: HttpBaseResp?) {
                Log.e(TAG , t!!.message);
            }

        }
        var url = Settings.POST_ORDER_STATE_URL
        var req = OrderReq();
        if(DEBUG){
            req.resUUID = application.loginResp!!.resUUID
            req.orderId = order.orderId
            req.orderState = Settings.ORDER_STORE_CONFIRM
        }else{
            req.resUUID = application.loginResp!!.resUUID
            req.orderId = order.orderId
            req.orderState = Settings.ORDER_STORE_CONFIRM
        }
//        var url = "${Settings.POST_ORDER_STATE_URL}?resUUID=${application.loginResp!!.resUUID}&orderId=${order.orderId}&orderState=${Settings.ORDER_STORE_CONFIRM}"

        post(url , gson.toJson(req), callback)
    }

    fun cancelOrder(order : Order){
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                Log.d(TAG , "success" + Gson().toJson(t))
                rl_unfinish.performClick()
            }

            override fun onFail(t: HttpBaseResp?) {
                Log.e(TAG , t!!.message);
            }

        }
        var url = Settings.POST_ORDER_STATE_URL
        var req = OrderReq();
        if(DEBUG){
            req.resUUID = application.loginResp!!.resUUID
            req.orderId = order.orderId
            req.orderState = Settings.ORDER_CANCEL
        }else{
            req.resUUID = application.loginResp!!.resUUID
            req.orderId = order.orderId
            req.orderState = Settings.ORDER_CANCEL
        }
//        var url = "${Settings.POST_ORDER_STATE_URL}?resUUID=${application.loginResp!!.resUUID}&orderId=${order.orderId}&orderState=${Settings.ORDER_CANCEL}"
        post(url ,  gson.toJson(req) , callback)
    }
    inner class OrderAdapter(val data : List<Order>) : RecyclerView.Adapter<OrderViewHolder>() , View.OnClickListener{
        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.btn_receive -> {
                    Log.d("" , "receive" + v.tag)
                    receiveOrder(data.get(v.tag as Int))
                }
                R.id.btn_cancel -> {
                    Log.d("" , "cancel" + v.tag)
                    cancelOrder(data.get(v.tag as Int))
                }
                R.id.ll_order -> {
                    application.order = data.get(v.tag as Int)
                    var intent = Intent(this@OrderActivity , OrderDetailActivity::class.java)
                    if(isUnReceive){
                        intent.putExtra(Settings.IS_UNRECEIVE_ORDER_KEY , Settings.UNRECEIVE_ORDER_VALUE)
                    }else{
                        intent.putExtra(Settings.IS_UNRECEIVE_ORDER_KEY , Settings.RECEIVE_ORDER_VALUE)
                    }
                    pushActivityForResult(intent , 1)
                }
                else -> {Log.d("" , "error")}
            }
        }


        override fun onBindViewHolder(p0: OrderViewHolder, p1: Int) {
            p0.btn_receive.setOnClickListener(this)
            p0.btn_receive.setTag(p1)
            p0.btn_cancel.setOnClickListener(this)
            p0.btn_cancel.setTag(p1)
            p0.ll_order.setOnClickListener(this)
            p0.ll_order.setTag(p1)
            if(!isUnReceive){
                p0.btn_receive.visibility = View.GONE
                p0.btn_cancel.visibility = View.GONE
            }
            p0.bind(data[p1]);
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderViewHolder {
            val layoutInflater = LayoutInflater.from(p0.context)
            val binding: ViewDataBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.adapter_order, p0, false)

            val holder = OrderViewHolder(binding);
            return holder
        }

        override fun getItemCount(): Int {
            return data.size;
        }

    }

    inner class OrderViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){
        var btn_receive : Button
        var btn_cancel : Button
        var ll_order : LinearLayout
        init {
            btn_receive = binding.root.findViewById(R.id.btn_receive) as Button
            btn_cancel = binding.root.findViewById(R.id.btn_cancel) as Button
            ll_order = binding.root.findViewById(R.id.ll_order) as LinearLayout
        }
        fun bind(data : Any){
            binding.setVariable(BR.data , data)
            binding.executePendingBindings()
        }
    }

    inner class OrderReciver : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            rl_unfinish.performClick()
        }
    }
}