package com.example.storyappsubmission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.R
import com.example.storyappsubmission.data.DataLogin
import com.example.storyappsubmission.data.SsPreferences
import com.example.storyappsubmission.databinding.ActivityLoginBinding
import com.example.storyappsubmission.viewmodel.DataStoreViewModel
import com.example.storyappsubmission.viewmodel.MainViewModel
import com.example.storyappsubmission.viewmodel.MainViewModelFactory
import com.example.storyappsubmission.viewmodel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animateObject()
        onClicked()

        val pref = SsPreferences.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLogin().observe(this) { loginTrue ->
            if (loginTrue) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.message.observe(this){ message ->
            loginResponse(message, dataStoreViewModel)
        }

        viewModel.isLoading.observe(this) {
            onLoading(it)
        }
    }

    private fun onClicked() {
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.seePass.setOnCheckedChangeListener { _, checked ->
            binding.cvPassLogin.transformationMethod = if (checked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.cvPassLogin.text?.let { binding.cvPassLogin.setSelection(it.length) }
        }

        binding.btnLogin.setOnClickListener {
            binding.cvEmailLogin.clearFocus()
            binding.cvPassLogin.clearFocus()

            if (dataValid()) {
                val requestLogin = DataLogin(
                    binding.cvEmailLogin.text.toString().trim(),
                    binding.cvPassLogin.text.toString().trim()
                )
                viewModel.login(requestLogin)
            } else {
                if (!binding.cvEmailLogin.emailValid) binding.cvEmailLogin.error =
                    getString(R.string.noEmail)
                if (!binding.cvPassLogin.passValid) binding.cvPassLogin.error =
                    getString(R.string.noPass)

                Toast.makeText(this, "Login Salah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dataValid(): Boolean {
        return binding.cvEmailLogin.emailValid && binding.cvPassLogin.passValid
    }

    private fun animateObject() {
        ObjectAnimator.ofFloat(binding.imgBangkit, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val cvEmail = ObjectAnimator.ofFloat(binding.cvEmailLogin, View.ALPHA, 1f).setDuration(300)
        val cvPass = ObjectAnimator.ofFloat(binding.cvPassLogin, View.ALPHA, 1f).setDuration(300)
        val seePass = ObjectAnimator.ofFloat(binding.seePass, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val btnRegis = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val txtNotRegis = ObjectAnimator.ofFloat(binding.txtNotRegis, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(cvEmail, cvPass, seePass, btnLogin, btnRegis, txtNotRegis)
            start()
        }
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun loginResponse(msg: String, dataStoreViewModel: DataStoreViewModel) {
        if (msg.contains("Hi")) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = viewModel.login.value
            dataStoreViewModel.saveLogin(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}