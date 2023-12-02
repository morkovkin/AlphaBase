package ru.egor.database.alphabase

import com.google.inject.Inject
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.Stage
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor
import ru.egor.database.alphabase.data.Contract
import ru.egor.database.alphabase.repository.IContractRepository
import ru.egor.database.alphabase.util.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.function.Consumer

class GenerateDocxController {

    @Inject
    lateinit var contractRepository: IContractRepository

    private lateinit var contract: Contract


    @FXML
    private lateinit var tvCompanyName: Label

    @FXML
    private lateinit var tvContractNumber: Label

    @FXML
    fun initialize() {
        Platform.runLater {
            val contract = contractRepository.getContract(tvCompanyName.scene.userData as Int)
            if (contract == null) {
                Toast.makeText("Неверное значение report id")
                val stage: Stage = tvCompanyName.scene.window as Stage
                stage.close()
                return@runLater
            }
            this.contract = contract
            initForm()
        }
    }

    private fun initForm() {
        tvCompanyName.text = contract.company.name
        tvContractNumber.text = contract.number
    }

    @FXML
    fun onReportClick() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        FileInputStream("data/agreement.docx").use { inputStream ->
            val doc = XWPFDocument(inputStream)
            replaceText(doc, "НомерДокумента", contract.number)
            replaceText(doc, "ДатаДокумента", dateFormat.format(contract.dataOrder))
            replaceText(doc, "НазваниеКонтр", contract.company.name)
            replaceText(doc, "КонтрВЛице", contract.company.owner?.name ?: contract.manager.name)
            replaceText(doc, "ФИОИП", "Иванов Иван Иванович")
            doc.write(FileOutputStream("outDocs/договор_${contract.company.name}.docx"))
            doc.close()
        }
    }

    private fun replaceTextInParagraph(paragraph: XWPFParagraph, originalText: String, updatedText: String) {
        val runs = paragraph.runs
        for (run in runs) {
            val text = run.getText(0)

            if (text != null && text.contains(originalText)) {
                val updatedRunText = text.replace(originalText, updatedText)
                run.setText(updatedRunText, 0)
                run.setTextHighlightColor(STHighlightColor.WHITE.toString())
            }
        }
    }

    private fun replaceText(doc: XWPFDocument, originalText: String, updatedText: String): XWPFDocument? {
        replaceTextInParagraphs(doc.paragraphs, originalText, updatedText)
        for (tbl in doc.tables) {
            for (row in tbl.rows) {
                for (cell in row.tableCells) {
                    replaceTextInParagraphs(cell.paragraphs, originalText, updatedText)
                }
            }
        }
        return doc
    }

    private fun replaceTextInParagraphs(paragraphs: List<XWPFParagraph>, originalText: String, updatedText: String) {
        paragraphs.forEach(Consumer { paragraph: XWPFParagraph ->
            replaceTextInParagraph(
                paragraph, originalText, updatedText
            )
        })
    }
}
