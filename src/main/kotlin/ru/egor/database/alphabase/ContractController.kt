package ru.egor.database.alphabase

import com.google.inject.Inject
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.stage.Stage
import javafx.util.Callback
import org.controlsfx.control.textfield.TextFields
import ru.egor.database.alphabase.AlphaDbApplication.Companion.injector
import ru.egor.database.alphabase.data.Company
import ru.egor.database.alphabase.data.Contact
import ru.egor.database.alphabase.repository.ICompanyRepository
import ru.egor.database.alphabase.repository.IContactRepository
import ru.egor.database.alphabase.repository.IContractRepository
import ru.egor.database.alphabase.util.Toast
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*


class ContractController {

    @Inject
    lateinit var companyRepository: ICompanyRepository

    @Inject
    lateinit var contactRepository: IContactRepository

    @Inject
    lateinit var contractRepository: IContractRepository

    @FXML
    private lateinit var idCompany: TableView<Company>

    @FXML
    private lateinit var dateOrder: DatePicker

    @FXML
    private lateinit var dateExt: DatePicker

    @FXML
    private lateinit var contractSum: TextField

    @FXML
    private lateinit var etManager: TextField

    @FXML
    private lateinit var etCompanyFilter: TextField

    private val changeContactListener: ChangeContactListener = ChangeContactListener()
    private val changeCompanyListener: ChangeCompanyListener = ChangeCompanyListener()

    @FXML
    fun initialize() {
        Platform.runLater {
            initForm()
        }
    }

    private fun initForm() {
        initCompensates()
        idCompany.items = companyRepository.getAllCompany()
        companyRepository.getAllCompany().addListener(changeCompanyListener)
        contactRepository.getAllContacts().addListener(changeContactListener)
        etCompanyFilter.textProperty()
            .addListener { _: ObservableValue<out String?>?, oldValue: String?, newValue: String ->
                if (oldValue != null && newValue.length < oldValue.length) {
                    idCompany.items = companyRepository.getAllCompany()
                }
                val value = newValue.lowercase(Locale.getDefault())
                val subentries: ObservableList<Company> = FXCollections.observableArrayList()
                val count = idCompany.columns.count()
                for (i in 0 until idCompany.items.size) {
                    for (j in 0 until count) {
                        val entry = "" + idCompany.columns[j].getCellData(i)
                        if (entry.lowercase(Locale.getDefault()).contains(value)) {
                            subentries.add(idCompany.items[i])
                            break
                        }
                    }
                }
                idCompany.setItems(subentries)
            }
    }

    @FXML
    fun onAddNewCompanyClick() {
        val fxmlLoader = FXMLLoader(AddCompanyController::class.java.getResource("add_company_form.fxml"))
        fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
            injector.getInstance(
                instantiatedClass
            )
        }

        val stage = Stage()
        val scene = Scene(fxmlLoader.load(), 600.0, 300.0)
        stage.title = "Create new company"
        stage.scene = scene
        stage.show()

    }

    private fun initCompensates() {
        initCompanyTableRow()
        bindCompletion()
        dateOrder.value = LocalDate.now()
        dateExt.value = LocalDate.now().plusMonths(11)
    }

    private fun bindCompletion() {
        val contacts = contactRepository.getAllContacts().map {
            getContactAsFiltered(it)
        }
        TextFields.bindAutoCompletion(etManager, contacts)
    }

    private fun initCompanyTableRow() {
        val companyId = TableColumn<Company, String>("id").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.id.toString())
            }
        }
        val companyName = TableColumn<Company, String>("CompanyName").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.name)
            }
        }
        val innColumn = TableColumn<Company, String>("inn").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.inn.toString())
            }
        }

        val ifns = TableColumn<Company, String>("ifns").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.ifns)
            }
        }
        idCompany.columns.addAll(
            companyId,
            companyName,
            innColumn,
            ifns,
        )
    }

    private fun getContactAsFiltered(contact: Contact): String {
        return contact.name + " | " + contact.phone + " | " + contact.email
    }

    @FXML
    fun onAddContactClick() {
        val fxmlLoader = FXMLLoader(AddContactController::class.java.getResource("add_contact_form.fxml"))
        fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
            injector.getInstance(instantiatedClass)
        }
        val stage = Stage()
        val scene = Scene(fxmlLoader.load(), 400.0, 200.0)
        stage.title = "Create new contact"
        stage.scene = scene
        stage.show()
    }

    @FXML
    fun onSaveClick() {
        initCompensates()
        val company = idCompany.selectionModel.selectedItem
        if (company == null) {
            Toast.makeText("Please select company")
            return
        }
        val sumResult = runCatching { contractSum.text.toDouble() }
        if (sumResult.isFailure) {
            Toast.makeText("Wrong sum result ${contractSum.text}")
            return
        }
        val contact = contactRepository.getAllContacts().find {
            etManager.text == getContactAsFiltered(it)
        }
        if (contact == null) {
            Toast.makeText("Contact can't be empty")
            return
        }
        contractRepository.addContract(
            getMs(dateOrder), getMs(dateExt), sumResult.getOrThrow(), contact, company
        )
        contactRepository.getAllContacts().removeListener(changeContactListener)
        companyRepository.getAllCompany().removeListener(changeCompanyListener)
        val stage: Stage = dateOrder.scene.window as Stage
        stage.close()
    }

    private fun getMs(datePicker: DatePicker): Long {
        return datePicker.value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    private inner class ChangeCompanyListener : ListChangeListener<Company> {
        override fun onChanged(c: ListChangeListener.Change<out Company>) {
            while (c.next()) {
                if (c.wasAdded()) {
                    etCompanyFilter.text = c.addedSubList.first().inn.toString()
                }
            }
        }

    }

    private inner class ChangeContactListener : ListChangeListener<Contact> {
        override fun onChanged(c: ListChangeListener.Change<out Contact>) {
            while (c.next()) {
                if (c.wasAdded()) {
                    val contact = c.addedSubList.first()
                    if (contact.name.isEmpty() && contact.email.isEmpty() && contact.phone.isEmpty()) {
                        return
                    }
                    etManager.text = getContactAsFiltered(c.addedSubList.first())
                }
            }
            bindCompletion()
        }

    }

}
