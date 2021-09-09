package no.eirikb.avatest.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.ButtonGroup
import javax.swing.JPanel

const val MARGIN_TOP = 10
const val MARGIN_BOTTOM = 8

class AppSettingsComponent {
    val panel: JPanel
    val myInputPathText = JBTextField()
    var inputPathText: String
        get() = myInputPathText.text
        set(newText) {
            myInputPathText.text = newText
        }

    private val npmPackageJSONPathInput = ComboBox<String>().apply {
        isEditable = false
        selectedItem = ""
    }

    private val npmScriptsInput = JBTextField()
    var npmScriptsText: String
        get() = npmScriptsInput.text
        set(text) {
            npmScriptsInput.text = text
        }

    private val commandModelRadioButton = JBRadioButton("Command Model")
    private val npmModelRadioButton = JBRadioButton("NPM Model")
    var selectedCommand: Boolean
        get() = commandModelRadioButton.isSelected
        set(isCommand) {
            commandModelRadioButton.isSelected = isCommand
            npmModelRadioButton.isSelected = !isCommand

            myInputPathText.isEnabled = isCommand
            npmPackageJSONPathInput.isEnabled = !isCommand
            npmScriptsInput.isEnabled = !isCommand
        }

    init {
        commandModelRadioButton.addChangeListener {
            selectedCommand = commandModelRadioButton.isSelected
        }

        ButtonGroup().apply {
            add(commandModelRadioButton)
            add(npmModelRadioButton)
        }

        panel = FormBuilder.createFormBuilder()
            .addComponent(commandModelRadioButton)
            .addLabeledComponent(JBLabel("Enter path to AVA: "), myInputPathText, 1, false)
            .addSeparator(MARGIN_TOP)
            .addVerticalGap(MARGIN_BOTTOM)
            .addComponent(npmModelRadioButton)
            .addLabeledComponent(JBLabel("npm Scripts: "), npmScriptsInput, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
