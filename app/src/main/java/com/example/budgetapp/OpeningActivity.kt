package com.example.budgetapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)
        supportActionBar?.hide()
    }
}