package com.example.budgetapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CategoriesFragment : Fragment() {

    private val categories : HashMap<String, Int>? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val bundle = arguments
        val categories  = bundle!!.getSerializable("categories") as HashMap<String, Int>
        print(categories?.size)

        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val summaryValue: TextView = view.findViewById(R.id.summaryValue) as TextView;

        var summaryValueCalculated = 0

        if (categories != null) {
            for(category in categories) {
                summaryValueCalculated += category.value
            }
        }

        summaryValue.text = "$summaryValueCalculated HUF"
    }
}