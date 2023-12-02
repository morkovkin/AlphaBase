package ru.egor.database.alphabase.data

import ru.egor.database.alphabase.data.raw.RawContact

data class Contact(val id: Int, val phone: String, val name: String, val email: String) {
    companion object {
        fun create(contract: RawContact?): Contact? {
            if (contract == null) return null
            return Contact(contract.id, contract.phone, contract.name, contract.email)
        }
    }

    override fun toString(): String {
        return name
    }
}
