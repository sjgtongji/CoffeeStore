package com.store.buzztime.coffee_store

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.store.buzztime.coffee_store.Bean.Period
import com.store.buzztime.coffee_store.Bean.Periods
import com.store.buzztime.coffee_store.http.*
import kotlinx.android.synthetic.main.activity_periods.*
import org.jetbrains.anko.Orientation
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor

/**
 * Created by sjg on 2017/7/6.
 */

class PeriodsActivity : BaseActivity(){
    var periods : MutableList<Periods> = mutableListOf(
    )

    var selectPeriods : Int = 0
    var periodNameAdapter : PeriodsActivity.PeriodNameAdapter? = null;
    var period : List<Period> ? = null;
    override fun initViews() {
        navigationBar.setTitle("南京西路店")
        navigationBar.displayLeftButton()
        navigationBar.hiddenRightButton()
//        rv_period_names.layoutManager = GridLayoutManager(this , periods.size)
//        periodNameAdapter = PeriodNameAdapter(periods);
//        rv_period_names.adapter = periodNameAdapter;
//        rv_periods.layoutManager = GridLayoutManager(this, 5)
    }

    override fun initEvents() {

    }

    override fun initDatas(view: View) {
        showDialog()
        var callback = object  : HttpCallback<PeriodsResp>(PeriodsResp::class.java){
            override fun onTestRest(): PeriodsResp {
                hideDialog()
                return PeriodsResp()
            }

            override fun onSuccess(t: PeriodsResp?) {
                hideDialog()
                Log.d(TAG , "success" + Gson().toJson(t))
                periods.clear()
                var tmpIndex : MutableList<Int> = mutableListOf<Int>()
                for(resp in t!!.items!!){
                    for(period in resp.listBusinessHourWeek){
                        var tmpPeriods : Periods? = null;
                        if(!tmpIndex.contains(period.WeekDay)){
                            tmpIndex.add(period.WeekDay)
                            tmpPeriods = Periods()
                            tmpPeriods.name = getPeriodName(period.WeekDay)
                            periods.add(tmpPeriods)
                        }else{
                            tmpPeriods = periods.get(tmpIndex.indexOf(period.WeekDay))
                        }
                        var tmpPeriod = Period()
                        tmpPeriod.isOpen = if(period.State == 1) false else true
                        tmpPeriod.id = period.id
                        tmpPeriod.sortIndex = period.SortIndex
                        tmpPeriod.name = formatTimeInOneDay(period.StartTime) + "-" + formatTimeInOneDay(period.EndTime)
                        tmpPeriods.periods.add(tmpPeriod)
                    }
                }
                periods.sortWith(Comparator<Periods> { a, b ->
                    if(getWeekDay(a.name) > getWeekDay(b.name)) 1
                    else -1
                })
                if(periods.size > 0){
                    for(tmpPeriods in periods){
                        tmpPeriods.periods.sortWith(Comparator<Period>{a , b ->
                            if(a.sortIndex > b.sortIndex) 1
                            else -1
                        })
                    }
                    rv_period_names.layoutManager = GridLayoutManager(this@PeriodsActivity , periods.size)
                    periodNameAdapter = PeriodNameAdapter(periods);
                    rv_period_names.adapter = periodNameAdapter;
                    rv_periods.layoutManager = GridLayoutManager(this@PeriodsActivity, 5)
                    rv_periods.adapter = PeriodAdapter(periods.get(0).periods)
                }


//                pushActivity(OrderActivity::class.java)
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
            }

        }
        var url =
                if(DEBUG){
                    "${Settings.GET_BUSINESS_HOURWEEKTYPE_URL}?resUUID=66de6c00-407c-4997-8bbe-ec09a3c8a7e5"
                }else{
                    "${Settings.GET_BUSINESS_HOURWEEKTYPE_URL}?resUUID=${application.loginResp!!.resUUID}"
                }
        Log.e(TAG , url)
        get(url , callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_periods)
    }

    fun getPeriodName(weekDay : Int): String{
        when(weekDay){
            1 -> return "星期一"
            2 -> return "星期二"
            3 -> return "星期三"
            4 -> return "星期四"
            5 -> return "星期五"
            6 -> return "星期六"
            7 -> return "星期日"
            else -> return ""
        }
    }

    fun getWeekDay(name : String) : Int {
        when(name){
            "星期一" -> return 1
            "星期二" -> return 2
            "星期三" -> return 3
            "星期四" -> return 4
            "星期五" -> return 5
            "星期六" -> return 6
            "星期日" -> return 7
            else -> return 0
        }
    }

    fun modifyPeriod(period : Period, adapter: PeriodAdapter){
        showDialog()
        var isOpen : Int = if(!period.isOpen) 0 else 1
        var Id = period.id
        var callback = object  : HttpCallback<String>(String::class.java){
            override fun onTestRest(): String {
                hideDialog()
                return ""
            }

            override fun onSuccess(t: String?) {
                hideDialog()
                Log.d(TAG , "success" + Gson().toJson(t))
                period.isOpen = !period.isOpen
                adapter.notifyDataSetChanged()
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
            }

        }
        var url = "${Settings.POST_BUSINESS_HOUR_URL}?resUUID=${application.loginResp!!.resUUID}&id=${Id}&state=${isOpen}"
//        var req = SetPeriodReq();
//        if(DEBUG){
//            req.resUUID = application.loginResp!!.resUUID
//            req.id = Id
//            req.state = isOpen
//        }else{
//            req.resUUID = application.loginResp!!.resUUID
//            req.id = Id
//            req.state = isOpen
//        }
        Log.e(TAG , url)
//        var url = "${Settings.POST_BUSINESS_HOUR_URL}?resUUID=${application.loginResp!!.resUUID}&id=${Id}&state=${isOpen}"
        get(url , callback)
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
                    modifyPeriod(data[index] , this)
//                    data[index].isOpen = !data[index].isOpen
//                    this.notifyDataSetChanged()
                }
                else -> {}
            }
        }


        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onBindViewHolder(p0: PeriodHolder, p1: Int) {
            p0.view.setTag(p1)
            p0.view.setOnClickListener(this)
            if(data[p1].isOpen){
                p0.view.background = p0.view.resources.getDrawable(R.drawable.bg_period_opened)
                p0.open.visibility = View.VISIBLE
                p0.open.setImageResource(R.mipmap.period_opened)
            }else{
                p0.view.background = p0.view.resources.getDrawable(R.drawable.bg_period_closed)
                p0.open.visibility = View.GONE
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
        var open : ImageView;
        init {
            view = binding.root.find(R.id.rl_period)
            name = binding.root.find(R.id.tv_period_name)
            open = binding.root.find(R.id.iv_period_isopen)
        }
        fun bind(data: Any) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }


}