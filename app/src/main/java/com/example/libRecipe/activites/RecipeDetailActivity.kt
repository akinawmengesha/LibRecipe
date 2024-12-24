package com.example.libRecipe.activites

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.libRecipe.R
import com.example.libRecipe.models.ResultListing
import org.jsoup.Jsoup

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var readyInMinutesTextView: TextView
    private lateinit var sourceNameTextView: TextView
    private lateinit var sourceUrlTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // Initialize views
        recipeImageView = findViewById(R.id.recipeImageView)
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        readyInMinutesTextView = findViewById(R.id.readyInMinutesTextView)
        sourceNameTextView = findViewById(R.id.sourceNameTextView)
        sourceUrlTextView = findViewById(R.id.sourceUrlTextView)

        // Retrieve the recipe data passed via the Intent
        val recipe: ResultListing? = intent.getParcelableExtra("RECIPE_DETAIL")

        // Check if the recipe is not null and populate the UI with the recipe data
        recipe?.let {
            populateRecipeDetails(it)
        }
    }

    private fun populateRecipeDetails(recipe: ResultListing) {
        // Set the recipe image using Coil
        recipeImageView.load(recipe.image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }

        // Set the title, description, and other fields
        titleTextView.text = recipe.title
        readyInMinutesTextView.text = getString(R.string.ready_in_minutes, recipe.readyInMinutes)

        // Parse and display description
        parseHtml(descriptionTextView, recipe.summary)

        // Set source name and URL
        sourceNameTextView.text = recipe.sourceName
        sourceUrlTextView.text = recipe.sourceUrl
    }

    // Method to parse HTML content (if any) in the description
    private fun parseHtml(textView: TextView, description: String?) {
        if (!description.isNullOrEmpty()) {
            val desc = Jsoup.parse(description).text()  // Parse the HTML
            textView.text = desc
        }
    }
}
