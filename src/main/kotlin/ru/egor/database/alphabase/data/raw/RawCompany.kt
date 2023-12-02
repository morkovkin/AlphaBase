package ru.egor.database.alphabase.data.raw

data class RawCompany(val id: Int, val inn: Long, val ifns: String, val name: String, val fkOwner: Int? = null)
