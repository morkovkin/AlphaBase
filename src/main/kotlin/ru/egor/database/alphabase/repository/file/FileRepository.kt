package ru.egor.database.alphabase.repository.file

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ru.egor.database.alphabase.data.Company
import ru.egor.database.alphabase.data.Contact
import ru.egor.database.alphabase.data.Contract
import ru.egor.database.alphabase.data.raw.RawCompany
import ru.egor.database.alphabase.data.raw.RawContact
import ru.egor.database.alphabase.data.raw.RawContract
import ru.egor.database.alphabase.repository.ICompanyRepository
import ru.egor.database.alphabase.repository.IContactRepository
import ru.egor.database.alphabase.repository.IContractRepository
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class FileRepository : IContactRepository, ICompanyRepository, IContractRepository {

    private val gson = Gson()
    private val contactFile = File("data/contacts.json")
    private val contactType: Type = object : TypeToken<List<RawContact>>() {}.type
    private val companyFile = File("data/company.json")
    private val companyType: Type = object : TypeToken<List<RawCompany>>() {}.type
    private val contractFile = File("data/contract.json")
    private val contractContact: Type = object : TypeToken<List<RawContract>>() {}.type

    private var companyList: ObservableList<Company>
    private var contractList: ObservableList<Contract>
    private var contactList: ObservableList<Contact>

    constructor() {
        contactList = FXCollections.observableList(getRawContacts().map { Contact.create(it) })
        companyList = FXCollections.observableList(getRawCompany().map(::map))
        contractList = FXCollections.observableList(getRawContracts().map(::map))

    }

    private fun getRawContracts(): List<RawContract> {
        return gson.fromJson(contractFile.readText(), contractContact)
    }

    private fun addRawContract(rawContract: RawContract) {
        val result = getRawContracts().toMutableList().apply {
            add(rawContract)
        }
        contractFile.writeText(gson.toJson(result))
    }

    private fun getRawContacts(): List<RawContact> {
        return gson.fromJson(contactFile.readText(), contactType)
    }

    private fun addContact(rawContact: RawContact) {
        val result = getRawContacts().toMutableList().apply {
            add(rawContact)
        }
        contactFile.writeText(gson.toJson(result))
    }

    override fun getAllContacts(): ObservableList<Contact> {
        return contactList
    }

    override fun getContact(id: Int): Contact? {
        return getAllContacts().find { it.id == id }
    }

    override fun addContact(phone: String, name: String, email: String): Contact {
        val lastId = getRawContacts().maxByOrNull { it.id }!!.id
        val nexId = lastId + 1
        val newContact = RawContact(nexId, phone, name, email)
        addContact(newContact)
        contactList.add(Contact.create(newContact))
        return contactList.find { it.id == nexId }!!
    }

    private fun getRawCompany(): List<RawCompany> {
        return gson.fromJson(companyFile.readText(), companyType)
    }

    private fun addRawCompany(rawCompany: RawCompany) {
        val result = getRawCompany().toMutableList().apply { add(rawCompany) }
        companyFile.writeText(gson.toJson(result))
    }

    private fun updateRawCompany(newRaw: RawCompany) {
        val result = getRawCompany().map {
            if (it.id == newRaw.id)
                newRaw
            else it
        }
        companyFile.writeText(gson.toJson(result))
    }

    private fun map(src: RawCompany): Company {
        val allContacts = getRawContacts()
        val contact = allContacts.find { it.id == src.fkOwner }
        return Company.create(src, contact)
    }

    private fun map(src: RawContract): Contract {
        val company = getCompany(src.company_id)!!
        val contact = getContact(src.contact_id)!!
        return Contract(src.id, Date(src.data_order), Date(src.date_ext), src.summa, company, contact, src.number)
    }

    override fun getAllCompany(): ObservableList<Company> {
        return companyList
    }

    override fun getCompany(id: Int): Company? {
        return getAllCompany().find { it.id == id }
    }

    override fun addCompany(inn: Long, ifns: String, name: String, owner: Contact): Company {
        val nextId = getRawCompany().maxByOrNull { it.id }!!.id + 1
        val newCompany = RawCompany(nextId, inn, ifns, name, owner.id)
        addRawCompany(newCompany)
        val newLocalCompany = map(newCompany)
        companyList.add(newLocalCompany)
        return newLocalCompany
    }

    override fun updateCompany(id: Int, inn: Long, ifns: String, name: String, owner: Contact?) {
        val newCompany = RawCompany(id, inn, ifns, name, owner?.id)
        updateRawCompany(newCompany)
    }

    override fun addContract(
        dataOrder: Long,
        dateExt: Long,
        summa: Double,
        manager: Contact,
        company: Company,
        contractNumber: String
    ): Contract {
        val nextId = (getRawContracts().maxByOrNull { it.id }?.id ?: 0) + 1
        val rawContract =
            RawContract(nextId, dataOrder, dateExt, summa, company.id, manager.id, contractNumber)
        addRawContract(rawContract)
        contractList.add(map(rawContract))
        return getAllContracts().find { it.id == nextId }!!
    }

    override fun getAllContracts(): ObservableList<Contract> {
        return contractList
    }

    override fun getContract(contractId: Int): Contract? {
        return getAllContracts().find { it.id == contractId }
    }

}
