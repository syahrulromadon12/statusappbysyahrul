package com.statusappbysyahrul.app.view.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.statusappbysyahrul.app.R
import com.statusappbysyahrul.app.databinding.ActivityDetailBinding
import com.statusappbysyahrul.app.view.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra("id") ?: ""

        viewModel.getDetailStoryById(id)

        viewModel.detailStory.observe(this) { detailStoryResponse ->
            val story = detailStoryResponse.story
            if (story != null) {
                binding.apply {
                    imgStory.load(story.photoUrl)
                    tvName.text = story.name ?: ""
                    tvDescription.text = story.description ?: ""

                    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val date = apiDateFormat.parse("2023-10-19T07:00:36.625Z")
                    val outputDateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                    val formattedDate = date?.let { outputDateFormat.format(it) }

                    val dateText = getString(R.string.tvDate, formattedDate)
                    tvDate.text = dateText
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
