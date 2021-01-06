package com.example.budgetapp.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.fragment.ElementsFragment
import com.example.budgetapp.fragment.NoElementsFragment
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Category
import com.example.budgetapp.persistence.entities.Element
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CategoryActivity : AppCompatActivity() {

    private var categoryId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(findViewById(R.id.toolbar))

        // This parent activity of ElementDetailsActivity can acquire data back by having "android:launchMode="singleTask" in the config.
        val intent = intent
        val categoryName  = intent.getStringExtra("categoryName") as String

        actionBar?.title = categoryName
        supportActionBar?.title = categoryName

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            createNewElementDialog()
        }

        if (savedInstanceState == null) {
            categoryId = GetCategoryIdByName(this, categoryName).execute().get()

            if (!categoryId.toString().isNullOrEmpty())
            {
                var elements = MainActivity.GetAllElementsByCategory(
                    categoryId = categoryId,
                    mContext = this
                ).execute()?.get()

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
                        var elements = MainActivity.GetAllElementsByCategory(
                            categoryId = categoryId,
                            mContext = this
                        ).execute()?.get()

                        elements?.forEach { element ->
                            print(element?.originalPriceProductName)
                        }

                        // The amount of saved money.
                        elementsAndValues[element?.lowerPriceProductName!!] = element.originalPrice!!.minus(
                            element!!.lowerPrice!!
                        )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == R.id.editButton) {
            CreateEditCategoryDialog()
        }

        if (id == R.id.deleteButton) {
            CreateDeleteCategoryDialog()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun CreateDeleteCategoryDialog()
    {
        val builder = AlertDialog.Builder(this)
            .setTitle("Delete category")
            .setMessage("Are you sure?")
            .setPositiveButton(
                android.R.string.ok, null
            )
            .setNegativeButton(android.R.string.cancel, null)

        val dialog =builder.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val affectedRows = DeleteCategory(this, categoryId).execute().get()

            if (affectedRows > 0)
            {
                dialog.dismiss()

                // Go back to summary page.
                val intent = Intent(this@CategoryActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun CreateEditCategoryDialog()
    {
        // TODO: put it in a container for better margins / paddings.
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val builder = AlertDialog.Builder(this)
            .setTitle("Rename category")
            .setView(input)
            .setPositiveButton(
                android.R.string.ok, null
            )
            .setNegativeButton(android.R.string.cancel, null)

        val dialog =builder.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val text = input.text.toString()

            when {
                text.isNullOrEmpty() -> {
                    input?.error = getString(R.string.name_is_mandatory)
                }
                MainActivity.GetAllCategories(this).execute().get().any { it?.name == text && it?.id != categoryId} -> {
                    input?.error = getString(R.string.name_is_reserved)
                }
                else -> {
                    UpdateCategoryName(this, text, categoryId).execute()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun createNewElementDialog()
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

                GetAllElements(this).execute().get().any { it?.lowerPriceProductName == lowerPriceProductName} -> {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                GetAllElements(this).execute().get().any { it?.originalPriceProductName == originalPriceProductName} -> {
                    originalPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                else -> {
                    SaveElement(this, 1, 1, lowerPriceProductName, originalPriceProductName, this.categoryId).execute()
                    dialog.dismiss()
                }
            }
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
    class SaveElement(
        mContext: Context,
        lowerPrice: Int,
        originalPrice: Int,
        lowerPriceProductName: String,
        originalPriceProductName: String,
        categoryId: Long?
    ): AsyncTask<String, Long, Element?>() {
        private var context: Context = mContext
        private var lowerPriceProductName = lowerPriceProductName
        private var originalPriceProductName = originalPriceProductName

        private var lowerPrice = lowerPrice
        private var originalPrice = originalPrice
        private var categoryId = categoryId

        override fun doInBackground(json: Array<String?>?): Element? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                var element = Element(
                    lowerPrice = lowerPrice,
                    originalPrice = originalPrice,
                    lowerPriceProductName = lowerPriceProductName,
                    originalPriceProductName = originalPriceProductName,
                    categoryId = categoryId
                )
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

    /**
     * Inserts a very new category in the database.
     */
    class UpdateCategoryName(mContext: Context, categoryName: String, categoryId: Long?): AsyncTask<String, Long, Category?>() {
        private var context: Context = mContext
        private var categoryName = categoryName
        private var categoryId = categoryId

        override fun doInBackground(json: Array<String?>?): Category? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                var category = budgetAppDatabase.CategoryDao().findById(categoryId)
                category?.name = categoryName
                budgetAppDatabase.CategoryDao().update(category)
                budgetAppDatabase.close()
                category
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to save category.", e)
                null
            }
        }
    }

    /**
     * Deletes category from the database.
     */
    class DeleteCategory(mContext: Context, categoryId: Long?): AsyncTask<String, Long, Int>() {
        private var context: Context = mContext
        private var categoryId = categoryId

        override fun doInBackground(json: Array<String?>?): Int? {
            return try {
                val budgetAppDatabase = BudgetAppDatabase(context)

                // Deleting related elements first.
                 budgetAppDatabase.ElementDao().deleteAllByCategoryId(categoryId)

                // .. then deleting the actual category.
                val categoryDeletion = budgetAppDatabase.CategoryDao().deleteById(categoryId)
                categoryDeletion
            } catch (e: Exception) {
                Log.e("", "Error occurred while tried to delete category.", e)
                null
            }
        }
    }


}