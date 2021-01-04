package com.example.budgetapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(findViewById(R.id.toolbar))

        val intent = intent
        val categoryName  = intent?.getStringExtra("categoryName") as String

        actionBar?.title = categoryName
        supportActionBar?.title = categoryName

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}