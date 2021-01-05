package com.example.budgetapp.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import androidx.room.Update
import com.example.budgetapp.persistence.entities.Category
import com.example.budgetapp.persistence.entities.Element

@Dao
interface ElementDao {
    @Insert(onConflict = ABORT)
    fun save(element: Element?): Long

    @Update
    fun update(element: Element?)

    @Query("SELECT * FROM elements WHERE lowerPriceProductName = :name")
    fun findByLowerPriceProductName(name: String?): Element?

    @Query("SELECT * FROM elements WHERE originalPriceProductName = :name")
    fun findByOriginalPriceProductName(name: String?): Element?

    @Query("SELECT * FROM elements WHERE id = :id")
    fun findById(id: Long?): Element?

    @Query("SELECT * FROM elements WHERE categoryId = :id")
    fun getAllByCategoryId(id: Long?): List<Element?>?

    @Query("DELETE FROM elements WHERE id=:id")
    fun deleteById(id: Long?): Int

    @Query("DELETE FROM elements WHERE categoryId=:categoryId")
    fun deleteAllByCategoryId(categoryId: Long?): Int

    @Query("SELECT * FROM elements")
    fun getAll(): List<Element?>?
}
