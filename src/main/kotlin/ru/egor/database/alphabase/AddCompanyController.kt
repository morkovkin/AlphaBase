package ru.egor.database.alphabase

import com.google.inject.Inject
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.stage.Stage
import javafx.util.Callback
import ru.egor.database.alphabase.data.Contact
import ru.egor.database.alphabase.repository.ICompanyRepository
import ru.egor.database.alphabase.repository.IContactRepository
import ru.egor.database.alphabase.util.Toast


class AddCompanyController {

    @Inject
    lateinit var contactRepository: IContactRepository

    @Inject
    lateinit var companyRepository: ICompanyRepository

    @FXML
    private lateinit var ownerName: TextField

    @FXML
    private lateinit var ownerEmail: TextField

    @FXML
    private lateinit var ownerPhone: TextField

    @FXML
    private lateinit var tfInn: TextField

    @FXML
    private lateinit var tfIfns: TextField

    @FXML
    private lateinit var tfCompanyName: TextField

    private var targetId: Int? = null

    @FXML
    fun initialize() {
        Platform.runLater {
            targetId = ownerName.scene.userData as Int?
            println("oxoxox initialize $targetId")
            initForm(targetId)
        }
    }

    private fun initForm(id: Int?) {
        if (id == null) return

        val targetCompany = companyRepository.getCompany(id) ?: return
        tfInn.text = "${targetCompany.inn}"
        tfIfns.text = targetCompany.ifns
        tfCompanyName.text = targetCompany.name

        ownerPhone.text = targetCompany.owner?.phone
        ownerEmail.text = targetCompany.owner?.email
        ownerName.text = targetCompany.owner?.name
        println("contact ${targetCompany.owner}")

    }

    @FXML
    fun onSafeClick() {

        if (!validateForm()) {
            return
        }
        safeCompany()
        val stage: Stage = tfIfns.scene.window as Stage
        stage.close()
    }

    private fun safeCompany() {
        val inn = getInn()
        val ifns = tfIfns.getNotEmpty()
        val name = tfCompanyName.getNotEmpty()
        if (targetId != null) {
            val targetCompany = companyRepository.getCompany(targetId!!)
            val owner =
                targetCompany?.owner?.copy(name = ownerName.text, email = ownerEmail.text, phone = ownerPhone.text)
            companyRepository.updateCompany(targetId!!, inn.getOrThrow(), ifns.getOrThrow(), name.getOrThrow(), owner)
        } else {
            val owner = contactRepository.addContact(ownerPhone.text, ownerName.text, ownerEmail.text)
            targetId = companyRepository.addCompany(inn.getOrThrow(), ifns.getOrThrow(), name.getOrThrow(), owner).id
        }
    }

    private fun validateForm(): Boolean {
        val inn = getInn()
        if (inn.isFailure) {
            Toast.makeText("Incorrect inn " + tfInn.text)
            return false
        }

        val ifns = tfIfns.getNotEmpty()
        if (ifns.isFailure) {
            Toast.makeText("Incorrect ifns " + tfInn.text)
            return false
        }

        val name = tfCompanyName.getNotEmpty()
        if (name.isFailure) {
            Toast.makeText("Incorrect company name " + tfCompanyName.text)
            return false
        }

        return true
    }

    private fun TextField.getNotEmpty(): Result<String> {
        return if (text.isNullOrBlank()) {
            Result.failure(IllegalArgumentException("empty"))
        } else {
            Result.success(text)
        }
    }

    private fun getInn(): Result<Long> {
        return runCatching {
            tfInn.text.toLong()
        }
    }

}
