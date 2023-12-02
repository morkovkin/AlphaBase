package ru.egor.database.alphabase

import com.google.inject.Inject
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import javafx.util.Callback
import ru.egor.database.alphabase.data.Contract
import ru.egor.database.alphabase.repository.IContractRepository
import ru.egor.database.alphabase.util.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainScreenController {

    @FXML
    private lateinit var contractsTableView: TableView<Contract>

    @Inject
    lateinit var contractRepository: IContractRepository

    @FXML
    private lateinit var menu: MenuBar
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    @FXML
    fun initialize() {
        initForm()
    }

    private fun initForm() {
        initContractsColumns()
        contractsTableView.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            if (it.clickCount > 1) {

                val stage = Stage()
                val fxmlLoader = FXMLLoader(GenerateDocxController::class.java.getResource("create_report_form.fxml"))
                fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
                    AlphaDbApplication.injector.getInstance(
                        instantiatedClass
                    )
                }
                val scene = Scene(fxmlLoader.load())
                stage.title = "Формирование пакета документов"
                scene.userData = contractsTableView.selectionModel.selectedItem.id
                stage.scene = scene
                stage.show()
            }
        }
        menu.menus.map { it.items }.flatten().forEach { item ->
            item.setOnAction {
                onMenuClick(item)
            }
        }

        contractRepository.getAllContracts().addListener(ListChangeListener {
            while (it.next()) {
                if (it.wasAdded()) {
                    refreshList()
                }
            }
        })
        refreshList()
    }

    private fun onMenuClick(menuItem: MenuItem) {
        println("click $menuItem")
        when (menuItem.id) {
            "menuReport" -> {
                val companyId = contractsTableView.selectionModel.selectedItem?.company?.id
                if (companyId == null) {
                    Toast.makeText("Пожалуйста выберете контракт")
                    return
                }
                val stage = Stage()
                val fxmlLoader = FXMLLoader(ContractController::class.java.getResource("report_company.fxml"))
                fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
                    AlphaDbApplication.injector.getInstance(
                        instantiatedClass
                    )
                }
                val scene = Scene(fxmlLoader.load())
                stage.title = "Отчет ${contractsTableView.selectionModel.selectedItem.company.name}"
                scene.userData = contractsTableView.selectionModel.selectedItem.company.id
                stage.scene = scene
                stage.show()
            }

            "menuNewContract" -> {
                val stage = Stage()
                val fxmlLoader = FXMLLoader(ContractController::class.java.getResource("contract_form.fxml"))
                fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
                    AlphaDbApplication.injector.getInstance(
                        instantiatedClass
                    )
                }
                val scene = Scene(fxmlLoader.load())
                stage.title = "Новый контракт"
                stage.scene = scene
                stage.show()
            }

            "menuAbout" -> {
                val stage = Stage()
                val fxmlLoader = FXMLLoader(AboutController::class.java.getResource("about_form.fxml"))
                val scene = Scene(fxmlLoader.load())
                stage.title = "About"
                stage.scene = scene
                stage.show()
            }
        }
    }

    private fun initContractsColumns() {
        val dateOrder = TableColumn<Contract, Date>("Дата заключения").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.dataOrder)
            }
            setCellFactory {
                object : TableCell<Contract, Date>() {
                    override fun updateItem(item: Date?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (empty || item == null) {
                            null
                        } else {
                            dateFormat.format(item)
                        }
                    }
                }
            }
        }
        val dateExt = TableColumn<Contract, Date>("Дата окончания").apply {
            setCellValueFactory { contact -> ReadOnlyObjectWrapper(contact.value.dateExt) }

            setCellFactory {
                object : TableCell<Contract, Date>() {


                    override fun updateItem(item: Date?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (empty || item == null) {
                            null
                        } else {
                            dateFormat.format(item)
                        }
                    }
                }
            }
        }

        val companyName = TableColumn<Contract, String>("Компания").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.company.name)
            }
        }

        val number = TableColumn<Contract, String>("Номер контракта").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.number)
            }
        }
        val price = TableColumn<Contract, Double>("Цена").apply {
            setCellValueFactory { p ->
                ReadOnlyObjectWrapper(p.value.summa)
            }
        }
        val ownerTableColumn = TableColumn<Contract, String>("Владелец").apply {
            val ownerName = TableColumn<Contract, String>("Имя").apply {
                setCellValueFactory { p ->
                    ReadOnlyObjectWrapper(p.value.company.owner?.name)
                }
            }

            val ownerPhone = TableColumn<Contract, String>("Телефон").apply {
                setCellValueFactory { p ->
                    ReadOnlyObjectWrapper(p.value.company.owner?.phone)
                }
            }
            columns.addAll(ownerName, ownerPhone)
        }

        val contact = TableColumn<Contract, String>("Менеджер").apply {
            val managerName = TableColumn<Contract, String>("Имя").apply {
                setCellValueFactory { p ->
                    ReadOnlyObjectWrapper(p.value.manager.name)
                }
            }

            val managerPhone = TableColumn<Contract, String>("Телефон").apply {
                setCellValueFactory { p ->
                    ReadOnlyObjectWrapper(p.value.manager.phone)
                }
            }
            val managerEmail = TableColumn<Contract, String>("Почта").apply {
                setCellValueFactory { p ->
                    ReadOnlyObjectWrapper(p.value.manager.email)
                }
            }
            columns.addAll(managerName, managerPhone, managerEmail)
        }
        contractsTableView.columns.addAll(
            number,
            dateOrder,
            dateExt,
            companyName,
            price,
            contact,
            ownerTableColumn
        )
    }

    private fun refreshList() {
        contractsTableView.items.clear()
        val items = contractRepository.getAllContracts().sortedBy { it.dateExt }
        contractsTableView.items.addAll(items)
    }

}
