package com.example.libRecipe.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.libRecipe.R
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.entity.FavoritesEntity
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

        // Retrieve the recipe object passed via the Intent
        val recipe: Any? = intent.getParcelableExtra("RECIPE_DETAIL")

        // Check if the recipe is an instance of ResultListing or FavoritesEntity and populate accordingly
        when (recipe) {
            is ResultListing -> populateRecipeDetailsFromApi(recipe)
            is FavoritesEntity -> populateRecipeDetailsFromDatabase(recipe)
        }
    }

    // Populate Recipe Details for ResultListing (API data)
    private fun populateRecipeDetailsFromApi(recipe: ResultListing) {
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

        // Make source URL clickable
        sourceUrlTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.sourceUrl))
            startActivity(browserIntent)
        }
    }

    // Populate Recipe Details for FavoritesEntity (Room DB data)
    private fun populateRecipeDetailsFromDatabase(recipe: FavoritesEntity) {
        // Set the recipe image using Coil
        recipeImageView.load(recipe.image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }

        // Set the title and description
        titleTextView.text = recipe.title
        descriptionTextView.text = Jsoup.parse(recipe.summary).text()

        // Make source URL clickable
        sourceUrlTextView.text = recipe.sourceUrl
        sourceUrlTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.sourceUrl))
            startActivity(browserIntent)
        }
    }

    // Method to parse HTML content (if any) in the description
    private fun parseHtml(textView: TextView, description: String?) {
        if (!description.isNullOrEmpty()) {
            val desc = Jsoup.parse(description).text()  // Parse the HTML
            textView.text = desc
        }
    }
}
