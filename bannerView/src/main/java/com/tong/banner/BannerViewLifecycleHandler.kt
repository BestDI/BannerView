package com.tong.banner

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

class BannerViewLifecycleHandler : Application.ActivityLifecycleCallbacks {

    companion object {
        @JvmStatic
        val INSTANCE = BannerViewLifecycleHandler()
    }

    private var mCurrentActivity: WeakReference<Activity>? = null

    fun getCurrentActivity(): Activity? = mCurrentActivity?.get()

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity) {
        mCurrentActivity = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}