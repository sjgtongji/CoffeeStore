package com.store.buzztime.coffee_store

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewStub
import android.widget.RelativeLayout

/**
 * Created by jigangsun on 2017/6/14.
 */
class BaseActivity : AppCompatActivity() {
    var navigationBar: NavigationBar? = null;
    protected var bodyStub: ViewStub? = null;
    var rootView: RelativeLayout ? = null;// 根容器
    var bodyView: View? = null;
    var application: BaseApplication? = null;
    protected var TAG = this.javaClass.simpleName;
    protected var dialog: ProgressDialog? = null;
    protected var context: Context? = null;
}