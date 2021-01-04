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
import com.example.budgetapp.CategoriesFragment
import com.example.budgetapp.MainActivity
import com.example.budgetapp.NoCategoriesFragment
import com.example.budgetapp.R
import com.example.budgetapp.fragment.ElementsFragment
import com.example.budgetapp.fragment.NoElementsFragment
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

        if (savedInstanceState == null) {
            val categoryId = GetCategoryIdByName(this, categoryName).execute().get()

            if (!categoryId.toString().isNullOrEmpty())
            {
                var elements = MainActivity.GetAllElementsByCategory(categoryId = categoryId, mContext = this).execute()?.get()

                elements?.forEach { element ->
                    print(element?.originalPriceProductName)
                }

                if(elements?.size == 0 || elements == null)
                {
                    // If no elements for the category, then show a message for the user.
                    val noElements = NoElementsFragment()
                    noElements.arguments = intent.extras

                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, noElements, "noElementsFragment")
                            .commit()
                }
                else {
                    val elementsAndValues = hashMapOf<String, Int>()

                    for(element in elements)
                    {
                        var elements = MainActivity.GetAllElementsByCategory(categoryId = categoryId, mContext = this).execute()?.get()

                        elements?.forEach { element ->
                            print(element?.originalPriceProductName)
                        }

                        // TODO: find out how will this work.
                        //elementsAndValues[element?.name!!] = 0
                    }

                    val bundle = Bundle()
                    bundle.putSerializable("elements", elementsAndValues)

                    val elementsFragment = ElementsFragment()
                    elementsFragment.arguments = bundle

                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, elementsFragment, "elementsFragment")
                            .commit()
                }
            }
            else
            {
                // If no elements for the category, then show a message for the user.
                val noElements = NoElementsFragment()
                noElements.arguments = intent.extras

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, noElements, "noElementsFragment")
                        .commit()
            }
        }
    }

    // TODO: this has a duplicate, find a way to put everything to helper class.
    /**
     * Gets all existing elements related to a specific category spotted by its ID.
     */
    class GetCategoryIdByName(mContext: Context, categoryName: String?):  AsyncTask<String, Long, Long?>() {
        private var context: Context = mContext
        private var categoryName: String? = categoryName

        override fun doInBackground(json: Array<String?>?): Long? {
            var categoryId: Long?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                categoryId = budgetAppDatabase.CategoryDao().findByName(categoryName)?.id
                budgetAppDatabase.close()
                categoryId
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get elements by category ID.", e)
                null
            }
        }
    }


}