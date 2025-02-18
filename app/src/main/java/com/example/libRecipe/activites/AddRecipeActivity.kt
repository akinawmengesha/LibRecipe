package com.example.libRecipe.activites

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.libRecipe.R
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AddRecipeActivity : AppCompatActivity() {

    @Inject lateinit var foodRecipeDao: FoodRecipeDao // Inject the DAO
    private val postViewModel: RecipesViewModel by viewModels()

    private var selectedImagePath: String? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        // UI Components
        val titleEditText = findViewById<EditText>(R.id.editTextRecipeTitle)
        val summaryEditText = findViewById<EditText>(R.id.editTextRecipeSummary)
        val minutesEditText = findViewById<EditText>(R.id.editTextReadyInMinutes) // "readyInMinutes"
        val saveButton = findViewById<Button>(R.id.buttonSaveRecipe)
        val imageButton = findViewById<ImageButton>(R.id.imageButtonAddRecipeImage)
        imageView = findViewById(R.id.imageViewRecipe) // ImageView to display the selected image

        // Image selection from gallery
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        // Save button functionality
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val summary = summaryEditText.text.toString().trim()
            val readyInMinutes = minutesEditText.text.toString().trim().toIntOrNull() ?: 0

            // Validate input
            if (title.isEmpty() || summary.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Store the image file path if selected
            val imagePath = selectedImagePath ?: ""

            // Create a new Recipe entry
            val newRecipe = ResultListing(
                recipeId = 0, // Auto-generated ID
                aggregateLikes = 0,
                cheap = false,
                dairyFree = false,
                glutenFree = false,
                image = imagePath, // Store absolute file path
                readyInMinutes = readyInMinutes,
                sourceName = "",
                sourceUrl = "",
                summary = summary,
                title = title,
                vegan = false,
                vegetarian = false,
                veryHealthy = false
            )

            // Save to database
            lifecycleScope.launch {
                foodRecipeDao.insertRecipeResultListing(newRecipe)
                Toast.makeText(this@AddRecipeActivity, "Recipe added!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
        }
    }

    // Handle Image Selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedUri = data.data
            selectedUri?.let { uri ->
                selectedImagePath = getRealPathFromURI(uri) // Convert to absolute path
                if (!selectedImagePath.isNullOrEmpty()) {
                    imageView.load(File(selectedImagePath!!)) // Load the image
                } else {
                    imageView.setImageResource(R.drawable.ic_error_placeholder)
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Convert URI to Absolute File Path
    private fun getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null
        val cursor: Cursor? = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }

    companion object {
        const val IMAGE_PICK_REQUEST_CODE = 1001
    }
}
