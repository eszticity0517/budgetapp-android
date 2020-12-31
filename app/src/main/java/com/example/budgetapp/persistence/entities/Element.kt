package com.example.budgetapp.persistence.entities
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "elements")
data class Element (
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @Expose
    val id: Long? = null,

    /**
     * ID of the parent category (like food, cosmetics, etc.)
     */
    @Expose
    @NonNull
    val categoryId: Long? = null,

    /**
     * Name of the lower price product.
     */
    @Expose
    @NonNull
    val lowerPriceProductName: String? = null,

    /**
     * Name of the original price product.
     */
    @Expose
    @NonNull
    val originalPriceProductName: String? = null,

    /**
     * Cheaper price for the desired product.
     */
    @Expose
    @NonNull
    val lowerPrice: Int? = null,

    /**
     * Original (higher) price for the desired product.
     * */
    @Expose
    @NonNull
    val originalPrice: Int? = null
)