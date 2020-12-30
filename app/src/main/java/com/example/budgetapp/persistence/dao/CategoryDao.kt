package com.example.budgetapp.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.budgetapp.persistence.entities.Category

@Dao
interface CategoryDao {
     @Insert(onConflict = ABORT)
     fun save(category: Category?): Long

    @Query("SELECT * FROM categories WHERE name = :name")
    fun findByName(name: String?): Category?

    @Query("SELECT * FROM categories WHERE id = :id")
    fun findById(id: Long?): Category?

    @Query("DELETE FROM categories WHERE id=:id")
    fun deleteById(id: Long?): Int

    @Query("SELECT * FROM categories")
    fun getAll(): List<Category?>?
}
