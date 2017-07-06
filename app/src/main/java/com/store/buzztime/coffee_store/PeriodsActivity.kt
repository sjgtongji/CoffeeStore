package com.store.buzztime.coffee_store

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.store.buzztime.coffee_store.Bean.Period
import com.store.buzztime.coffee_store.Bean.Periods
import kotlinx.android.synthetic.main.activity_periods.*
import org.jetbrains.anko.Orientation
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor

/**
 * Created by sjg on 2017/7/6.
 */

class PeriodsActivity : BaseActivity(){
    var periods : List<Periods> = listOf(
            Periods(),
            Periods(),
            Periods(),
            Periods(),
            Periods(),
            Periods(),
            Periods()
    )

    var selectPeriods : Int = 0
    var periodNameAdapter : PeriodsActivity.PeriodNameAdapter? = null;
    var period : List<Period> ? = null;
    override fun initViews() {
        navigationBar.setTitle("南京西路店")
        navigationBar.displayLeftButton()
        navigationBar.hiddenRightButton()
        rv_period_names.layoutManager = GridLayoutManager(this , periods.size)
        periodNameAdapter = PeriodNameAdapter(periods);
        rv_period_names.adapter = periodNameAdapter;
        rv_periods.layoutManager = GridLayoutManager(this, 5)
    }

    override fun initEvents() {

    }

    override fun initDatas(view: View) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_periods)
    }

    inner class PeriodNameAdapter(val data : List<Periods>) : RecyclerView.Adapter<PeriodNameHolder>() , View.OnClickListener{
        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.rl_period_name -> {
                    selectPeriods = v.getTag() as Int
                    periodNameAdapter!!.notifyDataSetChanged()
                    period = data[selectPeriods].periods;
                    val tmp = period
                    rv_periods.adapter = PeriodAdapter(tmp!!)
                }
                else -> {}
            }
        }


        override fun onBindViewHolder(p0: PeriodNameHolder, p1: Int) {
            p0.view.setTag(p1)
            p0.view.setOnClickListener(this)
            if(p1 == selectPeriods){
                p0.underline.backgroundColor = p0.view.resources.getColor(R.color.text_yellow)
                p0.name.textColor = p0.view.resources.getColor(R.color.text_yellow)
            }else{
                p0.underline.backgroundColor = p0.view.resources.getColor(R.color.bg_white)
                p0.name.textColor = p0.view.resources.getColor(R.color.black)
            }
            p0.bind(data[p1]);
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PeriodNameHolder {
            val layoutInflater = LayoutInflater.from(p0.context)
            val binding: ViewDataBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.adapter_period_name, p0, false)

            val holder = PeriodNameHolder(binding);
            return holder
        }

        override fun getItemCount(): Int {
            return data.size;
        }

    }

    class PeriodNameHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        var view : View;
        var underline : View;
        var name : TextView;
        init {
            view = binding.root.find(R.id.rl_period_name)
            underline = binding.root.find(R.id.view_underline)
            name = binding.root.find(R.id.tv_period_name)
        }
        fun bind(data: Any) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }


    inner class PeriodAdapter(val data : List<Period>) : RecyclerView.Adapter<PeriodHolder>() , View.OnClickListener{
        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.rl_period -> {
                    val index : Int = v.tag as Int;
                    data[index].isOpen = !data[index].isOpen
                    this.notifyDataSetChanged()
                }
                else -> {}
            }
        }


        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onBindViewHolder(p0: PeriodHolder, p1: Int) {
            p0.view.setTag(p1)
            p0.view.setOnClickListener(this)
            if(data[p1].isOpen){
                p0.view.background = p0.view.resources.getDrawable(R.mipmap.time_border)
            }else{
                p0.view.background = null
            }
//            if(p1 == selectPeriods){
//                p0.name.textColor = p0.view.resources.getColor(R.color.text_yellow)
//            }else{
//                p0.name.textColor = p0.view.resources.getColor(R.color.black)
//            }
            p0.bind(data[p1]);
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PeriodHolder {
            val layoutInflater = LayoutInflater.from(p0.context)
            val binding: ViewDataBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.adapter_period, p0, false)

            val holder = PeriodHolder(binding);
            return holder
        }

        override fun getItemCount(): Int {
            return data.size;
        }

    }

    class PeriodHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        var view : View;
        var name : TextView;
        init {
            view = binding.root.find(R.id.rl_period)
            name = binding.root.find(R.id.tv_period_name)
        }
        fun bind(data: Any) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }
}