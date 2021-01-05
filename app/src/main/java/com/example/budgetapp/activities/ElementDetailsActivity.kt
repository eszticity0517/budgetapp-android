package com.example.budgetapp.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.R
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Element

class ElementDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_element_details)
        setSupportActionBar(findViewById(R.id.toolbar))

        val intent = intent
        val categoryName  = intent?.getStringExtra("elementName") as String

        actionBar?.title = categoryName
        supportActionBar?.title = categoryName

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val element = GetElementByName(this, categoryName).execute().get()

        // Calculating difference between lower and higher price.
        var differenceCalculated = element?.originalPrice!!.minus(element?.lowerPrice!!)

        val differenceValue: TextView = findViewById(R.id.differenceValue);

        // Showing all the saved money.
        differenceValue.text = "$differenceCalculated HUF"

        // Put original and cheaper product data in a table.

        val summaryTable : TableLayout = findViewById(R.id.summaryTable)

        // Giving TextView-s the parent's layout params.
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

        // Put original product data in table.
        val originalPriceProductNameText = TextView(this)
        originalPriceProductNameText.text = element.originalPriceProductName
        originalPriceProductNameText.layoutParams = params

        val originalPriceProductPriceText = TextView(this)
        originalPriceProductPriceText.text = element.originalPrice.toString()
        originalPriceProductPriceText.layoutParams = params

        val tableRow = TableRow(this)

        tableRow.addView(originalPriceProductNameText)
        tableRow.addView(originalPriceProductPriceText)

        summaryTable.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

        // Put cheaper product data in table.
        val lowerPriceProductNameText = TextView(this)
        lowerPriceProductNameText.text = element.lowerPriceProductName
        lowerPriceProductNameText.layoutParams = params

        val lowerPriceProductPriceText = TextView(this)
        lowerPriceProductPriceText.text = element.lowerPrice.toString()
        lowerPriceProductPriceText.layoutParams = params

        val tableRow2 = TableRow(this)

        tableRow2.addView(lowerPriceProductNameText)
        tableRow2.addView(lowerPriceProductPriceText)

        summaryTable.addView(tableRow2, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == R.id.editButton) {
            // CreateEditElementDialog()
        }

        if (id == R.id.deleteButton) {
            // CreateDeleteElementDialog()
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Gets element with the provided name.
     */
    class GetElementByName(mContext: Context, elementName: String): AsyncTask<String, Long, Element?>() {
        private var context: Context = mContext
        private var elementName: String = elementName

        override fun doInBackground(json: Array<String?>?): Element? {
            var element: Element?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                element = budgetAppDatabase.ElementDao().findByLowerPriceProductName(elementName)
                budgetAppDatabase.close()
                element
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get element.", e)
                null
            }
        }
    }
}