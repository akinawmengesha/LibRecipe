package com.example.libRecipe.ui.login

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import com.example.libRecipe.R

class CustomProgressDialog(context: Context) : Dialog(context) {
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)  // Custom layout for progress dialog
        //progressBar = findViewById(com.facebook.shimmer.R.id.radial)  // Ensure the ProgressBar ID matches your layout
    }

    fun showProgress() {
        this.show()
    }

    fun dismissProgress() {
        this.dismiss()
    }
}
