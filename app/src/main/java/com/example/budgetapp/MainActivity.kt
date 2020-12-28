package com.example.budgetapp

import android.R.attr.*
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
        val spinner: Spinner = findViewById(R.id.periodSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.periods,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                //Do something
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

    }

    private fun createNewCategoryDialog()
    {
        // TODO: put it in a container for better margins / paddings.
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val builder = AlertDialog.Builder(this)
                .setTitle("New category")
                .setView(input)
                .setPositiveButton(
                    android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, which ->
                        // TODO: save new category in database
                    })
                .setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }
}