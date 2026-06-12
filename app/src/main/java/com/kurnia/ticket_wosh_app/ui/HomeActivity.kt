package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.R
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Cek jika pengguna belum login
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Tampilkan nama pengguna aktif (IMK Kejelasan & Umpan Balik)
        val fullName = sessionManager.getFullName() ?: "Penumpang Whoosh"
        binding.tvUserName.text = fullName

        setupMenuListeners()
        setupBottomNavigation()
    }

    private fun setupMenuListeners() {
        // Layanan Utama - Pesan Tiket & Cari Jadwal
        binding.menuBookTicket.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.menuSchedules.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Tiket Saya
        binding.menuMyTicket.setOnClickListener {
            Toast.makeText(this, "Membuka riwayat tiket aktif Anda...", Toast.LENGTH_SHORT).show()
            // Ke status pembayaran / tiket (simulasi data)
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        // Fitur Simulasi Tambahan (BRImo style)
        binding.menuRoutes.setOnClickListener {
            Toast.makeText(this, "Fitur Peta Rute Whoosh akan segera hadir!", Toast.LENGTH_SHORT).show()
        }

        binding.menuRefund.setOnClickListener {
            Toast.makeText(this, "Fitur Pengajuan Refund tiket dapat dilakukan H-3.", Toast.LENGTH_SHORT).show()
        }

        binding.menuStations.setOnClickListener {
            Toast.makeText(this, "Menampilkan 4 Stasiun Utama: Halim, Karawang, Padalarang, Tegalluar.", Toast.LENGTH_LONG).show()
        }

        binding.menuFood.setOnClickListener {
            Toast.makeText(this, "Pemesanan kuliner kereta (F&B) bisa diakses saat perjalanan.", Toast.LENGTH_SHORT).show()
        }

        binding.menuSupport.setOnClickListener {
            Toast.makeText(this, "Menghubungkan ke WhatsApp Customer Service Whoosh...", Toast.LENGTH_SHORT).show()
        }

        // Aksi Wallet / Card
        binding.btnTopUp.setOnClickListener {
            Toast.makeText(this, "Fitur Isi Ulang Saldo Terintegrasi QRIS.", Toast.LENGTH_SHORT).show()
        }

        binding.btnPromo.setOnClickListener {
            Toast.makeText(this, "Menampilkan daftar kupon potongan harga tiket Whoosh.", Toast.LENGTH_SHORT).show()
        }

        binding.btnQRScan.setOnClickListener {
            Toast.makeText(this, "Membuka kamera pemindai gate tiket stasiun...", Toast.LENGTH_SHORT).show()
        }

        // Logout
        binding.btnHomeLogout.setOnClickListener {
            sessionManager.logout()
            Toast.makeText(this, "Sesi berakhir. Logout berhasil.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnNotification.setOnClickListener {
            Toast.makeText(this, "Tidak ada notifikasi perjalanan baru saat ini.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Berada di home
                    true
                }
                R.id.navigation_history -> {
                    Toast.makeText(this, "Riwayat Transaksi Pemesanan Tiket", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_inbox -> {
                    Toast.makeText(this, "Pesan Masuk & Pengumuman Operasional", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profile -> {
                    Toast.makeText(this, "Detail Profil & Pengaturan Akun: ${sessionManager.getEmail()}", Toast.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
        }
    }
}
