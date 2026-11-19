package `in`.bhragu.classassignment.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the contacts table.
 * Provides methods to insert, query all contacts, count contacts, and delete contacts.
 */
@Dao
interface ContactDao {

    /**
     * Inserts a contact into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    /**
     * Fetches all contacts from the database as an observable Flow.
     */
    @Query("SELECT * FROM contacts ORDER BY id ASC")
    fun getAllContacts(): Flow<List<Contact>>

    /**
     * Counts the total number of contacts currently saved in the database.
     */
    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int

    /**
     * Deletes a contact from the database.
     */
    @Delete
    suspend fun deleteContact(contact: Contact)
}
