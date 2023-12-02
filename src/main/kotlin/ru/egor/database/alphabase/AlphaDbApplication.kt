package ru.egor.database.alphabase

import com.google.inject.Guice
import com.google.inject.Injector
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Callback
import ru.egor.database.alphabase.di.CoreModule

class AlphaDbApplication : Application() {

    companion object {
        val injector: Injector = Guice.createInjector(CoreModule())
    }

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainScreenController::class.java.getResource("main.fxml"))
        fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
            injector.getInstance(
                instantiatedClass
            )
        }
        val scene = Scene(fxmlLoader.load())
        stage.title = "AlphaDbApplication"
        scene.userData = 43
        stage.scene = scene
        stage.show()
    }

    private fun showAdd(stage: Stage, injector: Injector) {
        val fxmlLoader = FXMLLoader(AddCompanyController::class.java.getResource("add_company_form.fxml"))
        fxmlLoader.controllerFactory = Callback { instantiatedClass: Class<*>? ->
            injector.getInstance(
                instantiatedClass
            )
        }
        val scene = Scene(fxmlLoader.load(), 1200.0, 600.0)
        stage.title = "AlphaDbApplication"
        scene.userData = 39
        stage.scene = scene
        stage.show()
    }

}

fun main() {
    Application.launch(AlphaDbApplication::class.java)
}

