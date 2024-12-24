package com.example.libRecipe.ui.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.example.libRecipe.model.User
import com.example.libRecipe.util.SingleLiveEvent

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val email = ObservableField<String>("")
    val password = ObservableField<String>("")
    val btnSelected = ObservableBoolean(false)

    val navigateToHomePage = SingleLiveEvent<Boolean>()
    val errorMessage = MutableLiveData<String>() // Correct declaration
    fun onEmailChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        btnSelected.set(isFormValid())
    }

    fun onPasswordChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        btnSelected.set(isFormValid())
    }

    fun login() {
        // Hardcoded login logic
        if (email.get() == "admin@gmail.com" && password.get() == "admin") {
            navigateToHomePage.value = true
        }
        else {
            navigateToHomePage.value = false
            errorMessage.value = "You have no privilege"  // Set error message on failure
        }
    }

    private fun isFormValid(): Boolean {
        return email.get()?.isNotEmpty() == true && password.get()?.isNotEmpty() == true
    }
}
