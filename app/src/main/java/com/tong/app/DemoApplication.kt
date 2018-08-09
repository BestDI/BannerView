package com.tong.app

import android.app.Application
import com.tong.banner.BannerViewLifecycleHandler

/**
 * FileName : Application.java
 * Desc     :
 * Copyright (C) 2016-2017
 * Created by Tong Ww on 18-8-9.
 */
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(BannerViewLifecycleHandler.INSTANCE)
    }
}