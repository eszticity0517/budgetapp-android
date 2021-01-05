package com.example.budgetapp.persistence.entities
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "categories", indices = [Index(value = ["name"], unique = true)])
data class Category (
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @Expose
    val id: Long? = null,

    @Expose
    @NonNull
    var name: String? = null
)