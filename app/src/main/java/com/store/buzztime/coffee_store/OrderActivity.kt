package com.store.buzztime.coffee_store

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.store.buzztime.coffee_store.Bean.Order
import kotlinx.android.synthetic.main.activity_order.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * Created by sjg on 2017/6/27.
 */
class OrderActivity : BaseActivity(), View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.rl_unfinish -> {
                tv_unfinish.textColor = resources.getColor(R.color.text_yellow)
                view_unfinish.backgroundColor = resources.getColor(R.color.text_yellow)
                tv_finish.textColor = resources.getColor(R.color.black)
                view_finish.backgroundColor = resources.getColor(R.color.bg_white)
            }
            R.id.rl_finish -> {
                tv_unfinish.textColor = resources.getColor(R.color.black)
                view_unfinish.backgroundColor = resources.getColor(R.color.bg_white)
                tv_finish.textColor = resources.getColor(R.color.text_yellow)
                view_finish.backgroundColor = resources.getColor(R.color.text_yellow)
            }
            else -> {

            }
        }
    }

    var orders = listOf<Order>(
            Order(),
            Order(),
            Order()
    )
    override fun initViews() {
        navigationBar.setTitle("南京西路店")
        navigationBar.hiddenLeftButton()
        navigationBar.displayRightButton()
        navigationBar.rightBtn.text = "配送时间"
        navigationBar.rightBtn.setCompoundDrawables(resources.getDrawable(R.mipmap.add) , null , null , null)
        rv_orders.layoutManager = GridLayoutManager(this, 2)
        rv_orders.adapter = OrderAdapter(orders)

    }

    override fun initEvents() {
        rl_unfinish.setOnClickListener(this)
        rl_finish.setOnClickListener(this)
    }

    override fun initDatas(view: View) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_order)
    }

    class OrderAdapter(val data : List<Order>) : RecyclerView.Adapter<OrderViewHolder>(){
        override fun onBindViewHolder(p0: OrderViewHolder, p1: Int) {
           p0.bind(data[p1]);
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderViewHolder {
            val layoutInflater = LayoutInflater.from(p0.context)
            val binding: ViewDataBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.adapter_order, p0, false)
            binding.root.findViewById(R.id.btn_receive).setOnClickListener(View.OnClickListener {
                Log.d("" , p1.toString() + "receive")
            })
            binding.root.findViewById(R.id.btn_cancel).setOnClickListener(View.OnClickListener {
                Log.d("" , p1.toString() + "cancel")
            })
            return OrderViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return data.size;
        }

    }

    class OrderViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data : Any){
            binding.setVariable(BR.data , data)
            binding.executePendingBindings()
        }
    }
}