package `in`.bhragu.classassignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a contact in the Room database.
 * Contains id (auto-generated primary key), name, and phone number.
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phoneNumber: String
)
