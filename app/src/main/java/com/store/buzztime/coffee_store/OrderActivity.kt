package com.store.buzztime.coffee_store

import android.os.Bundle
import android.view.View

/**
 * Created by sjg on 2017/6/27.
 */
class OrderActivity : BaseActivity(){
    override fun initViews() {
        navigationBar.setTitle("南京西路店")
        navigationBar.hiddenLeftButton()
        navigationBar.displayRightButton()
        navigationBar.rightBtn.text = "配送时间"
        navigationBar.rightBtn.setCompoundDrawables(resources.getDrawable(R.mipmap.add) , null , null , null)
    }

    override fun initEvents() {

    }

    override fun initDatas(view: View) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_order)
    }
}