package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.RetrofitClient
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivityRegisterBinding
import com.kurnia.ticket_wosh_app.model.RegisterRequest
import com.kurnia.ticket_wosh_app.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.btnGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun performRegister() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // IMK: Pencegahan Kesalahan (Error Prevention)
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Nama lengkap tidak boleh kosong"
            return
        } else {
            binding.tilFullName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"
            return
        } else {
            binding.tilEmail.error = null
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            return
        } else {
            binding.tilEmail.error = null
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Nomor telepon tidak boleh kosong"
            return
        } else {
            binding.tilPhone.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Kata sandi tidak boleh kosong"
            return
        } else {
            binding.tilPassword.error = null
        }

        // Tampilkan loading indicator (Umpan balik langsung IMK)
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        // payload JSON sesuai target tugas: {"full_name": "...", "email": "...", "password": "...", "phone": "..."}
        val request = RegisterRequest(fullName, email, password, phone)

        // Memanggil API register.php secara asinkron
        RetrofitClient.instance.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                // Baris kode penanganan data sukses dari endpoint register.php
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    if (registerResponse.status == "success" || registerResponse.userId != null) {
                        // Simpan user session dari respons register sukses
                        val userId = registerResponse.userId ?: 1
                        sessionManager.saveSession(userId, fullName, email, phone)
                        
                        Toast.makeText(this@RegisterActivity, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                        
                        // Pindah ke Halaman Utama (HomeActivity)
                        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal, coba lagi nanti", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                // Umpan Balik pesan Error jika koneksi internet/server gagal
                Toast.makeText(this@RegisterActivity, "Koneksi gagal: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
