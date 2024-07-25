package vn.study.qlchsach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import vn.study.qlchsach.databinding.ActivityMainBinding
import vn.study.qlchsach.fragments.BookFragment
import vn.study.qlchsach.fragments.PublisherFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() {
            return _binding!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(BookFragment())
        with(binding.bottomNavigationView) {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigationBook -> {
                        loadFragment(BookFragment())
                    }

                    R.id.navigationPublisher -> {
                        loadFragment(PublisherFragment())
                    }

                    R.id.navigationStore -> {

                    }

                    R.id.navigationAuthor -> {

                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(fragment::class.java.name)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}