package com.stonesoupprogramming.mnemosynehelper

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage

fun main(args : Array<String>){
    Application.launch(MnemosyneHelper::class.java, *args)
}

fun StringBuilder.appendLine(line : String = "\n"){
    this.append(line + "\n")
}

data class Card(var question : String = "",
                var choices : List<String> = listOf(),
                var answer: String = "",
                var explanation : String = ""){

    private val abcs : Map<Int, String>

    init {
        val abcMap = mutableMapOf<Int, String>()
        for (i in 65..90){
            abcMap[i - 65] = i.toChar().toString()
        }
        abcs = abcMap
    }

    fun formatTop () : String {
        return with(StringBuilder()){
            appendLine(question)
            appendLine("""<ol type="A">""")
            choices.forEachIndexed { index, s -> appendLine("""<!-- ${abcs[index]} --> <li>$s</li>""") }
            appendLine("""</ol>""")
            toString()
        }
    }

    fun formatBottom () : String {
        return with(StringBuilder()){
            appendLine("Answer: $answer")
            if(explanation.isNotBlank()){
                appendLine("<hr/>")
                appendLine(explanation)
            }
            toString()
        }
    }
}

class MnemosyneHelper : Application(){

    private val questionText = Text("Question")
    private val questionBox = TextArea()

    private val choicesText = Text("Choices--Separate by 'Enter' key")
    private val choicesBox = TextArea()

    private val answerText = Text("Correct Answer Here")
    private val answerBox = TextArea()

    private val explanationText = Text("Optional Detailed Explanation")
    private val explanationBox = TextArea()

    private val upperText = Text("Paste into top half")
    private val upperBox = TextArea()

    private val bottomText = Text("Paste into the bottom half")
    private val bottomBox = TextArea()

    private val card = Card()

    private val clearButton = Button("Clear")

    init {
        upperBox.editableProperty().value = false
        bottomBox.editableProperty().value = false
    }

    private fun buildLeft() : VBox{
        return VBox(10.toDouble(), questionText, questionBox,
                choicesText, choicesBox,
                answerText, answerBox,
                explanationText, explanationBox)
    }

    private fun buildRight() : VBox {
        return VBox(10.toDouble(),
                upperText, upperBox,
                bottomText, bottomBox)
    }

    private fun buildUI() : BorderPane {
        configureListeners()
        return with(BorderPane()){
            padding = Insets(10.toDouble(), 10.toDouble(), 10.toDouble(), 10.toDouble())
            center = HBox(10.toDouble(), buildLeft(), buildRight())
            val hBox = HBox(10.toDouble(), clearButton)
            hBox.alignment = Pos.CENTER_RIGHT
            bottom = hBox
            this
        }
    }

    private fun configureListeners() {
        questionBox.textProperty().addListener { _, _, newValue ->
            card.question = newValue
            upperBox.text = card.formatTop()
        }
        choicesBox.textProperty().addListener { _, _, newValue ->
            card.choices = newValue.split("\n")
            upperBox.text = card.formatTop()
        }
        answerBox.textProperty().addListener { _, _, newValue ->
            card.answer = newValue
            bottomBox.text = card.formatBottom()
        }
        explanationBox.textProperty().addListener { _, _, newValue ->
            card.explanation = newValue
            bottomBox.text = card.formatBottom()
        }
        clearButton.setOnAction {
            with(card){
                question = ""
                choices = listOf()
                answer = ""
                explanation = ""
            }
            val boxes = listOf(questionBox, choicesBox, answerBox, explanationBox)
            boxes.forEach { box -> box.text = "" }
        }
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Mnemosyne Helper"
        primaryStage.scene = Scene(buildUI())
        primaryStage.sizeToScene()
        primaryStage.show()
    }

}