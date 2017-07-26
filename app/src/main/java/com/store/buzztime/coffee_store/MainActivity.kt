package com.store.buzztime.coffee_store

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.telephony.TelephonyManager
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
import com.store.buzztime.coffee_store.R.id.btn_login
import com.store.buzztime.coffee_store.databinding.ActivityMainBinding
import com.store.buzztime.coffee_store.http.*
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.http.params.HttpParams
import org.w3c.dom.Text
import rx.functions.Action
import rx.functions.Action0
import rx.functions.Action1


class MainActivity : BaseActivity() , View.OnClickListener{
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_login -> {
                var name : String = et_name.text.toString();
                var password : String = et_password.text.toString();

                login(name , password)

            }
            else -> {}
        }
    }

    fun login(name : String , password : String){
        showDialog()
        var telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
        var deviceId = android.os.Build.SERIAL
        var address =
                if(DEBUG){
                    "${Settings.LOGIN_URL}?name=loginname&passWord=password&deviceId=${deviceId}"
                }else{
                    "${Settings.LOGIN_URL}?name=${name}&passWord=${password}&deviceId=${deviceId}";
                }
        var callback = object  : HttpCallback<LoginResp>(LoginResp::class.java){
            override fun onTestRest(): LoginResp {
                hideDialog()
                return LoginResp();
            }

            override fun onSuccess(t: LoginResp?) {
                hideDialog()
                val app = getApplication() as BaseApplication
                app.loginResp = t
                PrefUtils().putString(this@MainActivity, Settings.NAME_KEY , name)
                PrefUtils().putString(this@MainActivity, Settings.PWD_KEY , password)
                PrefUtils().putString(this@MainActivity, Settings.RES_ID_KEY , t!!.resUUID)
                pushActivity(OrderActivity::class.java , true)
            }

            override fun onFail(t: HttpBaseResp?) {
                hideDialog()
                showText(t!!.message)
                Log.e(TAG , t!!.message);
            }

        }
        get(address  , callback);
    }

    var user : User = User();
    lateinit var dataBind : ActivityMainBinding;
    var permissions : MutableList<String> = mutableListOf(android.Manifest.permission.READ_PHONE_STATE);

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        navigationBar.hiddenButtons()
        navigationBar.setTitle("登录")
        for(permission in permissions){
            var hasPermission : Boolean = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            if(!hasPermission){
                requestPermissions( permissions.toTypedArray(), 0);
            }
        }
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
        var name = PrefUtils().getString(this, Settings.NAME_KEY , "")
        var password = PrefUtils().getString(this, Settings.PWD_KEY , "")
        if(name != null && !name.isEmpty() && password != null && !password.isEmpty()){
            login(name , password)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_main)
    }


}
