package com.example.a5biospass

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Agregar listeners para los botones
        val btnFingerprint = findViewById<Button>(R.id.btnFingerprint)
        val btnManual = findViewById<Button>(R.id.btnManual)
        val btnControl = findViewById<Button>(R.id.btnControl)
        btnFingerprint.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FingerprintFragment())
                .addToBackStack(null)
                .commit()
        }
        btnManual.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManualFragment())
                .addToBackStack(null)
                .commit()
        }
        btnControl.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ControlFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}