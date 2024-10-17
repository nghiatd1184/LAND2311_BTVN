package com.nghiatd.mixic

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
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
    }

    private fun initView() {
        val sharedPref = this.getSharedPreferences("data", MODE_PRIVATE)
//        val playlists = listOf(
//            Playlist("1", "My Playlist", mutableListOf()),
//        )
//        val json = Gson().toJson(playlists)
//        Log.d("NGHIA", "initView: $json")
//        sharedPref.edit().putString("playlists", json).apply()
        val theme = sharedPref.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)

        window.statusBarColor = getColor(R.color.background_color)
        window.navigationBarColor = getColor(R.color.main_component_reverse)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, SplashFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

}