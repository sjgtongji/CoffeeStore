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
import android.view.KeyEvent
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
    var newOrderReminder : NewOrderReceiver ? = null
    var isUnReceive : Boolean = true
    var isFirstIn : Boolean = true
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.rl_unfinish -> {
                isUnReceive = true
                tv_unfinish.textColor = resources.getColor(R.color.text_yellow)
                view_unfinish.backgroundColor = resources.getColor(R.color.text_yellow)
                tv_finish.textColor = resources.getColor(R.color.black)
                view_finish.backgroundColor = resources.getColor(R.color.bg_white)
                tv_history.textColor = resources.getColor(R.color.black)
                view_history.backgroundColor = resources.getColor(R.color.bg_white)
                rl_search.visibility = View.GONE
                getUnreceiveOrders()
            }
            R.id.rl_finish -> {
                isUnReceive = false
                tv_unfinish.textColor = resources.getColor(R.color.black)
                view_unfinish.backgroundColor = resources.getColor(R.color.bg_white)
                tv_finish.textColor = resources.getColor(R.color.text_yellow)
                view_finish.backgroundColor = resources.getColor(R.color.text_yellow)
                tv_history.textColor = resources.getColor(R.color.black)
                view_history.backgroundColor = resources.getColor(R.color.bg_white)
                rl_search.visibility = View.GONE
                getReceiverOrders()

            }
            R.id.rl_history -> {
                isUnReceive = false
                tv_unfinish.textColor = resources.getColor(R.color.black)
                view_unfinish.backgroundColor = resources.getColor(R.color.bg_white)
                tv_finish.textColor = resources.getColor(R.color.black)
                view_finish.backgroundColor = resources.getColor(R.color.bg_white)
                tv_history.textColor = resources.getColor(R.color.text_yellow)
                view_history.backgroundColor = resources.getColor(R.color.text_yellow)
                rl_search.visibility = View.VISIBLE
                rv_orders.adapter = OrderAdapter(mutableListOf<Order>())
            }
            R.id.iv_search -> {
                var content : String = et_search.text.toString()
                if(content == null || content.isEmpty()){
                    showText("搜索内容不能为空")
                    return
                }
                getHistory(content)
            }
            R.id.activity_frame_title_btn_right -> {
                pushActivity(PeriodsActivity::class.java)
            }
            R.id.activity_frame_title_btn_left -> {
//                PrefUtils().putString(this@OrderActivity , Settings.NAME_KEY , "")
//                PrefUtils().putString(this@OrderActivity , Settings.PWD_KEY , "")
//                PrefUtils().putString(this@OrderActivity , Settings.RES_ID_KEY , "")
//                pushActivity(MainActivity::class.java , true)
                showConfirmDialog(500 , "确定退出登录?")
            }
            else -> {

            }
        }
    }

    override fun initViews() {
        navigationBar.setTitle(application.loginResp!!.name)
        navigationBar.displayLeftButton()
        navigationBar.displayRightButton()
        navigationBar.leftBtn.text = "退出登录"
        navigationBar.rightBtn.text = "配送时间"
        navigationBar.rightBtn.setOnClickListener(this)
        navigationBar.leftBtn.setOnClickListener(this)
//        rv_orders.adapter = OrderAdapter(orders)
        rv_orders.layoutManager = GridLayoutManager(this, 2)
    }

    override fun initEvents() {
        rl_unfinish.setOnClickListener(this)
        rl_finish.setOnClickListener(this)
        rl_history.setOnClickListener(this)
        iv_search.setOnClickListener(this)
    }


    override fun initDatas(view: View) {
//        showDialog()
        getUnreceiveOrders()
        SyncService().actionStart(this)
        orderReceiver = OrderReciver()
        newOrderReminder = NewOrderReceiver()
        var filter : IntentFilter = IntentFilter();
        filter.addAction(Settings.ACTION_ORDER)
        registerReceiver(orderReceiver , filter)
        var reminderFilter : IntentFilter = IntentFilter()
        reminderFilter.addAction(Settings.ACTION_NEW_REMINDER)
        registerReceiver(newOrderReminder , reminderFilter)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun logout(){
        PrefUtils().putString(this@OrderActivity , Settings.NAME_KEY , "")
        PrefUtils().putString(this@OrderActivity , Settings.PWD_KEY , "")
        PrefUtils().putString(this@OrderActivity , Settings.RES_ID_KEY , "")
        pushActivity(MainActivity::class.java , true)
    }
    fun getUnreceiveOrders(){
        showDialog()
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                hideDialog()
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                hideDialog()
                Log.d(TAG , "success" + Gson().toJson(t))
                var app = getApplication() as BaseApplication
                for(order in t!!.Items!!){
                    formatOrder(order)
                }
                app.unReceiveOrders = t!!.Items!!;
                rv_orders.adapter = OrderAdapter(t.Items!!)
                if(t!!.Items!!.size > 0 && isFirstIn){
                    isFirstIn = false
                    var speakIntent : Intent = Intent(Settings.ACTION_NEW_REMINDER)
                    speakIntent.putExtra(ACTION_KEY , com.store.buzztime.coffee_store.ACTION_START)
                    speakIntent.putExtra(ORDER_ID_KEY , t!!.Items!![0].id)
                    sendBroadcast(speakIntent)
                }
//                pushActivity(OrderActivity::class.java)
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
            }

        }
        var app = getApplication() as BaseApplication
        var url =
                if(DEBUG){
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=efad271f-6673-4d91-a9f7-abd3d4fe5f87&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10"
                }else{
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=${Settings.ORDER_INIT}&startIndex=1&count=10"
                }
        Log.e(TAG , url)
        get(url , callback)
    }

    override fun onDestroy() {
        unregisterReceiver(orderReceiver)
        unregisterReceiver(newOrderReminder)
        super.onDestroy()
    }

    fun getHistory(content : String){
        //TODO 获取历史订单
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                hideDialog()
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                hideDialog()
                Log.d(TAG , "success" + Gson().toJson(t))
                for(order in t!!.Items!!){
                    formatOrder(order)
                }
                rv_orders.adapter = OrderAdapter(t!!.Items!!)
//                pushActivity(OrderActivity::class.java)
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
                rv_orders.adapter = OrderAdapter(mutableListOf<Order>())
            }

        }
        var app = getApplication() as BaseApplication
        var url =
                if(DEBUG){
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=efad271f-6673-4d91-a9f7-abd3d4fe5f87&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10&searchKey=${content}"
                }else{
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=${Settings.ORDER_FINISH}&startIndex=1&count=10&searchKey=${content}"
                }
        get(url , callback)
    }
    fun getReceiverOrders(){
        showDialog()
        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onTestRest(): OrderResp {
                hideDialog()
                return OrderResp()
            }

            override fun onSuccess(t: OrderResp?) {
                hideDialog()
                Log.d(TAG , "success" + Gson().toJson(t))
                for(order in t!!.Items!!){
                    formatOrder(order)
                }
                rv_orders.adapter = OrderAdapter(t!!.Items!!)
//                pushActivity(OrderActivity::class.java)
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
            }

        }
        var app = getApplication() as BaseApplication
        var url =
                if(DEBUG){
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10"
                }else{
                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=${Settings.ORDER_STORE_CONFIRM},${Settings.ORDER_RIDER_GET},${Settings.ORDER_RIDER_POST}&startIndex=1&count=10"
                }
        get(url , callback)
    }

    fun formatOrder(order : Order){
        order.amount = String.format("%.2f", order.payMomey)
        order.distributeTime = formatDateTime(order.deliveryMinTime) + "-" + formatTime(order.deliveryMaxTime)
        order.createTimeShow = formatDateTime(order.createTime)
        order.orderStateShow = getOrderStateShow(order)
    }

    fun getOrderStateShow(order : Order) : String{
        when(order.orderState){
            //1：已确认；2：取消；3：已配送；4：已完成；5：门店接单；6：骑手取餐；7：骑手送餐中
            1 -> return "已支付"
            2 -> return "已取消"
            3 -> return "配送完成"
            4 -> return "已完成"
            5 -> return "门店已接单"
            6 -> return "骑手已接单"
            7 -> return "骑手配送中"
            else -> return "已支付"
        }
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
                        Settings.ORDER_OPERATION_CONFIRM -> {
                            showConfirmDialog(102 , "确定要接此订单?")
                        }
                        Settings.ORDER_OPERATION_CANCEL -> {
                            showConfirmDialog(103 , "确定要取消此订单?")
                        }
                        else -> {}
                    }
                }
                100 -> {
                    receiveOrder(toReceive!!)
                }
                101 -> {
                    cancelOrder(toCancel!!)
                }
                102 -> {
                    receiveOrder(application.order!!)
                }
                103 -> {
                    cancelOrder(application.order!!)
                }
                500 -> {
                    logout()
                }
                else ->{}
            }
        }
    }

    fun receiveOrder(order : Order){
        showDialog()
        var callback = object  : HttpCallback<Boolean>(Boolean::class.java){
            override fun onTestRest(): Boolean {
                hideDialog()
                return false
            }

            override fun onSuccess(t: Boolean?) {
                Log.d(TAG , "success" + Gson().toJson(t))
                hideDialog()
                rl_unfinish.performClick()
                var cancelIntent : Intent = Intent(Settings.ACTION_NEW_REMINDER)
                cancelIntent.putExtra(ACTION_KEY , com.store.buzztime.coffee_store.ACTION_STOP)
                cancelIntent.putExtra(ORDER_ID_KEY , order.id)
                sendBroadcast(cancelIntent)
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
                rl_unfinish.performClick()
            }

        }
        var url = "${Settings.POST_ORDER_STATE_URL}?resUUID=${application.loginResp!!.resUUID}&orderId=${order.id}&orderState=${Settings.ORDER_STORE_CONFIRM}"
//        var req = OrderReq();
//        if(DEBUG){
//            req.resUUID = application.loginResp!!.resUUID
//            req.orderId = order.orderId
//            req.orderState = Settings.ORDER_STORE_CONFIRM
//        }else{
//            req.resUUID = application.loginResp!!.resUUID
//            req.orderId = order.orderId
//            req.orderState = Settings.ORDER_STORE_CONFIRM
//        }
//        var url = "${Settings.POST_ORDER_STATE_URL}?resUUID=${application.loginResp!!.resUUID}&orderId=${order.orderId}&orderState=${Settings.ORDER_STORE_CONFIRM}"

        get(url , callback)
    }

    fun cancelOrder(order : Order){
        showDialog()
        var callback = object  : HttpCallback<Boolean>(Boolean::class.java){
            override fun onTestRest(): Boolean {
                hideDialog()
                return false
            }

            override fun onSuccess(t: Boolean?) {
                hideDialog()
                Log.d(TAG , "success" + Gson().toJson(t))
                rl_unfinish.performClick()
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
                rl_unfinish.performClick()
            }

        }
        var url = "${Settings.POST_ORDER_STATE_URL}?resUUID=${application.loginResp!!.resUUID}&orderId=${order.id}&orderState=${Settings.ORDER_CANCEL}"
//        var req = OrderReq();
//        if(DEBUG){
//            req.resUUID = application.loginResp!!.resUUID
//            req.orderId = order.orderId
//            req.orderState = Settings.ORDER_CANCEL
//        }else{
//            req.resUUID = application.loginResp!!.resUUID
//            req.orderId = order.orderId
//            req.orderState = Settings.ORDER_CANCEL
//        }
//        var url = "${Settings.POST_ORDER_STATE_URL}?resUUID=${application.loginResp!!.resUUID}&orderId=${order.orderId}&orderState=${Settings.ORDER_CANCEL}"
        get(url , callback)
    }

    var toReceive : Order? = null
    var toCancel : Order? = null
    inner class OrderAdapter(val data : List<Order>) : RecyclerView.Adapter<OrderViewHolder>() , View.OnClickListener{
        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.btn_receive -> {
                    Log.d("" , "receive" + v.tag)
                    toReceive = data.get(v.tag as Int)
                    showConfirmDialog(100 , "确定要接此订单?")
//                    receiveOrder()
                }
                R.id.btn_cancel -> {
                    Log.d("" , "cancel" + v.tag)
                    toCancel = data.get(v.tag as Int)
                    showConfirmDialog(101 , "确定要取消此订单?")
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
            binding.setVariable(BR.address , (data as Order).address)
            binding.executePendingBindings()
        }
    }

    inner class OrderReciver : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.e(TAG , "新订单")
            rl_unfinish.performClick()
        }
    }
}