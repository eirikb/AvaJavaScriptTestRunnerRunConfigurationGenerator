package no.eirikb.avatest.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class AppSettingsComponent {
    val panel: JPanel
    val myInputPathText = JBTextField()
    var inputPathText: String
        get() = myInputPathText.text
        set(newText) {
            myInputPathText.text = newText
        }

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Enter path to AVA: "), myInputPathText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
