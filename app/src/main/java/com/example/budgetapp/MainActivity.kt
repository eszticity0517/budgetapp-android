package com.example.budgetapp

import android.R.attr.*
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        actionBar?.title = "Summary";
        supportActionBar?.title = "Summary";

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
           createNewCategoryDialog()
        }
    }

    private fun createNewCategoryDialog()
    {
        // TODO: put it in a container for better margins / paddings.
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val builder = AlertDialog.Builder(this)
                .setTitle("New category")
                .setView(input)
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    // TODO: save new category in database
                })
                .setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }
}