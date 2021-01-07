package com.example.budgetapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.persistence.tools.*
import com.example.budgetapp.persistence.tools.roomtasks.*

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
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)

        val container1 = FrameLayout(this)

        val originalPriceProductNameText = EditText(this)
        originalPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        originalPriceProductNameText.hint = "Original product name"
        originalPriceProductNameText.setSingleLine()

        originalPriceProductNameText.layoutParams = params
        container1.addView(originalPriceProductNameText);

        val container2 = FrameLayout(this)

        val originalPriceProductPriceText = EditText(this)
        originalPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        originalPriceProductPriceText.hint = "Original product price"
        originalPriceProductPriceText.setSingleLine()

        originalPriceProductPriceText.layoutParams = params
        container2.addView(originalPriceProductPriceText);

        val container3 = FrameLayout(this)

        val lowerPriceProductNameText = EditText(this)
        lowerPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        lowerPriceProductNameText.hint = "Cheaper product name"
        lowerPriceProductNameText.setSingleLine()

        lowerPriceProductNameText.layoutParams = params
        container3.addView(lowerPriceProductNameText);

        val container4 = FrameLayout(this)

        val lowerPriceProductPriceText = EditText(this)
        lowerPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        lowerPriceProductPriceText.hint = "Cheaper product price"
        lowerPriceProductPriceText.setSingleLine()

        lowerPriceProductPriceText.layoutParams = params
        container4.addView(lowerPriceProductPriceText);

        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL

        linearLayout.addView(container1)
        linearLayout.addView(container2)
        linearLayout.addView(container3)
        linearLayout.addView(container4)

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

                GetAllElements(this).execute().get().any { it?.lowerPriceProductName == lowerPriceProductName && it?.id != elementId} -> {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                GetAllElements(this).execute().get().any { it?.originalPriceProductName == originalPriceProductName && it?.id != elementId} -> {
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
}