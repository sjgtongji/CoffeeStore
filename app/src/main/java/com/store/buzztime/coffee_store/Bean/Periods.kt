package com.store.buzztime.coffee_store.Bean

/**
 * Created by sjg on 2017/7/6.
 */
class Periods{
    var name : String = "星期一"
    var periods : MutableList<Period> = mutableListOf(

    )

    override fun equals(other: Any?): Boolean {
        if(other is Periods){
            var otherPeriods = other as Periods
            return this.name.equals(otherPeriods.name)
        }else{
            return false
        }
    }
}