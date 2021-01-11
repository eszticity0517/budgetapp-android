package com.example.budgetapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.persistence.entities.Element
import com.example.budgetapp.persistence.tools.*
import com.example.budgetapp.persistence.tools.roomtasks.*

class ElementDetailsActivity : AppCompatActivity() {

    private var element: Element? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_element_details)
        setSupportActionBar(findViewById(R.id.toolbar))

        val intent = intent
        val elementName  = intent?.getStringExtra("elementName") as String

        actionBar?.title = elementName
        supportActionBar?.title = elementName

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getElementAndShowInTable(elementName)
    }

    private fun getElementAndShowInTable(elementName: String)
    {
        element = GetElementByName(this, elementName).execute().get()

        // Calculating difference between lower and higher price.
        var differenceCalculated = element?.originalPrice!!.minus(element?.lowerPrice!!)

        // Put original and cheaper product data in a table.

        val summaryTable : TableLayout = findViewById(R.id.summaryTable)
        summaryTable.removeAllViews()

        // Add summary text and value first to see the saved money amount.

        val paddingValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5F,
                resources.displayMetrics
        ).toInt()

        // Giving TextView-s the parent's layout params.
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

        val summaryText = TextView(this)
        summaryText.text = "${getString(R.string.in_all)}"
        summaryText.layoutParams = params
        summaryText.setTextColor(getColor(android.R.color.holo_purple))
        summaryText.setTypeface(null, Typeface.BOLD);

        val summaryValueText = TextView(this)
        summaryValueText.text = "$differenceCalculated ${getString(R.string.currency)}"
        summaryValueText.layoutParams = params
        summaryValueText.setTextColor(getColor(android.R.color.holo_purple))
        summaryValueText.setTypeface(null, Typeface.BOLD);

        // Summary row has the same font size.
        summaryText.textSize = 18F
        summaryValueText.textSize = 18F

        summaryText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        summaryValueText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

        val summaryTableRow = TableRow(this)

        summaryTableRow.addView(summaryText)
        summaryTableRow.addView(summaryValueText)

        summaryTable.addView(summaryTableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

        // Put original product data in table.
        val originalPriceProductNameText = TextView(this)
        originalPriceProductNameText.text = element?.originalPriceProductName
        originalPriceProductNameText.layoutParams = params

        val originalPriceProductPriceText = TextView(this)
        originalPriceProductPriceText.text = "${element?.originalPrice.toString()} ${getString(R.string.currency)}"
        originalPriceProductPriceText.layoutParams = params

        // Summary row has the same font size.
        originalPriceProductPriceText.textSize = 18F
        originalPriceProductNameText.textSize = 18F

        originalPriceProductPriceText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        originalPriceProductNameText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

        val tableRow = TableRow(this)

        tableRow.addView(originalPriceProductNameText)
        tableRow.addView(originalPriceProductPriceText)

        summaryTable.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

        // Put cheaper product data in table.
        val lowerPriceProductNameText = TextView(this)
        lowerPriceProductNameText.text = element?.lowerPriceProductName
        lowerPriceProductNameText.layoutParams = params
        lowerPriceProductNameText.setTextColor(getColor(android.R.color.holo_green_dark))

        val lowerPriceProductPriceText = TextView(this)
        lowerPriceProductPriceText.text = "${element?.lowerPrice.toString()} ${getString(R.string.currency)}"
        lowerPriceProductPriceText.layoutParams = params
        lowerPriceProductPriceText.setTextColor(getColor(android.R.color.holo_green_dark))

        // Summary row has the same font size.
        lowerPriceProductPriceText.textSize = 18F
        lowerPriceProductNameText.textSize = 18F

        lowerPriceProductPriceText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        lowerPriceProductNameText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

        val tableRow2 = TableRow(this)

        tableRow2.addView(lowerPriceProductNameText)
        tableRow2.addView(lowerPriceProductPriceText)

        summaryTable.addView(tableRow2, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
    }

    override fun onBackPressed() {
        // Go back to summary page.
        super.onBackPressed()
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("categoryName", GetCategoryById(this, element?.categoryId).execute().get()?.name)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == R.id.editButton) {
            createEditElementDialog()
        }

        if (id == R.id.deleteButton) {
            createDeleteElementDialog()
        }

        if (id == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createEditElementDialog()
    {
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)

        val container1 = FrameLayout(this)

        val originalPriceProductNameText = EditText(this)
        originalPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        originalPriceProductNameText.hint = getString(R.string.original_product_name)
        originalPriceProductNameText.setSingleLine()
        originalPriceProductNameText.setText(element?.originalPriceProductName)

        originalPriceProductNameText.layoutParams = params
        container1.addView(originalPriceProductNameText);

        val container2 = FrameLayout(this)

        val originalPriceProductPriceText = EditText(this)
        originalPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        originalPriceProductPriceText.hint = getString(R.string.original_product_price)
        originalPriceProductPriceText.setSingleLine()
        originalPriceProductPriceText.setText(element?.originalPrice.toString())

        originalPriceProductPriceText.layoutParams = params
        container2.addView(originalPriceProductPriceText);

        val container3 = FrameLayout(this)

        val lowerPriceProductNameText = EditText(this)
        lowerPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        lowerPriceProductNameText.hint = getString(R.string.cheaper_product_name)
        lowerPriceProductNameText.setSingleLine()
        lowerPriceProductNameText.setText(element?.lowerPriceProductName)

        lowerPriceProductNameText.layoutParams = params
        container3.addView(lowerPriceProductNameText);

        val container4 = FrameLayout(this)

        val lowerPriceProductPriceText = EditText(this)
        lowerPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        lowerPriceProductPriceText.hint =  getString(R.string.cheaper_product_price)
        lowerPriceProductPriceText.setSingleLine()
        lowerPriceProductPriceText.setText(element?.lowerPrice.toString())

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
                .setTitle(R.string.edit_element)
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

            var isOneFieldEmpty = lowerPriceProductName.isNullOrEmpty() ||
                    originalPriceProductName.isNullOrEmpty() ||
                    lowerPriceProductPrice.isNullOrEmpty() ||
                    originalPriceProductPrice.isNullOrEmpty()

            if (isOneFieldEmpty) {
                if (lowerPriceProductName.isNullOrEmpty()) {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_mandatory)
                }

                if (originalPriceProductName.isNullOrEmpty()) {
                    originalPriceProductNameText?.error = getString(R.string.name_is_mandatory)
                }

                if (lowerPriceProductPrice.isNullOrEmpty()) {
                    lowerPriceProductPriceText?.error = getString(R.string.price_is_mandatory)
                }

                if (GetAllElements(this).execute().get()
                                .any { it?.lowerPriceProductName == lowerPriceProductName  && it?.id != element?.id }
                ) {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                if (originalPriceProductPrice.isNullOrEmpty()) {
                    originalPriceProductPriceText?.error = getString(R.string.price_is_mandatory)
                }

                if (GetAllElements(this).execute().get()
                                .any { it?.originalPriceProductName == originalPriceProductName  && it?.id != element?.id}
                ) {
                    originalPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }
            }
            else {
                UpdateElement(
                        this,
                        lowerPriceProductPrice.toInt(),
                        originalPriceProductPrice.toInt(),
                        lowerPriceProductName,
                        originalPriceProductName,
                        element?.id
                ).execute()

                // Just change the title, lower price product name is the main reference of an element.
                supportActionBar?.title = lowerPriceProductName

                // Show changed data.
                getElementAndShowInTable(lowerPriceProductName)

                dialog.dismiss()
            }
        }
    }

    private fun createDeleteElementDialog()
    {
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.delete_element)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(
                        android.R.string.ok, null
                )
                .setNegativeButton(android.R.string.cancel, null)

        val dialog =builder.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val affectedRows = DeleteElement(this, element?.id).execute().get()

            if (affectedRows > 0)
            {
                dialog.dismiss()

                // Go back to summary page.
                val intent = Intent(this@ElementDetailsActivity, MainActivity::class.java)
                intent.putExtra("categoryName", GetCategoryNameById(this, element?.categoryId).execute().get())
                startActivity(intent)
            }
        }
    }
}