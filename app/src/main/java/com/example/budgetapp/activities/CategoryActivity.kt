package com.example.budgetapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.fragment.ElementsFragment
import com.example.budgetapp.fragment.NoElementsFragment
import com.example.budgetapp.persistence.entities.Category
import com.example.budgetapp.persistence.tools.*
import com.example.budgetapp.persistence.tools.roomtasks.*
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CategoryActivity : AppCompatActivity() {

    private var category : Category? = null

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
            getAllElementsAndShowFragment(categoryName)
        }
    }

    private fun getAllElementsAndShowFragment(categoryName: String?)
    {
        category = GetCategoryById(this, GetCategoryIdByName(this, categoryName).execute().get()).execute().get()

        if (!category?.id.toString().isNullOrEmpty())
        {
            var elements = GetAllElementsByCategory(
                categoryId = category?.id,
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
                    var elements = GetAllElementsByCategory(
                        categoryId = category?.id,
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == R.id.editButton) {
            createEditCategoryDialog()
        }

        if (id == R.id.deleteButton) {
            createDeleteCategoryDialog()
        }

        if (id == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


    private fun createDeleteCategoryDialog()
    {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.delete_category)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(
                android.R.string.ok, null
            )
            .setNegativeButton(android.R.string.cancel, null)

        val dialog =builder.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val affectedRows = DeleteCategory(this, category?.id).execute().get()

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

    private fun createEditCategoryDialog()
    {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setSingleLine()

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        input.layoutParams = params
        input.setText(category?.name)
        container.addView(input);

        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.rename_category)
            .setView(container)
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
                GetAllCategories(this).execute().get().any { it?.name == text && it?.id != category?.id} -> {
                    input?.error = getString(R.string.name_is_reserved)
                }
                else -> {
                    UpdateCategoryName(this, text, category?.id).execute()

                    // Just change the title.
                    supportActionBar?.title = text

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
        originalPriceProductNameText.hint = getString(R.string.original_product_name)
        originalPriceProductNameText.setSingleLine()

        originalPriceProductNameText.layoutParams = params
        container1.addView(originalPriceProductNameText);

        val container2 = FrameLayout(this)

        val originalPriceProductPriceText = EditText(this)
        originalPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        originalPriceProductPriceText.hint =  getString(R.string.original_product_price)
        originalPriceProductPriceText.setSingleLine()

        originalPriceProductPriceText.layoutParams = params
        container2.addView(originalPriceProductPriceText);

        val container3 = FrameLayout(this)

        val lowerPriceProductNameText = EditText(this)
        lowerPriceProductNameText.inputType = InputType.TYPE_CLASS_TEXT
        lowerPriceProductNameText.hint =  getString(R.string.cheaper_product_name)
        lowerPriceProductNameText.setSingleLine()

        lowerPriceProductNameText.layoutParams = params
        container3.addView(lowerPriceProductNameText);

        val container4 = FrameLayout(this)

        val lowerPriceProductPriceText = EditText(this)
        lowerPriceProductPriceText.inputType = InputType.TYPE_CLASS_NUMBER
        lowerPriceProductPriceText.hint = getString(R.string.cheaper_product_price)
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

        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.new_element)
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
                        .any { it?.lowerPriceProductName == lowerPriceProductName }
                ) {
                    lowerPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }

                if (originalPriceProductPrice.isNullOrEmpty()) {
                    originalPriceProductPriceText?.error = getString(R.string.price_is_mandatory)
                }

                if (GetAllElements(this).execute().get()
                        .any { it?.originalPriceProductName == originalPriceProductName }
                ) {
                    originalPriceProductNameText?.error = getString(R.string.name_is_reserved)
                }
            }
            else {
                SaveElement(this, lowerPriceProductPrice.toInt(), originalPriceProductPrice.toInt(), lowerPriceProductName, originalPriceProductName, this.category?.id).execute()

                // Showing changes.
                getAllElementsAndShowFragment(category?.name)

                dialog.dismiss()
            }
        }
    }

    override fun onBackPressed() {
        // Go back to summary page.
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}