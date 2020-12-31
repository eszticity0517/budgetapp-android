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

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val bundle = arguments
        val categories  = bundle!!.getSerializable("categories") as HashMap<String, Int>
        print(categories?.size)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val summaryValue: TextView = view.findViewById(R.id.summaryValue) as TextView;
        summaryValue.text = "0 HUF"
    }
}