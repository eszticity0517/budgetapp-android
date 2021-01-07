package com.example.budgetapp.persistence.tools.roomtasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.budgetapp.persistence.BudgetAppDatabase
import com.example.budgetapp.persistence.entities.Category

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
 * Gets the category id by its name.
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
            Log.e("", "Error occurred while tried to get category by ID.", e)
            null
        }
    }
}
/**
 * Gets the category name by its id.
 */
class GetCategoryNameById(mContext: Context, categoryId: Long?):  AsyncTask<String, Long, String?>() {
    private var context: Context = mContext
    private var categoryId: Long? = categoryId

    override fun doInBackground(json: Array<String?>?): String? {
        var categoryName: String?

        return try {
            val budgetAppDatabase = BudgetAppDatabase(context)

            categoryName = budgetAppDatabase.CategoryDao().findById(categoryId)?.name
            budgetAppDatabase.close()
            categoryName
        } catch (e: Exception) {
            Log.e("", "Error occurred while tried to get category by name.", e)
            null
        }
    }
}

/**
 * Updates a category's name acquired by its id.
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
            Log.e("", "Error occurred while tried to update category with a new name.", e)
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
