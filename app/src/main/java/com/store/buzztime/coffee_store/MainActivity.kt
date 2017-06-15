package com.store.buzztime.coffee_store

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewStubProxy
import android.databinding.adapters.ViewStubBindingAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.store.buzztime.coffee_store.Bean.User
import com.store.buzztime.coffee_store.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() , View.OnClickListener {
    var text : String = "";
    var user : User = User();
    lateinit var dataBind : ActivityMainBinding;
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_main -> {
                Log.d(TAG , "onClick")
                user.name = "world";
                user.notifyChange();
            }
            else -> {}
        }
    }

    override fun initViews() {
        tv_main.setText("hello")
    }

    override fun initEvents() {
        tv_main.setOnClickListener(this);
    }

    override fun initDatas(view : View) {
        dataBind = DataBindingUtil.bind(view , null);
        dataBind.data = user;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_main)
    }

}
