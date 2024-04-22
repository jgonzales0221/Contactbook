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
import androidx.compose.material3.AlertDialog
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

    fun updateContact(contact: Contact, nName: String, nPhone: String) {
        contact.name = nName
        contact.phone = nPhone
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
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(contact.name) }
    var editedPhone by remember { mutableStateOf(contact.phone) }

    Column {
        if (isEditing) {
            // Editing mode
            TextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Name") }
            )
            TextField(
                value = editedPhone,
                onValueChange = { editedPhone = it },
                label = { Text("Phone") }
            )
            Button(onClick = {
                viewModel.updateContact(contact, editedName, editedPhone)
                isEditing = false
            }) {
                Text("Save")
            }
        } else {
            // Display mode
            Text("Name: ${contact.name}")
            Text("Phone: ${contact.phone}")

            Row {
                Button(onClick = { isEditing = true }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showDeleteConfirmation = true }) {
                    Text("Delete")
                }

                // Delete confirmation dialog
                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text("Delete Contact") },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.deleteContact(contact)
                                showDeleteConfirmation = false
                            }) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteConfirmation = false }) {
                                Text("Cancel")
                            }
                        }
                    )
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

