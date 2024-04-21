import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel



class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = ContactStorage(application)
    private var contacts = mutableStateListOf<Contact>()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        contacts.addAll(storage.getContacts())
    }

    fun addContact(name: String, phone: String) {
        val newContact = Contact(contacts.size + 1, name, phone)
        contacts.add(newContact)
        saveContacts()
    }

    fun updateContact(contact: Contact, name: String, phone: String) {
        contact.name = name
        contact.phone = phone
        saveContacts()
    }

    fun deleteContact(contact: Contact) {
        contacts.remove(contact)
        saveContacts()
    }

    private fun saveContacts() {
        storage.saveContacts(contacts)
    }
}