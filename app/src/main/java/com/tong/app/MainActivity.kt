package com.tong.app

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tong.banner.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        success.setOnClickListener { BannerViewManager.showCommonBanner(Level.SUCCESS, Duration.MEDIUM, "This is a sample of success top message.", null) }

        error.setOnClickListener { BannerViewManager.showCommonBanner(Level.ERROR, Duration.LONG, "This is a sample of error top message.", null) }

        warning.setOnClickListener { BannerViewManager.showCommonBanner(Level.WARNING, Duration.MEDIUM, "This is a sample of warning top message.", null) }

        info.setOnClickListener { BannerViewManager.showCommonBanner(message = "This is a sample of info top message.") }

        confirm_and_cancel.setOnClickListener {
            BannerViewManager.showConfirmCancelBanner(Level.WARNING, Duration.LONG, "This is a sample of confirm and cancel button top message.", null, "Confirm", "Cancel", object : ConfirmCancelListener {
                override fun onCancelClick(self: View) {
                    hello.text = "cancel button click"
                }

                override fun onConfirmClick(self: View) {
                    hello.text = "confirm button clicked"
                }

            })
        }

        common.setOnClickListener {
            BannerViewManager.showCommonBanner(Level.SUCCESS, Duration.MEDIUM, "This is a sample of common button top message.", null,
                    "Common", object : CommonButtonListener {
                override fun onCommonClick(self: View) {
                    hello.text = "common button click"
                }
            })
        }

        input.setOnClickListener {
            BannerViewManager.showInputBanner(Level.INFO, Duration.LONG, "请告诉我WI-FI密码多少?", "提示", "给你",
                    object : InputListener {

                        override fun onInput(content: String) {
                            hello.text = "Input content:$content"
                        }
                    }, "WI-FI是密码是...", "不要告诉我你家的WI-FI没有密码...")
        }

        toast.setOnClickListener {
            val textView = TextView(this@MainActivity)
            textView.text = "This is a test"
            textView.setTextColor(Color.BLACK)
            textView.textSize = 50f
            val layout = LinearLayout(this@MainActivity)
            layout.addView(textView)
            layout.setBackgroundColor(Color.WHITE)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layout.layoutParams = params
            BannerViewManager.show(layout)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
