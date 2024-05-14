package com.nghiatd.networking

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nghiatd.networking.adapter.UserAdapter
import com.nghiatd.networking.databinding.ActivityMainBinding
import com.nghiatd.networking.model.User
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.ArrayList
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }
    private var listUser = arrayListOf<User>()
    private val adapter:UserAdapter by lazy {
        UserAdapter(listUser)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myHandler = Handler(Looper.getMainLooper()) {
            val list = it.data.getParcelableArrayList<User>("users")
            list?.forEach {
                listUser.add(it)
            }
            adapter.notifyDataSetChanged()
            return@Handler true
        }
        val url = URL("https://api.github.com/users")

        thread(start = true) {
            val sBuilder = StringBuilder()
            try {
                val conn : HttpsURLConnection = url.openConnection() as HttpsURLConnection
                conn.connectTimeout = 60000

                val br = BufferedReader(InputStreamReader(conn.inputStream))
                var str : String?

                while (br.readLine().also { str = it } != null) {
                    if (!str.isNullOrEmpty()) {
                        sBuilder.append(str)
                    }
                }
            } catch (ex:Exception) {
                Log.d("hehe", " Ex is call : $ex")
            }

//            val modelString = sBuilder.toString().substring(2,sBuilder.length-2)
//            val modelArray = modelString.split("},{")
//            modelArray.forEach {
//                TODO()
//            }

            val users = arrayListOf<User>()
            val jsonArray = JSONArray(sBuilder.toString())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val user = User().apply {
                    if (jsonObject.has("login")) {
                        login = jsonObject.getString("login")
                    }
                    if (jsonObject.has("id")) {
                        id = jsonObject.getInt("id")
                    }
                    if (jsonObject.has("node_id")) {
                        nodeId = jsonObject.getString("node_id")
                    }
                    if (jsonObject.has("avatar_url")) {
                        avatarUrl = jsonObject.getString("avatar_url")
                    }
                }
                users.add(user)
            }
            val msg = myHandler.obtainMessage()
            msg.data =  Bundle().apply {
                putParcelableArrayList("users", ArrayList(users))
            }
            myHandler.sendMessage(msg)
        }

        binding.container.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL,false)
            adapter = this@MainActivity.adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}