package com.example.budgetapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)
        supportActionBar?.hide()

        val questionTitle = findViewById<TextView>(R.id.question_title)
        questionTitle.text = "What brand of food, household product, clothing you come up with the most economically?"

        val introductionTextFirst = findViewById<TextView>(R.id.introduction_text_1)
        introductionTextFirst.text = "See the difference between the most economical product and the price of the product you are used to buying."

        val introductionTextSecond = findViewById<TextView>(R.id.introduction_text_2)
        introductionTextSecond.text = "This way, you can easily summarize how much you have saved in a given category, broken down by period."
    }
}