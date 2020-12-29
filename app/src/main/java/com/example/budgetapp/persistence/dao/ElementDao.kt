package com.example.budgetapp.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.budgetapp.persistence.entities.Element

@Dao
interface ElementDao {
    @Insert(onConflict = ABORT)
    fun save(element: Element?): Long

    @Query("SELECT * FROM elements WHERE name = :name")
    fun findByName(name: String?): Element?

    @Query("SELECT * FROM elements WHERE id = :id")
    fun findById(id: Long?): Element?

    @Query("SELECT * FROM elements WHERE categoryId = :id")
    fun findByIdCategoryId(id: Long?): Element?

    @Query("DELETE FROM elements WHERE id=:id")
    fun deleteById(id: Long?): Int

    @Query("DELETE FROM elements WHERE categoryId=:categoryId")
    fun deleteAllByCategoryId(categoryId: Long?): Int
}
