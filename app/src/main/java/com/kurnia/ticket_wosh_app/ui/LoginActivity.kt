package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.RetrofitClient
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivityLoginBinding
import com.kurnia.ticket_wosh_app.model.LoginRequest
import com.kurnia.ticket_wosh_app.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Memuat IP address server yang tersimpan secara otomatis
        sessionManager.getServerIp()

        // Cek jika user sudah login sebelumnya (Direct routing)
        if (sessionManager.isLoggedIn()) {
            goToHome()
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        var isPasswordVisible = false
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // IMK: Pencegahan Kesalahan (Error Prevention)
        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format email tidak valid"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Kata sandi tidak boleh kosong"
            return
        }

        val ip = sessionManager.getServerIp()

        // Tampilkan loading indicator (Feedback IMK)
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        val request = LoginRequest(email, password)

        // Melakukan panggilan API login.php sesungguhnya tanpa data dummy
        RetrofitClient.instance.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                // Baris kode penanganan data sukses dari endpoint login.php
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.status == "success" && loginResponse.userId != null) {
                        // Simpan session pengguna dari respons login riil
                        sessionManager.saveSession(
                            userId = loginResponse.userId,
                            fullName = loginResponse.fullName ?: "Penumpang Woosh",
                            email = email,
                            phone = loginResponse.phone ?: ""
                        )
                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        goToHome()
                    } else {
                        // Umpan balik kegagalan otentikasi (contoh: email atau password salah)
                        Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Gagal masuk: HTTP ${response.code()} (Respons server tidak valid)", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                // Umpan balik error koneksi server/API
                Toast.makeText(this@LoginActivity, "Koneksi ke $ip gagal: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}