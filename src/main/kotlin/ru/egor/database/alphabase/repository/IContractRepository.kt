package ru.egor.database.alphabase.repository

import javafx.collections.ObservableList
import ru.egor.database.alphabase.data.Company
import ru.egor.database.alphabase.data.Contact
import ru.egor.database.alphabase.data.Contract

interface IContractRepository {
    fun addContract(
        dataOrder: Long,
        dateExt: Long,
        summa: Double,
        manager: Contact,
        company: Company,
        contractNumber: String,
    ): Contract

    fun getAllContracts(): ObservableList<Contract>

    fun getContract(contractId: Int): Contract?
}
