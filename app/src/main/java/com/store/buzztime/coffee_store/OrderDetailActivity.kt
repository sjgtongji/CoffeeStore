package com.store.buzztime.coffee_store

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.store.buzztime.coffee_store.Bean.Order
import com.store.buzztime.coffee_store.Bean.Product
import com.store.buzztime.coffee_store.databinding.ActivityOrderDetailBinding
import com.store.buzztime.coffee_store.http.Settings
import kotlinx.android.synthetic.main.activity_order_detail.*

/**
 * Created by sjg on 2017/7/19.
 */
class OrderDetailActivity : AppCompatActivity() , View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btn_receive -> {
                var intent = Intent();
                intent.putExtra(Settings.ORDER_OPERATION_KEY , Settings.ORDER_OPERATION_CONFIRM)
                setResult(Activity.RESULT_OK , intent)
                finish()
            }
            R.id.btn_cancel -> {
                var intent = Intent();
                intent.putExtra(Settings.ORDER_OPERATION_KEY , Settings.ORDER_OPERATION_CANCEL)
                setResult(Activity.RESULT_OK , intent)
                finish()
            }
            else -> {}
        }
    }

    lateinit var binding : ActivityOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        when(intent.getIntExtra(Settings.IS_UNRECEIVE_ORDER_KEY , -1)){
            Settings.UNRECEIVE_ORDER_VALUE -> {
                btn_receive.visibility = View.VISIBLE
                btn_cancel.visibility = View.VISIBLE
            }
            Settings.RECEIVE_ORDER_VALUE -> {
                btn_receive.visibility = View.GONE
                btn_cancel.visibility = View.GONE
            }
            else ->{}
        }
        var order : Order? = (application as BaseApplication).order
        formatOrder(order!!)
        binding.data = order
        lv_product.layoutManager = GridLayoutManager(this,1)
        lv_product.adapter = OrderDetailAdapter(order.listCOrderCommodityRelation)
        btn_receive.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    fun formatOrder(order : Order){
        order.orderMoneyShow = String.format("%.2f", order.orderMomey)
        order.payMoneyShow = String.format("%.2f", order.payMomey)
        order.couponMoneyShow = String.format("%.2f", order.orderMomey - order.payMomey)
        order.serviceFeeShow = String.format("%.2f", order.serverFee)
        var tmp = order.listCOrderCommodityRelation.get(0)
        for(product in order.listCOrderCommodityRelation){
            product.amountShow = String.format("%.2f", product.price * product.quantity)
            product.quantityShow = product.quantity.toString()
        }
    }

    inner class OrderDetailAdapter(val data : List<Product>) : RecyclerView.Adapter<OrderDetailViewHolder>(){


        override fun onBindViewHolder(p0: OrderDetailViewHolder, p1: Int) {
            p0.bind(data[p1]);
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderDetailViewHolder {
            val layoutInflater = LayoutInflater.from(p0.context)
            val binding: ViewDataBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.adapter_order_detail, p0, false)

            val holder = OrderDetailViewHolder(binding);
            return holder
        }

        override fun getItemCount(): Int {
            return data.size;
        }

    }

    inner class OrderDetailViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data : Any){
            binding.setVariable(BR.data , data)
            binding.executePendingBindings()
        }
    }
}