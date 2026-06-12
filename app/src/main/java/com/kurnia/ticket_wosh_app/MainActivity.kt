package com.kurnia.ticket_wosh_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.ui.HomeActivity
import com.kurnia.ticket_wosh_app.ui.LoginActivity
import com.kurnia.ticket_wosh_app.ui.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(this)
        
        // Routing awal berdasarkan session login pengguna
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}