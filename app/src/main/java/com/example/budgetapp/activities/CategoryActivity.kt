package com.example.budgetapp.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.R
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Element
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }

    // TODO: this has a duplicate, find a way to put everything to helper class.
    /**
     * Gets all existing elements related to a specific category spotted by its ID.
     */
    class GetAllElementsByCategory(mContext: Context, categoryId: Long?):  AsyncTask<String, Long, List<Element?>?>() {
        private var context: Context = mContext
        private var categoryId: Long? = categoryId

        override fun doInBackground(json: Array<String?>?): List<Element?>? {
            var elements: List<Element?>?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                elements = budgetAppDatabase.ElementDao().getAllByCategoryId(categoryId)
                budgetAppDatabase.close()
                elements
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get elements by category ID.", e)
                null
            }
        }
    }


}