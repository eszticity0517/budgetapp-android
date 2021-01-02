package com.example.budgetapp

import android.R.attr.*
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Category
import com.example.budgetapp.persistence.entities.Element
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        actionBar?.title = "Summary";
        supportActionBar?.title = "Summary";

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
           createNewCategoryDialog()
        }

        val spinner: Spinner = findViewById(R.id.periodSpinner)

        ArrayAdapter.createFromResource(
                this,
                R.array.periods,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long
            ) {
                //Do something
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        if (savedInstanceState == null) {
            val categories = GetAllCategories(this).execute().get()

            if(categories?.size == 0 || categories == null)
            {
                // If no category, then show a message for the user.
                val noCategoryFragment = NoCategoryFragment()
                noCategoryFragment.arguments = intent.extras

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, noCategoryFragment, "noCategoryFragment")
                        .commit()
            }
            else {
                // Put categories and money amount together in a map.
                val categoriesAndValues = hashMapOf<String, Int>()

                for(category in categories)
                {
                     var elements = GetAllElementsByCategory(categoryId = category?.id, mContext = this).execute()?.get()

                    elements?.forEach { element ->
                        print(element?.originalPriceProductName)
                    }

                    categoriesAndValues[category?.name!!] = 0
                }

                val bundle = Bundle()
                bundle.putSerializable("categories", categoriesAndValues)

                val categoriesFragment = CategoriesFragment()
                categoriesFragment.arguments = bundle

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, categoriesFragment, "categoriesFragment")
                        .commit()
            }
        }
    }

    private fun createNewCategoryDialog()
    {
        // TODO: put it in a container for better margins / paddings.
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val builder = AlertDialog.Builder(this)
                .setTitle("New category")
                .setView(input)
                .setPositiveButton(
                        android.R.string.ok,
                        DialogInterface.OnClickListener { dialog, which ->
                            SaveCategory(this, "lel").execute()
                        })
                .setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }

    /**
     * Gets all existing categories from the database.
     */
    class GetAllCategories(mContext: Context): AsyncTask<String, Long, List<Category?>>() {
        private var context: Context = mContext

        override fun doInBackground(json: Array<String?>?): List<Category?>? {
            var categories: List<Category?>?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                categories = budgetAppDatabase.CategoryDao().getAll()
                budgetAppDatabase.close()
                categories
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get categories.", e)
                null
            }
        }
    }

    /**
     * Gets all existing elements related to a specific category spotted by its ID.
     */
    class GetAllElementsByCategory(mContext: Context, categoryId: Long?):  AsyncTask<String, Long, List<Element?>?>() {
        private var context: Context = mContext
        private var categoryId: Long? = categoryId

        override fun doInBackground(json: Array<String?>?): List<Element?>? {
            var elements: List<Element?>?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                elements = budgetAppDatabase.ElementDao().getAllByCategoryId(categoryId)
                budgetAppDatabase.close()
                elements
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get elements by category ID.", e)
                null
            }
        }
    }

    /**
     * Inserts a very new category in the database.
     */
    class SaveCategory(mContext: Context, categoryName: String): AsyncTask<String, Long, Category?>() {
        private var context: Context = mContext
        private var categoryName = categoryName

        override fun doInBackground(json: Array<String?>?): Category? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                var category = Category(name = categoryName)
                budgetAppDatabase.CategoryDao().save(category)
                budgetAppDatabase.close()
                category
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to save category.", e)
                null
            }
        }
    }
}