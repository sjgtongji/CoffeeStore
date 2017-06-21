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
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import com.store.buzztime.coffee_store.Bean.User
import com.store.buzztime.coffee_store.databinding.ActivityMainBinding
import com.store.buzztime.coffee_store.http.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text


class MainActivity : BaseActivity() , View.OnClickListener{

    var LOGIN_URL : String = "http://waimaitest.buzztimecoffee.com";
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_login -> {
                var name : String = et_name.text.toString();
                var password : String = et_password.text.toString();
                user.name = name;
                user.password = password;
                // TODO login
                LOGIN_URL.request().get().rxExecute()
                        .map({ r -> r.body().string() })
                        .observeOnMain()
                        .subscribeSafeNext { result -> Log.d(TAG, "request result: $result");}

                Settings.LOGIN_URL.request().post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8") , Gson().toJson(user))).rxExecute().map({ r -> r.body().string() })
                        .observeOnMain().subscribeSafeNext { result ->  Log.d(TAG, "request result: $result");}
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
