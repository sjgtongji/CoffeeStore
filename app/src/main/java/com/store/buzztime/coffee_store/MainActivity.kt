package com.store.buzztime.coffee_store

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.store.buzztime.coffee_store.Bean.User
import com.store.buzztime.coffee_store.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() , View.OnClickListener{

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_login -> {
                var name : String = et_name.text.toString();
                var password : String = et_password.text.toString();
                // TODO login
            }
            else -> {}
        }
    }

    var user : User = User();
    lateinit var dataBind : ActivityMainBinding;

    override fun initViews() {

    }

    override fun initEvents() {

        btn_login.setOnTouchListener(View.OnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    btn_login.setTextColor(resources.getColor(R.color.text_yellow));
                }
                MotionEvent.ACTION_UP -> {
                    btn_login.setTextColor(resources.getColor(R.color.white))
                }
                else -> {}
            }
            false;
        })

        btn_login.setOnClickListener(this)

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
