package com.example.contactbook

import Contact
import ContactStorage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.app.Application
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = ContactStorage(application)
    var contacts = mutableStateListOf<Contact>()
        private set

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

@Composable
fun ContactApp(contactViewModel: ContactViewModel) {
    val contacts = contactViewModel.contacts
    Column {
        ContactList(contacts, contactViewModel)
        AddContactForm(onAddContact = { name, phone ->
            contactViewModel.addContact(name, phone)
        })
    }
}

@Composable
fun ContactList(contacts: List<Contact>, viewModel: ContactViewModel) {
    LazyColumn {
        items(contacts) { contact ->
            ContactItem(contact, viewModel)
        }
    }
}

@Composable
fun ContactItem(contact: Contact, viewModel: ContactViewModel) {
    var isEditing by remember { mutableStateOf(false) }

    Column {
        if (isEditing) {
            // Name TextField
            TextField(
                value = contact.name,
                onValueChange = { contact.name = it },
                label = { Text("Name") }
            )
            // Phone TextField
            TextField(
                value = contact.phone,
                onValueChange = { contact.phone = it },
                label = { Text("Phone") }
            )
            Button(onClick = {
                viewModel.updateContact(contact, contact.name, contact.phone)
                isEditing = false
            }) {
                Text("Save")
            }
        } else {
            Text("Name: ${contact.name}")
            Text("Phone: ${contact.phone}")
            // Buttons in a Row
            Row {
                Button(onClick = { isEditing = true }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp)) // Space between buttons
                Button(onClick = { viewModel.deleteContact(contact) }) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AddContactForm(onAddContact: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    Column {
        Text("Name")
        TextField(value = name, onValueChange = { name = it }, label={Text("Enter Name")})

        Spacer(modifier = Modifier.height(8.dp))

        Text("Phone")
        TextField(value = phone, onValueChange = { phone = it }, label={Text("Enter Phone Number")})

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            onAddContact(name, phone)
            // Clear the fields after adding contact
            name = ""
            phone = ""
        }) {
            Text("Add Contact")
        }
    }
}

class MainActivity : ComponentActivity() {
    private val contactViewModel: ContactViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactApp(contactViewModel)
        }

    }
}

