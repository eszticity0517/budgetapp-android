package com.example.budgetapp.persistence.tools.roomtasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Element

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

/**
 * Deletes category from the database.
 */
class DeleteElement(mContext: Context, elementId: Long?): AsyncTask<String, Long, Int>() {
    private var context: Context = mContext
    private var elementId = elementId

    override fun doInBackground(json: Array<String?>?): Int? {
        return try {
            val budgetAppDatabase = BudgetAppDatabase(context)

            val elementDeletion = budgetAppDatabase.ElementDao().deleteById(elementId)
            elementDeletion
        } catch (e: Exception) {
            Log.e("", "Error occurred while tried to delete category.", e)
            null
        }
    }
}

/**
 * Inserts a very new category in the database.
 */
class UpdateElement(
    mContext: Context,
    lowerPrice: Int,
    originalPrice: Int,
    lowerPriceProductName: String,
    originalPriceProductName: String,
    elementId: Long?
): AsyncTask<String, Long, Element?>() {
    private var context: Context = mContext
    private var lowerPriceProductName = lowerPriceProductName
    private var originalPriceProductName = originalPriceProductName

    private var lowerPrice = lowerPrice
    private var originalPrice = originalPrice
    private var elementId = elementId

    override fun doInBackground(json: Array<String?>?): Element? {
        return try {
            val budgetAppDatabase = BudgetAppDatabase(context)

            var element = budgetAppDatabase.ElementDao().findById(elementId)
            element?.lowerPrice = lowerPrice
            element?.lowerPriceProductName = lowerPriceProductName
            element?.originalPrice = originalPrice
            element?.originalPriceProductName = originalPriceProductName

            budgetAppDatabase.ElementDao().update(element)
            budgetAppDatabase.close()
            element
        } catch (e: Exception) {
            Log.e("", "Error occurred while tried to save category.", e)
            null
        }
    }
}


/**
 * Gets element with the provided name.
 */
class GetElementByName(mContext: Context, elementName: String): AsyncTask<String, Long, Element?>() {
    private var context: Context = mContext
    private var elementName: String = elementName

    override fun doInBackground(json: Array<String?>?): Element? {
        var element: Element?

        return try {
            val budgetAppDatabase = BudgetAppDatabase(context)

            element = budgetAppDatabase.ElementDao().findByLowerPriceProductName(elementName)
            budgetAppDatabase.close()
            element
        } catch (e: Exception) {
            Log.e("", "Error occurred while tried to get element.", e)
            null
        }
    }
}
