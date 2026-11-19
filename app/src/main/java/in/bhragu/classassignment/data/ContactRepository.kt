package `in`.bhragu.classassignment.data

import kotlinx.coroutines.flow.Flow

sealed interface InsertResult {
    data object Success : InsertResult
    data class LimitReached(val message: String = "Contact limit reached! You can only store up to 3 contacts.") : InsertResult
    data class Error(val message: String) : InsertResult
}

/**
 * Repository to abstract data access operations and enforce business rules (max 3 contacts).
 */
class ContactRepository(private val contactDao: ContactDao) {

    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    /**
     * Retrieves the count of contacts in the database.
     */
    suspend fun getContactCount(): Int {
        return contactDao.getContactCount()
    }

    /**
     * Checks the contact count in the database before inserting.
     * If 3 or more contacts already exist, insertion is rejected.
     */
    suspend fun insertContact(contact: Contact): InsertResult {
        return try {
            val count = contactDao.getContactCount()
            if (count >= 3) {
                InsertResult.LimitReached()
            } else {
                contactDao.insertContact(contact)
                InsertResult.Success
            }
        } catch (e: Exception) {
            InsertResult.Error(e.localizedMessage ?: "Failed to save contact.")
        }
    }

    /**
     * Deletes a contact from the database.
     */
    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }
}
