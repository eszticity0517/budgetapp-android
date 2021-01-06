package com.example.budgetapp.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Element

class ElementDetailsActivity : AppCompatActivity() {

    private var elementId: Long? = null
    private var elementCategoryId: Long? = null

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
        elementId = element?.id
        elementCategoryId = element?.categoryId

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

    // Let's leave it here for now.
//    override fun onBackPressed() {
//        // Go back to summary page.
//        super.onBackPressed()
//        val intent = Intent(this, CategoryActivity::class.java)
//        intent.putExtra("categoryName", "foodie")
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        startActivity(intent)
//        finish()
//    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == R.id.editButton) {
            CreateEditElementDialog()
        }

        if (id == R.id.deleteButton) {
            CreateDeleteElementDialog()
        }

        if (id == android.R.id.home) {
           onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun CreateEditElementDialog()
    {
        val originalPriceProductNameText = EditText(this)
        originalPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        originalPriceProductNameText.hint = "Original product name"

        val originalPriceProductPriceText = EditText(this)
        originalPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        originalPriceProductPriceText.hint = "Original product price"

        val lowerPriceProductNameText = EditText(this)
        lowerPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        lowerPriceProductNameText.hint = "Cheaper product name"

        val lowerPriceProductPriceText = EditText(this)
        lowerPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        lowerPriceProductPriceText.hint = "Cheaper product price"

        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL

        linearLayout.addView(originalPriceProductNameText)
        linearLayout.addView(originalPriceProductPriceText)
        linearLayout.addView(lowerPriceProductNameText)
        linearLayout.addView(lowerPriceProductPriceText)

        // TODO: put it in a container for better margins / paddings.
        val builder = AlertDialog.Builder(this)
            .setTitle("New element")
            .setView(linearLayout)
            .setPositiveButton(
                    android.R.string.ok, null
            )
            .setNegativeButton(android.R.string.cancel, null)

        val dialog =builder.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val lowerPriceProductName = lowerPriceProductNameText.text.toString()
            val lowerPriceProductPrice = lowerPriceProductPriceText.text.toString()

            val originalPriceProductName = originalPriceProductNameText.text.toString()
            val originalPriceProductPrice = originalPriceProductPriceText.text.toString()

            when {
                lowerPriceProductName.isNullOrEmpty() -> {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_mandatory)
                }
                originalPriceProductName.isNullOrEmpty() -> {
                    originalPriceProductNameText?.error = getString(R.string.name_is_mandatory)
                }

                lowerPriceProductPrice.isNullOrEmpty() -> {
                    lowerPriceProductPriceText?.error = getString(R.string.price_is_mandatory)
                }
                originalPriceProductPrice.isNullOrEmpty() -> {
                    originalPriceProductPriceText?.error = getString(R.string.price_is_mandatory)
                }

                CategoryActivity.GetAllElements(this).execute().get().any { it?.lowerPriceProductName == lowerPriceProductName && it?.id != elementId} -> {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                CategoryActivity.GetAllElements(this).execute().get().any { it?.originalPriceProductName == originalPriceProductName && it?.id != elementId} -> {
                    originalPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                else -> {
                    UpdateElement(
                            this,
                            lowerPriceProductPrice.toInt(),
                            originalPriceProductPrice.toInt(),
                            lowerPriceProductName,
                            originalPriceProductName,
                            this.elementId
                    ).execute()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun CreateDeleteElementDialog()
    {
        val builder = AlertDialog.Builder(this)
            .setTitle("Delete element")
            .setMessage("Are you sure?")
            .setPositiveButton(
                    android.R.string.ok, null
            )
            .setNegativeButton(android.R.string.cancel, null)

        val dialog =builder.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val affectedRows = DeleteElement(this, elementId).execute().get()

            if (affectedRows > 0)
            {
                dialog.dismiss()

                // Go back to summary page.
                val intent = Intent(this@ElementDetailsActivity, MainActivity::class.java)
                intent.putExtra("categoryName", GetCategoryNameById(this, elementCategoryId).execute().get())
                startActivity(intent)
            }
        }
    }


    // TODO: this has a duplicate, find a way to put everything to helper class.
    /**
     * Gets all existing elements related to a specific category spotted by its ID.
     */
    class GetCategoryNameById(mContext: Context, categoryId: Long?):  AsyncTask<String, Long, String?>() {
        private var context: Context = mContext
        private var categoryId: Long? = categoryId

        override fun doInBackground(json: Array<String?>?): String? {
            var categoryName: String?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                categoryName = budgetAppDatabase.CategoryDao().findById(categoryId)?.name
                budgetAppDatabase.close()
                categoryName
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get category.", e)
                null
            }
        }
    }

    /**
     * Deletes category from the database.
     */
    class DeleteElement(mContext: Context, elementId: Long?): AsyncTask<String, Long, Int>() {
        private var context: Context = mContext
        private var elementId = elementId

        override fun doInBackground(json: Array<String?>?): Int? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                val elementDeletion = budgetAppDatabase.ElementDao().deleteById(elementId)
                elementDeletion
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to delete category.", e)
                null
            }
        }
    }

    /**
     * Inserts a very new category in the database.
     */
    class UpdateElement(
            mContext: Context,
            lowerPrice: Int,
            originalPrice: Int,
            lowerPriceProductName: String,
            originalPriceProductName: String,
            elementId: Long?
    ): AsyncTask<String, Long, Element?>() {
        private var context: Context = mContext
        private var lowerPriceProductName = lowerPriceProductName
        private var originalPriceProductName = originalPriceProductName

        private var lowerPrice = lowerPrice
        private var originalPrice = originalPrice
        private var elementId = elementId

        override fun doInBackground(json: Array<String?>?): Element? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                var element = budgetAppDatabase.ElementDao().findById(elementId)
                element?.lowerPrice = lowerPrice
                element?.lowerPriceProductName = lowerPriceProductName
                element?.originalPrice = originalPrice
                element?.originalPriceProductName = originalPriceProductName

                budgetAppDatabase.ElementDao().update(element)
                budgetAppDatabase.close()
                element
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to save category.", e)
                null
            }
        }
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