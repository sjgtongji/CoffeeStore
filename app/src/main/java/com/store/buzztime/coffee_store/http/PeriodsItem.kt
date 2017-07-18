package com.store.buzztime.coffee_store.http

import com.store.buzztime.coffee_store.Bean.PeriodResp

/**
 * Created by sjg on 2017/7/18.
 */
class PeriodsItem {
    var Key : String = "";
    var Value : String = "";
    var listBusinessHourWeek : List<PeriodResp> = mutableListOf<PeriodResp>();
}