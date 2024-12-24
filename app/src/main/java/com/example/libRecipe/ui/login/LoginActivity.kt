package com.example.libRecipe.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.libRecipe.R
import com.example.libRecipe.activites.HomePage
import com.example.libRecipe.databinding.ActivityLoginBinding
import com.example.libRecipe.ui.login.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    // Initialize the ViewModel using by viewModels() delegate
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use DataBinding to inflate the layout and bind the ViewModel
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Set the ViewModel for the layout
        binding.viewmodel = loginViewModel
        binding.lifecycleOwner = this // Ensure LiveData is observed

        // Observe the navigation flag to transition to MainActivity
        loginViewModel.navigateToHomePage.observe(this, Observer { navigate ->
            if (navigate == true) {
                // If login is successful, navigate to MainActivity
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish() // Finish LoginActivity to prevent going back
            }
            else {
                // Observe error message for failed login
                loginViewModel.errorMessage.observe(this, Observer { message ->
                    message?.let {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()  // Show error message
                    }

                })
            }
        })
    }

    // This method will be triggered by the onClick event in XML
    fun onLoginClick(view: android.view.View) {
        loginViewModel.login()  // Trigger login action in ViewModel
    }
}
