package com.example.budgetapp.fragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        // Put original and cheaper product data in a table.

        val summaryTable : TableLayout = view.findViewById(R.id.summaryTable) as TableLayout;

        // Add summary text and value first to see the saved money amount.

        val paddingValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5F,
                resources.displayMetrics
        ).toInt()

        // Giving TextView-s the parent's layout params.
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

        val summaryText = TextView(this.activity)
        summaryText.text = "${getString(R.string.in_all)}"
        summaryText.layoutParams = params
        summaryText.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.holo_purple))
        summaryText.setTypeface(null, Typeface.BOLD);

        val summaryValueText = TextView(this.activity)
        summaryValueText.text = "$summaryValueCalculated ${getString(R.string.currency)}"
        summaryValueText.layoutParams = params
        summaryValueText.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.holo_purple))
        summaryValueText.setTypeface(null, Typeface.BOLD);

        // Summary row has the same font size.
        summaryText.textSize = 18F
        summaryValueText.textSize = 18F

        summaryText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        summaryValueText.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

        val summaryTableRow = TableRow(this.activity)

        summaryTableRow.addView(summaryText)
        summaryTableRow.addView(summaryValueText)

        summaryTable.addView(summaryTableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

        // Fill table programmatically with category and price data.

        elements?.forEach { element ->
            // Giving TextView-s the parent's layout params.
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

            val categoryNameText = TextView(this.activity)
            categoryNameText.text = element.key
            categoryNameText.layoutParams = params

            val categoryValueText = TextView(this.activity)
            categoryValueText.text = "${element.value} ${getString(R.string.currency)}"
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