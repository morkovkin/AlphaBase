package ru.egor.database.alphabase

import com.google.inject.Inject
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.Stage
import ru.egor.database.alphabase.repository.IContactRepository
import ru.egor.database.alphabase.util.Toast

class AddContactController {

    @Inject
    lateinit var contactRepository: IContactRepository

    @FXML
    private lateinit var contactName: TextField

    @FXML
    private lateinit var contactEmail: TextField

    @FXML
    private lateinit var contactPhone: TextField

    @FXML
    fun onAddClick() {
        if (contactName.text.isEmpty() && contactEmail.text.isEmpty() && contactPhone.text.isEmpty()) {
            Toast.makeText("Введите имя")
            return
        }
        contactRepository.addContact(contactPhone.text, contactName.text, contactEmail.text)
        val stage: Stage = contactPhone.scene.window as Stage
        stage.close()
    }
}
