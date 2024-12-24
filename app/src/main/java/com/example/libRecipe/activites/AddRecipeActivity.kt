package com.example.libRecipe.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.libRecipe.R
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddRecipeActivity : AppCompatActivity() {

    @Inject lateinit var foodRecipeDao: FoodRecipeDao // Inject the DAO
    private lateinit var postViewModel: RecipesViewModel

    // Variable to store the selected image URI
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        // Initialize ViewModel
        postViewModel = viewModels<RecipesViewModel>().value

        // Set up the input fields
        val titleEditText = findViewById<EditText>(R.id.editTextRecipeTitle)
        val summaryEditText = findViewById<EditText>(R.id.editTextRecipeSummary)
        val ingredientsEditText = findViewById<EditText>(R.id.editTextRecipeIngredients)
        val instructionsEditText = findViewById<EditText>(R.id.editTextRecipeInstructions)
        val saveButton = findViewById<Button>(R.id.buttonSaveRecipe)
        val imageButton = findViewById<ImageButton>(R.id.imageButtonAddRecipeImage) // ImageButton to select an image
        val imageView = findViewById<ImageView>(R.id.imageViewRecipe) // ImageView to display the selected image

        // Open the image picker when the user clicks the ImageButton
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        // Save button click listener
        saveButton.setOnClickListener {
            // Get the text input from the EditText fields
            val title = titleEditText.text.toString().trim()
            val summary = summaryEditText.text.toString().trim()
            val ingredients = ingredientsEditText.text.toString().trim()
            val instructions = instructionsEditText.text.toString().trim()

            // Validate the input fields
            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (summary.isEmpty()) {
                Toast.makeText(this, "Summary is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ingredients.isEmpty()) {
                Toast.makeText(this, "Ingredients are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (instructions.isEmpty()) {
                Toast.makeText(this, "Instructions are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the image URI or set a default if no image is selected
            val imageUri = selectedImageUri?.toString() ?: ""

            // Create a ResultListing object (mapped from input fields)
            val newRecipe = ResultListing(
                recipeId = 0, // Auto-generated ID by Room
                aggregateLikes = 0, // Set default values for non-user input
                cheap = false,
                dairyFree = false,
                glutenFree = false,
                image = imageUri,
                readyInMinutes = 0,
                sourceName = "",
                sourceUrl = "",
                summary = summary,
                title = title,
                vegan = false,
                vegetarian = false,
                veryHealthy = false
            )

            // Insert the new recipe into the database
            lifecycleScope.launch {
                // Assuming you have a method in DAO to insert the ResultListing
                // If not, you would need to adapt the DAO for ResultListing or perform the mapping manually.
                foodRecipeDao.insertRecipeResultListing(newRecipe)
                Toast.makeText(this@AddRecipeActivity, "Recipe added!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
        }
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            selectedImageUri = data.data
            val imageView = findViewById<ImageView>(R.id.imageViewRecipe)

            // Display the selected image in the ImageView
            imageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        const val IMAGE_PICK_REQUEST_CODE = 1001
    }
}
