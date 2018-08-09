package com.tong.banner

import android.view.View

object BannerViewManager {

    @JvmStatic
    fun show(view: View) {
        val bannerView = BannerView()
        bannerView.setContentView(view)
        bannerView.scheduleTime(Duration.LONG)
    }

    @JvmStatic
    @JvmOverloads
    fun showCommonBanner(
            level: Level = Level.INFO,
            duration: Duration = Duration.MEDIUM,
            message: String,
            title: String? = null,
            commonButtonText: String? = null,
            commonButtonListener: CommonButtonListener? = null
    ) {
        level.localTitle = title.orEmpty()
        val bannerView = BannerView()
        bannerView.createView(message,
                level.localTitle,
                level.color,
                level.drawableRes,
                commonButtonListener, commonButtonText,
                null, null, null, null, null, null)

        bannerView.scheduleTime(duration)
    }

    @JvmStatic
    @JvmOverloads
    fun showConfirmCancelBanner(
            level: Level = Level.INFO,
            duration: Duration = Duration.MEDIUM,
            message: String,
            title: String? = null,
            confirmText: String? = null,
            cancelText: String? = null,
            confirmCancelListener: ConfirmCancelListener? = null
    ) {
        level.localTitle = title.orEmpty()
        val bannerView = BannerView()
        bannerView.createView(message,
                level.localTitle,
                level.color,
                level.drawableRes,
                null, null,
                confirmCancelListener, confirmText, cancelText,
                null, null, null)

        bannerView.scheduleTime(duration)
    }

    @JvmStatic
    @JvmOverloads
    fun showInputBanner(
            level: Level = Level.INFO,
            duration: Duration = Duration.MEDIUM,
            message: String,
            title: String? = null,
            sendButtonText: String? = null,
            inputListener: InputListener? = null,
            vararg inputHint: String?
    ) {
        level.localTitle = title.orEmpty()
        val bannerView = BannerView()
        bannerView.createView(message,
                level.localTitle,
                level.color,
                level.drawableRes,
                null, null, null, null, null,
                inputListener, sendButtonText, *inputHint)

        bannerView.scheduleTime(duration)
    }

}

abstract class Level {
    abstract var localTitle: String
    abstract val color: Int
    abstract val drawableRes: Int

    object SUCCESS : Level() {
        override var localTitle: String = ""
            get() = if (field.isNullOrEmpty())
                BannerViewLifecycleHandler.INSTANCE.getCurrentActivity()!!.resources.getString(R.string.success)
            else
                field
        override val color: Int
            get() = R.color.success
        override val drawableRes: Int
            get() = R.drawable.success_light
    }

    object INFO : Level() {
        override var localTitle: String = ""
            get() = if (field.isEmpty())
                BannerViewLifecycleHandler.INSTANCE.getCurrentActivity()!!.resources.getString(R.string.info)
            else
                field
        override val color: Int
            get() = R.color.info
        override val drawableRes: Int
            get() = R.drawable.info_light
    }

    object WARNING : Level() {
        override var localTitle: String = ""
            get() = if (field.isEmpty())
                BannerViewLifecycleHandler.INSTANCE.getCurrentActivity()!!.resources.getString(R.string.warning)
            else
                field
        override val color: Int
            get() = R.color.warning
        override val drawableRes: Int
            get() = R.drawable.warning_light
    }

    object ERROR : Level() {
        override var localTitle: String = ""
            get() = if (field.isEmpty())
                BannerViewLifecycleHandler.INSTANCE.getCurrentActivity()!!.resources.getString(R.string.error)
            else
                field
        override val color: Int
            get() = R.color.error
        override val drawableRes: Int
            get() = R.drawable.error_light
    }
}
