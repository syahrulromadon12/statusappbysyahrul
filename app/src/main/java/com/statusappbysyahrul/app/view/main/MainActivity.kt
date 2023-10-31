package com.statusappbysyahrul.app.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.statusappbysyahrul.app.R
import com.statusappbysyahrul.app.databinding.ActivityMainBinding
import com.statusappbysyahrul.app.view.ViewModelFactory
import com.statusappbysyahrul.app.view.auth.login.LoginActivity
import com.statusappbysyahrul.app.view.detail.DetailActivity
import com.statusappbysyahrul.app.view.maps.MapsActivity
import com.statusappbysyahrul.app.view.poststory.PostStoryActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy {
        MainAdapter{
            Intent(this, DetailActivity::class.java).apply {
                putExtra("id", it.id)
                startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                viewModel.getStories()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.stories.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
            binding.progressBar.visibility = View.GONE
        }

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        with(binding) {
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }

        binding.postBtn.setOnClickListener {
            val intent = Intent(this, PostStoryActivity::class.java)
            startActivity(intent)
        }

        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.maps -> {
                val maps = Intent(this, MapsActivity::class.java)
                startActivity(maps)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
