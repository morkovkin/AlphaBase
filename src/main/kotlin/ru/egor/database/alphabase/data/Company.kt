package ru.egor.database.alphabase.data

import ru.egor.database.alphabase.data.raw.RawCompany
import ru.egor.database.alphabase.data.raw.RawContact

class Company(val id: Int, val inn: Long, val ifns: String, val name: String, val owner: Contact? = null) {
    companion object {
        fun create(rawCompany: RawCompany, owner: RawContact?): Company {
            return Company(
                rawCompany.id,
                rawCompany.inn,
                rawCompany.ifns,
                rawCompany.name,
                Contact.create(owner)
            )
        }
    }

    override fun toString(): String {
        return "$name $inn"
    }
}
