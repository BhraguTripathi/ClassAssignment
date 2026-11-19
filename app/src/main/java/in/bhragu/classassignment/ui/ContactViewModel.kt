package `in`.bhragu.classassignment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import `in`.bhragu.classassignment.data.Contact
import `in`.bhragu.classassignment.data.ContactRepository
import `in`.bhragu.classassignment.data.InsertResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    // Observable contacts list from Room
    val contacts: StateFlow<List<Contact>> = repository.allContacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Form state
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    // Form errors
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError.asStateFlow()

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError.asStateFlow()

    // One-time notification / alert message (e.g., limit reached or success)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
        if (_nameError.value != null && newName.isNotBlank()) {
            _nameError.value = null
        }
    }

    fun onPhoneChange(newPhone: String) {
        _phone.value = newPhone
        if (_phoneError.value != null && newPhone.isNotBlank()) {
            _phoneError.value = null
        }
    }

    fun onAddContact() {
        val trimmedName = _name.value.trim()
        val trimmedPhone = _phone.value.trim()

        var hasError = false
        if (trimmedName.isEmpty()) {
            _nameError.value = "Name cannot be empty"
            hasError = true
        }
        if (trimmedPhone.isEmpty()) {
            _phoneError.value = "Phone number cannot be empty"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            // Requirement: Check the count before inserting. If 3 contacts already exist, do not insert and show a message that the limit is reached.
            when (val result = repository.insertContact(Contact(name = trimmedName, phoneNumber = trimmedPhone))) {
                is InsertResult.Success -> {
                    _name.value = ""
                    _phone.value = ""
                    _nameError.value = null
                    _phoneError.value = null
                    _userMessage.value = "Contact \"$trimmedName\" saved successfully!"
                }
                is InsertResult.LimitReached -> {
                    _userMessage.value = "Limit reached! Maximum 3 contacts allowed. Please delete one to add more."
                }
                is InsertResult.Error -> {
                    _userMessage.value = "Error: ${result.message}"
                }
            }
        }
    }

    fun onDeleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            _userMessage.value = "Deleted \"${contact.name}\""
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }

    companion object {
        fun provideFactory(repository: ContactRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(repository) as T
                }
            }
    }
}
