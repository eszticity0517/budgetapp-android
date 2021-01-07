package com.example.budgetapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.budgetapp.activities.CategoryActivity


/**
 * A simple [Fragment] subclass for showing existing categories.
 */
class CategoriesFragment : Fragment() {

    private var categories : HashMap<String, Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bundle = arguments
        categories  = bundle!!.getSerializable("categories") as HashMap<String, Int>

        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Calculating saved money amount.
        var summaryValueCalculated = 0

        categories?.forEach { category ->
            summaryValueCalculated += category.value
        }

        val summaryValue: TextView = view.findViewById(R.id.summaryValue) as TextView;

        // Showing all the saved money.
        summaryValue.text = "$summaryValueCalculated HUF"

        val summaryTable : TableLayout = view.findViewById(R.id.summaryTable) as TableLayout;

        // Fill table programmatically with category and price data.

        categories?.forEach { category ->
            // Giving TextView-s the parent's layout params.
            val params = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            val categoryNameText = TextView(this.activity)
            categoryNameText.text = category.key
            categoryNameText.layoutParams = params

            val categoryValueText = TextView(this.activity)
            categoryValueText.text = category.value.toString()
            categoryNameText.layoutParams = params

            // Summary row has the same font size.
            categoryNameText.textSize = 18F
            categoryValueText.textSize = 18F

            val paddingValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5F,
                resources.displayMetrics
            ).toInt()

            categoryNameText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
            categoryValueText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

            val tableRow = TableRow(this.activity)

            tableRow.addView(categoryNameText)
            tableRow.addView(categoryValueText)

            // Add click listener to the row.
            tableRow.setOnClickListener{
                val intent = Intent(this.activity, CategoryActivity::class.java)
                intent?.putExtra("categoryName", category?.key)

                startActivity(intent)
                this.activity?.finish()
            }

            summaryTable.addView(
                tableRow, TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }
}