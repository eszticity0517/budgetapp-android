package com.example.budgetapp.persistence.entities
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "categories")
data class Category (
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @Expose
    val id: Long? = null,

    @Expose
    @NonNull
    val name: String? = null
)