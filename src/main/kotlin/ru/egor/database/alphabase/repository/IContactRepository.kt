package ru.egor.database.alphabase.repository

import javafx.collections.ObservableList
import ru.egor.database.alphabase.data.Contact

interface IContactRepository {
    fun getAllContacts(): ObservableList<Contact>
    fun getContact(id: Int): Contact?
    fun addContact(phone: String, name: String, email: String): Contact
}
