package ru.egor.database.alphabase.repository

import javafx.collections.ObservableList
import ru.egor.database.alphabase.data.Company
import ru.egor.database.alphabase.data.Contact

interface ICompanyRepository {
    fun getAllCompany(): ObservableList<Company>
    fun getCompany(id: Int): Company?
    fun addCompany(inn: Long, ifns: String, name: String, owner: Contact):Company
    fun updateCompany(id: Int, inn: Long, ifns: String, name: String, owner: Contact?)
}
