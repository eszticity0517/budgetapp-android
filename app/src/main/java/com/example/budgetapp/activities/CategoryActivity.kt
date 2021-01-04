package com.example.budgetapp.activities

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.CategoriesFragment
import com.example.budgetapp.MainActivity
import com.example.budgetapp.NoCategoriesFragment
import com.example.budgetapp.R
import com.example.budgetapp.fragment.ElementsFragment
import com.example.budgetapp.fragment.NoElementsFragment
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Category
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

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            createNewElementDialog()
        }

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

                        // The amount of saved money.
                        elementsAndValues[element?.lowerPriceProductName!!] = element.originalPrice!!.minus(element!!.lowerPrice!!)
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

    private fun createNewElementDialog()
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
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
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
            val text = lowerPriceProductNameText.text.toString()
            dialog.dismiss()

//            when {
//                text.isNullOrEmpty() -> {
//                    input?.error = getString(R.string.name_is_mandatory)
//                }
//                CategoryActivity.GetAllElements(this).execute().get().any { it?.name == text } -> {
//                    input?.error = getString(R.string.name_is_reserved)
//                }
//                else -> {
//                    CategoryActivity.SaveElement(this, 1, 1, "lel", "lelt").execute()
//                    dialog.dismiss()
//                }
//            }
        }
    }

    /**
     * Gets all existing elements from the database.
     */
    class GetAllElements(mContext: Context): AsyncTask<String, Long, List<Element?>>() {
        private var context: Context = mContext

        override fun doInBackground(json: Array<String?>?): List<Element?>? {
            var elements: List<Element?>?

            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                elements = budgetAppDatabase.ElementDao().getAll()
                budgetAppDatabase.close()
                elements
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to get elements.", e)
                null
            }
        }
    }
    /**
     * Inserts a very new element in the database.
     */
    class SaveElement(mContext: Context, lowerPrice: Int, originalPrice: Int, lowerPriceProductName: String, originalPriceProductName: String): AsyncTask<String, Long, Element?>() {
        private var context: Context = mContext
        private var lowerPriceProductName = lowerPriceProductName
        private var originalPriceProductName = originalPriceProductName

        private var lowerPrice = lowerPrice
        private var originalPrice = originalPrice

        override fun doInBackground(json: Array<String?>?): Element? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                var element = Element(lowerPrice = lowerPrice, originalPrice = originalPrice, lowerPriceProductName = lowerPriceProductName, originalPriceProductName = originalPriceProductName)
                budgetAppDatabase.ElementDao().save(element)
                budgetAppDatabase.close()
                element
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to save element.", e)
                null
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