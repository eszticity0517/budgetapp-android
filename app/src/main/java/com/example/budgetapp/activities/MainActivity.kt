package com.example.budgetapp

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.persistence.tools.roomtasks.GetAllCategories
import com.example.budgetapp.persistence.tools.roomtasks.GetAllElementsByCategory
import com.example.budgetapp.persistence.tools.roomtasks.SaveCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
           createNewCategoryDialog()
        }

        if (savedInstanceState == null) {
            getCategoriesAndShowFragment()
        }
    }

    private fun getCategoriesAndShowFragment()
    {
        val categories = GetAllCategories(this).execute().get()

        if(categories?.size == 0 || categories == null)
        {
            // If no category, then show a message for the user.
            val noCategoryFragment = NoCategoriesFragment()
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

                var savedAmount = 0

                elements?.forEach { element ->
                    savedAmount += element?.originalPrice!!.minus(element?.lowerPrice!!)
                }

                categoriesAndValues[category?.name!!] = savedAmount
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

    private fun createNewCategoryDialog()
    {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setSingleLine()

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        input.layoutParams = params
        container.addView(input);

        val builder = AlertDialog.Builder(this)
                .setTitle("New category")
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
                GetAllCategories(this).execute().get().any { it?.name == text } -> {
                    input?.error = getString(R.string.name_is_reserved)
                }
                else -> {
                    SaveCategory(this, text).execute()
                    // Showing changes.
                    getCategoriesAndShowFragment()
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onBackPressed() {
        // Do nothing here, just serves to disable back navigation.
    }
}