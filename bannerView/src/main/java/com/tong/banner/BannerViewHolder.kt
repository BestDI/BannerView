package com.tong.banner

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.SwipeDismissBehavior
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.tong.banner.util.getStatusHeight

internal class BannerView {

    private var mHandler: Handler
    private lateinit var mDecorView: ViewGroup
    private lateinit var bannerViewLayout: BannerViewLayout
    private var isShow: Boolean = false
    private var mCommonButtonListener: CommonButtonListener? = null
    private var mConfirmCancelListener: ConfirmCancelListener? = null
    private var mInputListener: InputListener? = null

    companion object {
        private const val MSG_SHOW = 0
        private const val MSG_DISMISS = 1
        private const val ANIMATE_IN_DURATION = 300
        private const val ANIMATE_OUT_DURATION = 200
    }

    init {
        mHandler = Handler(Looper.getMainLooper()) {
            when (it.what) {
                MSG_SHOW -> {
                    showBannerView()
                    return@Handler true
                }
                MSG_DISMISS -> {
                    hideBannerView()
                    return@Handler true
                }
                else -> {
                    return@Handler false
                }
            }
        }
    }


    private fun animateIn() {
        ViewCompat.setTranslationY(bannerViewLayout, -(bannerViewLayout.height.toFloat()))
        ViewCompat.setAlpha(bannerViewLayout, 0.6f)
        ViewCompat.animate(bannerViewLayout)
                .alpha(1.0f)
                .translationY(0f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(ANIMATE_IN_DURATION.toLong())
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationEnd(view: View?) {
                    }

                    override fun onAnimationCancel(view: View?) {
                    }

                    override fun onAnimationStart(view: View?) {
                        view?.visibility = View.VISIBLE
                    }

                }).start()
    }

    private fun animateOut() {
        ViewCompat.animate(bannerViewLayout)
                .alpha(0.6f)
                .translationY(-(bannerViewLayout.height.toFloat()))
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(ANIMATE_OUT_DURATION.toLong())
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationEnd(view: View?) {
                        view?.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(view: View?) {
                    }

                    override fun onAnimationStart(view: View?) {
                    }
                })
    }

    private fun showBannerView() {
        if (!isShow) {
            isShow = true
            animateIn()
        }
    }

    private fun hideBannerView() {
        if (isShow) {
            isShow = false
            animateOut()
        }
    }

    internal fun scheduleTime(duration: Duration = Duration.MEDIUM) {
        cancelAllSchedule()
        mHandler.sendEmptyMessage(MSG_SHOW)
        restoreSchedule(duration.timeout)
    }

    private fun cancelAllSchedule() {
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun restoreSchedule(timeout: Long) {
        if (mCommonButtonListener == null && mConfirmCancelListener == null && mInputListener == null) {
            mHandler.sendEmptyMessageDelayed(MSG_DISMISS, timeout)
        }
    }

    // need set inner.
    private inner class Behavior : SwipeDismissBehavior<View>() {
        override fun canSwipeDismissView(view: View): Boolean {
            return view is LinearLayout
        }

        override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent): Boolean {
            if (parent?.isPointInChildBounds(child, event.x.toInt(), event.y.toInt()) == true) {
                when (event.actionMasked) {
                    ACTION_DOWN -> cancelAllSchedule()
                    ACTION_UP, ACTION_CANCEL -> restoreSchedule(1000L)
                }
            }
            return super.onInterceptTouchEvent(parent, child, event)
        }
    }

    private fun getBannerViewIndex(decorView: ViewGroup): Int {
        for (i in 0 until decorView.childCount) {
            if (decorView.getChildAt(i) is BannerViewLayout) {
                return i
            }
        }
        return -2
    }

    internal fun createView(message: String,
                            title: String,
                            @ColorRes backgroundColor: Int,
                            @DrawableRes drawableResId: Int,
                            commonButtonListener: CommonButtonListener?, commonButtonText: String?,
                            confirmCancelListener: ConfirmCancelListener?, confirmButtonText: String?, cancelButtonText: String?,
                            inputListener: InputListener?, sendButtonText: String?, vararg inputHint: String?) {
        this.mCommonButtonListener = commonButtonListener
        this.mConfirmCancelListener = confirmCancelListener
        this.mInputListener = inputListener
        val currentActivity = BannerViewLifecycleHandler.INSTANCE.getCurrentActivity()
        mDecorView = currentActivity!!.window.decorView as ViewGroup
        val messageViewIndex = getBannerViewIndex(mDecorView)

        // if activity already has a message view, retrieving it from decorview
        if (-2 != messageViewIndex) {
            mDecorView.removeViewAt(messageViewIndex)
        }
        bannerViewLayout = BannerViewLayout(currentActivity)
        val rootLayout = LayoutInflater.from(currentActivity).inflate(R.layout.banner_view, bannerViewLayout, false)
        bannerViewLayout.addView(rootLayout)
        mDecorView.addView(bannerViewLayout, mDecorView.childCount)
        bannerViewLayout.visibility = View.INVISIBLE

        // find all widgets
        val rootLL = rootLayout.findViewById(R.id.root) as LinearLayout
        val messageTV = rootLayout.findViewById(R.id.message) as TextView
        val titleTV = rootLayout.findViewById(R.id.title) as TextView
        val iconIV = rootLayout.findViewById(R.id.icon) as ImageView
        val confirmTV = rootLayout.findViewById(R.id.confirm) as TextView
        val cancelTV = rootLayout.findViewById(R.id.cancel) as TextView
        val commonTV = rootLayout.findViewById(R.id.common) as TextView
        val inputAreaRL = rootLayout.findViewById(R.id.input_area) as RelativeLayout
        val inputET = rootLayout.findViewById(R.id.input) as EditText
        val sendTV = rootLayout.findViewById(R.id.send) as TextView

        rootLL.setPadding(0, currentActivity.getStatusHeight(), 0, 0)
        rootLL.setBackgroundResource(backgroundColor)
        inputAreaRL.setBackgroundResource(backgroundColor)
        messageTV.text = message
        titleTV.text = title
        iconIV.setImageDrawable(currentActivity.resources.getDrawable(drawableResId))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bannerViewLayout.elevation = 5f
        }

        val lp = rootLL.layoutParams
        if (lp is CoordinatorLayout.LayoutParams/* && null == commonButtonListener && null == confirmCancelListener && null == inputListener*/) {
            val behavior = Behavior()
            behavior.setStartAlphaSwipeDistance(0.1f)
            behavior.setEndAlphaSwipeDistance(0.7f)
            behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)
            behavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
                override fun onDismiss(view: View) {
                    hideBannerView()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bannerViewLayout.elevation = 0f
                    }
                }

                override fun onDragStateChanged(state: Int) {
                    when (state) {
                        SwipeDismissBehavior.STATE_DRAGGING, SwipeDismissBehavior.STATE_SETTLING ->
                            // If the view is being dragged or settling, cancel the timeout
                            cancelAllSchedule()
                        SwipeDismissBehavior.STATE_IDLE ->
                            // If the view has been released and is idle, restore the timeout
                            restoreSchedule(1000)
                    }
                }
            })
            lp.behavior = behavior
        }

        // for common button
        if (null != commonButtonListener) {
            commonTV.visibility = View.VISIBLE
            commonTV.setOnClickListener { v ->
                cancelAllSchedule()
                mHandler.sendEmptyMessage(MSG_DISMISS)
                commonButtonListener.onCommonClick(v)
            }
            commonTV.text = commonButtonText
        } else {
            commonTV.visibility = View.GONE
        }

        // for confirm and cancel button
        if (null != confirmCancelListener) {
            confirmTV.visibility = View.VISIBLE
            cancelTV.visibility = View.VISIBLE
            confirmTV.setOnClickListener { v ->
                cancelAllSchedule()
                mHandler.sendEmptyMessage(MSG_DISMISS)
                confirmCancelListener.onConfirmClick(v)
            }
            cancelTV.setOnClickListener { v ->
                cancelAllSchedule()
                mHandler.sendEmptyMessage(MSG_DISMISS)
                confirmCancelListener.onCancelClick(v)
            }
            confirmTV.text = confirmButtonText
            cancelTV.text = cancelButtonText
        } else {
            confirmTV.visibility = View.GONE
            cancelTV.visibility = View.GONE
        }

        // for onInput button and input area
        if (null != inputListener) {
            inputAreaRL.visibility = View.VISIBLE
            val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputHint.isNotEmpty()) {
                inputET.hint = inputHint[0]
            }
            sendTV.text = sendButtonText
            sendTV.setOnClickListener(View.OnClickListener { v ->
                val content = inputET.text.toString().trim { it <= ' ' }
                if ("" == content) {
                    if (inputHint.size > 1) {
                        inputET.hint = inputHint[1]
                    } else {
                        inputET.hint = "You must input something!"
                    }
                    inputET.setHintTextColor(v.resources.getColor(R.color.hint_warning))
                    return@OnClickListener
                }
                cancelAllSchedule()
                mHandler.sendEmptyMessage(MSG_DISMISS)
                // hide the soft keyboard
                imm.hideSoftInputFromWindow(inputET.windowToken, 0)
                inputListener.onInput(content)
            })
            // show the soft keyboard automatically; request the focus on the edittext so user can
            // input directly
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            inputET.requestFocus()
            cancelAllSchedule()
        } else {
            inputAreaRL.visibility = View.GONE
        }
    }

    fun setContentView(view: View) {
        if (view !is LinearLayout) {
            throw RuntimeException("Your view must be LinearLayout")
        }
        val currentActivity = BannerViewLifecycleHandler.INSTANCE.getCurrentActivity()
        mDecorView = (currentActivity?.window?.decorView as ViewGroup?)!!

        val bannerViewIndex = getBannerViewIndex(mDecorView)
        if (bannerViewIndex != -2) {
            mDecorView.removeViewAt(bannerViewIndex)
        }

        bannerViewLayout = BannerViewLayout(currentActivity!!)
        bannerViewLayout.addView(view)
        mDecorView.addView(bannerViewLayout, mDecorView.childCount)

        bannerViewLayout.visibility = View.INVISIBLE
        view.setPadding(0, currentActivity.getStatusHeight(), 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bannerViewLayout.elevation = 5f
        }

        val layoutParams = view.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = Behavior().apply {
                setStartAlphaSwipeDistance(0.1f)
                setEndAlphaSwipeDistance(0.7f)
                setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)
                setListener(object : SwipeDismissBehavior.OnDismissListener {
                    override fun onDismiss(view: View?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bannerViewLayout.elevation = 0f
                        }
                    }

                    override fun onDragStateChanged(state: Int) {
                        when (state) {
                            SwipeDismissBehavior.STATE_DRAGGING,
                            SwipeDismissBehavior.STATE_SETTLING -> cancelAllSchedule()
                            SwipeDismissBehavior.STATE_IDLE -> restoreSchedule(1000L)
                        }
                    }
                })
            }

            layoutParams.behavior = behavior
        }
    }
}

interface CommonButtonListener {
    fun onCommonClick(self: View)
}

interface ConfirmCancelListener {
    fun onConfirmClick(self: View)

    fun onCancelClick(self: View)
}

interface InputListener {
    fun onInput(content: String)
}

// Duration SHORT,MEDIUM,LONG
sealed class Duration {
    abstract val timeout: Long

    object SHORT : Duration() {
        override val timeout: Long
            get() = 1000L
    }

    object MEDIUM : Duration() {
        override val timeout: Long
            get() = 1500L
    }

    object LONG : Duration() {
        override val timeout: Long
            get() = 2000L
    }
}