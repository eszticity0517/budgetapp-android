package com.example.budgetapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)
        supportActionBar?.hide()
    }

    fun goToSummaryPage(view: View?) {
        val intent = Intent(this@OpeningActivity, MainActivity::class.java)
        startActivity(intent)
    }
}