package com.example.budgetapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.budgetapp.R
import com.example.budgetapp.activities.ElementDetailsActivity

/**
 * A simple [Fragment] subclass for showing existing elements for a given category.
 */
class ElementsFragment : Fragment() {
    private var elements : HashMap<String, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        elements  = bundle!!.getSerializable("elements") as HashMap<String, Int>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Calculating saved money amount.
        var summaryValueCalculated = 0

        elements?.forEach { element ->
            summaryValueCalculated += element.value
        }

        val summaryValue: TextView = view.findViewById(R.id.summaryValue) as TextView;

        // Showing all the saved money.
        summaryValue.text = "$summaryValueCalculated ${R.string.currency}"

        val summaryTable : TableLayout = view.findViewById(R.id.summaryTable) as TableLayout;

        // Fill table programmatically with category and price data.

        elements?.forEach { element ->
            // Giving TextView-s the parent's layout params.
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

            val categoryNameText = TextView(this.activity)
            categoryNameText.text = element.key
            categoryNameText.layoutParams = params

            val categoryValueText = TextView(this.activity)
            categoryValueText.text = element.value.toString()
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
                val intent = Intent(this.activity, ElementDetailsActivity::class.java)
                intent?.putExtra("elementName", element?.key)

                startActivity(intent)
            }

            summaryTable.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
        }
    }
}