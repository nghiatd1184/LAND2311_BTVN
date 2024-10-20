package com.nghiatd.mixic

import android.app.Dialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.ui.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private var isHandleCallbackBackPress: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isHandleCallbackBackPress) {
                    showCloseAppDialog()
                } else {
                    finish()
                }
            }
        })
    }

    private fun initView() {
        val sharedPref = this.getSharedPreferences("mixic_data", MODE_PRIVATE)
        val theme = sharedPref.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())
        window.insetsController?.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.statusBarColor = getColor(R.color.background_color)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, SplashFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun showCloseAppDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_normal_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        val positiveButton = dialog.findViewById<TextView>(R.id.tv_positive)
        val negativeButton = dialog.findViewById<TextView>(R.id.tv_negative)
        tvMessage.text = getString(R.string.alert_exit_app)
        positiveButton.setOnClickListener {
            isHandleCallbackBackPress = false
            onBackPressedDispatcher.onBackPressed()
        }
        negativeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}