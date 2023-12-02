package ru.egor.database.alphabase.data

import java.util.Date

data class Contract(
    val id: Int,
    val dataOrder: Date,
    val dateExt: Date,
    val summa: Double,
    val company: Company,
    val manager: Contact,
    val number: String
) {
    fun prettySum(): String {
        return "$summa₽"
    }
}
