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
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Menggunakan ID baru: btnRegisterSubmit
        binding.btnRegisterSubmit.setOnClickListener {
            performRegister()
        }

        // Menggunakan ID baru: btnBackToLogin (Teks "Login" di bagian bawah)
        binding.btnBackToLogin.setOnClickListener {
            finish()
        }

        // Fitur Baru: Tombol Back (Panah putih di Header Merah)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Fitur Baru: Logika manual klik mata untuk Show/Hide Password
        binding.ivToggleRegisterPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Tampilkan password asli
                binding.etRegisterPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
            } else {
                // Sembunyikan password menjadi dot/bullet
                binding.etRegisterPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            }
            // Kembalikan posisi kursor ketikan ke bagian paling akhir
            binding.etRegisterPassword.setSelection(binding.etRegisterPassword.text.length)
        }
    }

    private fun performRegister() {
        // Penyesuaian variabel ID EditText yang baru
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()

        // IMK: Pencegahan Kesalahan (Error dialihkan langsung ke EditText karena TextInputLayout dilepas)
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Nama lengkap tidak boleh kosong"
            binding.etFullName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.etRegisterEmail.error = "Email tidak boleh kosong"
            binding.etRegisterEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.error = "Format email tidak valid"
            binding.etRegisterEmail.error = "Format email tidak valid"
            binding.etRegisterEmail.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "Nomor telepon tidak boleh kosong"
            binding.etPhone.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.etRegisterPassword.error = "Kata sandi tidak boleh kosong"
            binding.etRegisterPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.etRegisterPassword.error = "Kata sandi minimal harus 6 karakter"
            binding.etRegisterPassword.requestFocus()
            return
        }

        // Validasi Tambahan: Checkbox Syarat & Ketentuan wajib dicentang
        if (!binding.cbTerms.isChecked) {
            Toast.makeText(this, "Anda harus menyetujui Syarat & Ketentuan", Toast.LENGTH_LONG).show()
            binding.cbTerms.requestFocus()
            return
        }

        // Tampilkan loading indicator (Umpan balik langsung IMK)
        // Menggunakan tombol submit yang di-disable sebagai pengganti ProgressBar
        binding.btnRegisterSubmit.isEnabled = false
        binding.btnRegisterSubmit.text = "Processing..."

        // Payload JSON tetap aman sesuai model struktur tugasmu
        val request = RegisterRequest(fullName, email, password, phone)

        // Memanggil API register.php secara asinkron
        RetrofitClient.instance.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                binding.btnRegisterSubmit.isEnabled = true
                binding.btnRegisterSubmit.text = "Register  ➔"

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
                binding.btnRegisterSubmit.isEnabled = true
                binding.btnRegisterSubmit.text = "Register  ➔"
                // Umpan Balik pesan Error jika koneksi internet/server gagal
                Toast.makeText(this@RegisterActivity, "Koneksi gagal: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}