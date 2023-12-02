package ru.egor.database.alphabase.di

import com.google.inject.AbstractModule
import ru.egor.database.alphabase.repository.ICompanyRepository
import ru.egor.database.alphabase.repository.IContactRepository
import ru.egor.database.alphabase.repository.IContractRepository
import ru.egor.database.alphabase.repository.file.FileRepository

class CoreModule : AbstractModule() {

    override fun configure() {
        val fileRepository = FileRepository()
        bind(IContactRepository::class.java).toInstance(fileRepository)
        bind(ICompanyRepository::class.java).toInstance(fileRepository)
        bind(IContractRepository::class.java).toInstance(fileRepository)
    }
}
