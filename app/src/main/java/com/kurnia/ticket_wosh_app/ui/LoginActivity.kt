package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Memuat IP address server yang tersimpan agar tidak perlu mengetik ulang
//        binding.etServerIp.setText(sessionManager.getServerIp())

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

//        binding.btnSaveIp.setOnClickListener {
//            val ip = binding.etServerIp.text.toString().trim()
//            if (ip.isNotEmpty()) {
//                sessionManager.saveServerIp(ip)
//                Toast.makeText(this, "IP Server disimpan: $ip", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "IP tidak boleh kosong", Toast.LENGTH_SHORT).show()
//            }
//        }

        // Logika kustom untuk toggle show/hide password (menggantikan passwordToggleEnabled lama)
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Tampilkan password text asli
                binding.etPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                // Ganti icon mata jika ada asetnya, atau biarkan bawaan android jika memakai ic_menu_view
            } else {
                // Sembunyikan password (kembali jadi dot/bullet)
                binding.etPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            }
            // Kembalikan posisi kursor teks ke bagian paling akhir agar tidak lompat ke depan
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // IMK: Pencegahan Kesalahan (Error Prevention) langsung pada EditText
        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            binding.etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format email tidak valid"
            binding.etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Kata sandi tidak boleh kosong"
            binding.etPassword.requestFocus()
            return
        }

        // Simpan IP Address yang sedang aktif ke Retrofit client
//        val ip = binding.etServerIp.text.toString().trim()
//        if (ip.isNotEmpty()) {
//            sessionManager.saveServerIp(ip)
//        }

        // Tampilkan loading indicator (Feedback IMK)
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        // Simulasi Login (Karena backend hanya menyediakan endpoint register,
        // login langsung sukses untuk kemudahan demo interaksi pengguna)
        binding.root.postDelayed({
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true

            // Simpan session pengguna (Nama default & data dummy untuk login)
            sessionManager.saveSession(
                userId = 1,
                fullName = "Kurnia Woosh Penumpang",
                email = email,
                phone = "08123456789"
            )

            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
            goToHome()
        }, 1200)
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}