package com.statusappbysyahrul.app.view.poststory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.statusappbysyahrul.app.R
import com.statusappbysyahrul.app.data.ResultState
import com.statusappbysyahrul.app.databinding.ActivityPostStoryBinding
import com.statusappbysyahrul.app.view.ViewModelFactory
import com.statusappbysyahrul.app.view.getImageUri
import com.statusappbysyahrul.app.view.main.MainActivity
import com.statusappbysyahrul.app.view.reduceFileImage
import com.statusappbysyahrul.app.view.uriToFile

class PostStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostStoryBinding
    private var currentImageUri: Uri? = null

    private val viewModel: PostStoryViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[PostStoryViewModel::class.java]
    }

    private var isLocationAllowed = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        showLoading(false)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.descEditTextLayout
        binding.switchLoc.setOnCheckedChangeListener { _, isChecked ->
            isLocationAllowed = isChecked
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uriToFile(uri, this).reduceFileImage()
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descEditText.text.toString()

            var lat: Float?
            var lon: Float?

            if (!isLocationAllowed) {
                showToast("Location permission is required to upload the image.")
                return
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val currentLat = location.latitude.toFloat()
                        val currentLon = location.longitude.toFloat()
                        lat = currentLat
                        lon = currentLon

                        viewModel.uploadImage(imageFile, description, lat ?: 0f, lon ?: 0f)
                            .observe(this) { result ->
                                when (result) {
                                    is ResultState.Loading -> {
                                        showLoading(true)
                                    }

                                    is ResultState.Success -> {
                                        result.data.message?.let { showToast(it) }
                                        showLoading(false)
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                    }

                                    is ResultState.Error -> {
                                        showToast(result.error)
                                        showLoading(false)
                                    }

                                    else -> {
                                    }
                                }
                            }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}