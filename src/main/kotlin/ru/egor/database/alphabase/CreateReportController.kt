package ru.egor.database.alphabase

import com.google.inject.Inject
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.Stage
import ru.egor.database.alphabase.data.Company
import ru.egor.database.alphabase.repository.ICompanyRepository
import ru.egor.database.alphabase.repository.IContractRepository
import ru.egor.database.alphabase.util.Toast

class CreateReportController {

    @Inject
    lateinit var companyRepository: ICompanyRepository

    @Inject
    lateinit var contractRepository: IContractRepository

    private lateinit var company: Company

    @FXML
    private lateinit var tvContractsCount: Label

    @FXML
    private lateinit var tvTotalPayment: Label

    @FXML
    fun initialize() {
        Platform.runLater {

            val company = companyRepository.getCompany(tvContractsCount.scene.userData as Int)
            if (company == null) {
                Toast.makeText("Неверное значение company id")
                val stage: Stage = tvContractsCount.scene.window as Stage
                stage.close()
                return@runLater
            }
            this.company = company
            initForm()
        }
    }

    private fun initForm() {
        val totalContracts =
            contractRepository.getAllContracts().filter { it.company.id == company.id }
        tvContractsCount.text = totalContracts.size.toString()
        tvTotalPayment.text = "${totalContracts.sumOf { it.summa }}"
    }
}
