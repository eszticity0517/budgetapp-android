package com.example.budgetapp

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.lang.Boolean


class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)
        supportActionBar?.hide()

        val prefs = PreferenceManager.getDefaultSharedPreferences(
            baseContext
        )
        val previouslyStarted = prefs.getBoolean("isFirstRun", false)

        if (!previouslyStarted) {
            val edit = prefs.edit()
            edit.putBoolean("isFirstRun", Boolean.TRUE)
            edit.commit()
        } else
        {
            // Go to main page immediately.
            goToSummaryPage(null)
        }
    }

    fun goToSummaryPage(view: View?) {
        val intent = Intent(this@OpeningActivity, MainActivity::class.java)
        startActivity(intent)
    }
}