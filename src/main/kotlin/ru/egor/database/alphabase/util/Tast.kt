package ru.egor.database.alphabase.util

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


object Toast {

    fun makeText(
        toastMsg: String?,
        toastDelay: Int = 1500,
        fadeInDelay: Double = 500.0,
        fadeOutDelay: Double = 500.0
    ) {
        val toastStage = Stage().apply {
            isResizable = false
            initStyle(StageStyle.TRANSPARENT)

        }
        toastStage.setOnShown {
            toastStage.y = Screen.getPrimary().visualBounds.maxY - 300
        }

        val text = Text(toastMsg).apply {
            font = Font.font("Verdana", 20.0)
            fill = Color.BLACK

        }
        val root = StackPane(text).apply {
            style = "-fx-background-radius: 10; -fx-background-color: cornsilk; -fx-padding: 14px;"
            opacity = 0.0

        }

        val scene = Scene(root).apply {
            fill = Color.TRANSPARENT
        }

        toastStage.scene = scene

        toastStage.show()
        toastStage.scene.root
        val fadeInTimeline = Timeline()
        val fadeInKey1 =
            KeyFrame(Duration.millis(fadeInDelay), KeyValue(toastStage.scene.root.opacityProperty(), 1))
        fadeInTimeline.keyFrames.add(fadeInKey1)
        fadeInTimeline.onFinished = EventHandler {
            Thread {
                try {
                    Thread.sleep(toastDelay.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                val fadeOutTimeline = Timeline()
                val fadeOutKey1 = KeyFrame(
                    Duration.millis(fadeOutDelay),
                    KeyValue(toastStage.scene.root.opacityProperty(), 0)
                )
                fadeOutTimeline.keyFrames.add(fadeOutKey1)
                fadeOutTimeline.onFinished =
                    EventHandler { toastStage.close() }
                fadeOutTimeline.play()
            }.start()
        }
        fadeInTimeline.play()
    }
}
