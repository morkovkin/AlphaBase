package ru.egor.database.alphabase.data.raw


data class RawContract(
    val id: Int,
    val data_order: Long,
    val date_ext: Long,
    val summa: Double,
    val company_id: Int,
    val contact_id: Int,
    val number: String,
)
