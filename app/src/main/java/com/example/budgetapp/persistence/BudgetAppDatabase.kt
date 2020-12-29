package com.example.budgetapp.persistence
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetapp.persistence.dao.CategoryDao
import com.example.budgetapp.persistence.dao.ElementDao
import com.example.budgetapp.persistence.entities.Category
import com.example.budgetapp.persistence.entities.Element

@Database(entities = [Category::class, Element::class], version = 1)
abstract class BudgetAppDatabase: RoomDatabase() {
        abstract fun CategoryDao(): CategoryDao
        abstract fun ElementDao(): ElementDao

        companion object {
            @Volatile
            private var instance: BudgetAppDatabase? = null
            private val LOCK = Any()

            operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also { instance = it }
            }

            private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context,
                BudgetAppDatabase::class.java, "budget-app.db"
            ).build()
        }
}