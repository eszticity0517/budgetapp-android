package com.example.budgetapp.persistence.entities
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "elements", indices = [Index(value = ["lowerPriceProductName", "originalPriceProductName"], unique = true)])
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
    var categoryId: Long? = null,

    /**
     * Name of the lower price product.
     */
    @Expose
    @NonNull
    var lowerPriceProductName: String? = null,

    /**
     * Name of the original price product.
     */
    @Expose
    @NonNull
    var originalPriceProductName: String? = null,

    /**
     * Cheaper price for the desired product.
     */
    @Expose
    @NonNull
    var lowerPrice: Int? = null,

    /**
     * Original (higher) price for the desired product.
     * */
    @Expose
    @NonNull
    var originalPrice: Int? = null
)