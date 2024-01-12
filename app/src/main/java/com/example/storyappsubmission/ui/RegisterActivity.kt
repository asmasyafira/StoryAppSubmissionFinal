package com.example.storyappsubmission.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.R
import com.example.storyappsubmission.data.DataLogin
import com.example.storyappsubmission.data.DataRegister
import com.example.storyappsubmission.data.SsPreferences
import com.example.storyappsubmission.databinding.ActivityRegisterBinding
import com.example.storyappsubmission.viewmodel.DataStoreViewModel
import com.example.storyappsubmission.viewmodel.MainViewModel
import com.example.storyappsubmission.viewmodel.MainViewModelFactory
import com.example.storyappsubmission.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicked()

        val pref = SsPreferences.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLogin().observe(this) { loginTrue ->
            if (loginTrue) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.message.observe(this) { msg ->
            registerResponse(msg)
        }

        viewModel.isLoading.observe(this) {
            onLoading(it)
        }

        viewModel.message.observe(this) { msg ->
            loginResponse(msg, dataStoreViewModel)
        }
    }

    private fun registerResponse(msg: String) {
        if (msg == "Berhasil membuat akun") {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = DataLogin(
                binding.cvEmail.text.toString(),
                binding.cvPass.text.toString()
            )
            viewModel.login(user)
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            binding.cvEmail.setErrorMsg(resources.getString(R.string.emailTaken), binding.cvEmail.text.toString())
            Toast.makeText(this, resources.getString(R.string.emailTaken), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginResponse(
        msg: String,
        dataStoreViewModel: DataStoreViewModel
    ){
        if (msg.contains("Hi!")) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = viewModel.login.value
            dataStoreViewModel.saveLogin(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun onClicked() {
        binding.seePass.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                binding.cvPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.cvSamePass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.cvPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.cvSamePass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.cvPass.text?.let { binding.cvPass.setSelection(it.length) }
            binding.cvPass.text?.let { binding.cvSamePass.setSelection(it.length) }
        }

        binding.btnRegis.setOnClickListener {
            binding.apply {
                cvName.clearFocus()
                cvEmail.clearFocus()
                cvPass.clearFocus()
                cvSamePass.clearFocus()
            }

            if (binding.cvName.nameValid && binding.cvEmail.emailValid && binding.cvPass.passValid && binding.cvSamePass.samaPassValid) {
                val dataRegisterAccount = DataRegister(
                    name = binding.cvName.text.toString().trim(),
                    email = binding.cvEmail.text.toString().trim(),
                    password = binding.cvPass.text.toString().trim()
                )
                viewModel.register(dataRegisterAccount)
            } else {
                if (!binding.cvName.nameValid) binding.cvName.error = resources.getString(R.string.noName)
                if (!binding.cvEmail.emailValid) binding.cvEmail.error = resources.getString(R.string.noEmail)
                if (!binding.cvPass.passValid) binding.cvPass.error = resources.getString(R.string.noPass)
                if (!binding.cvSamePass.samaPassValid) binding.cvSamePass.error = resources.getString(R.string.noSamePass)
                Toast.makeText(this, "Data yang dimasukkan tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}