package no.eirikb.avatest.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String =
        "AVA Test Runner Run Configuration Generator"

    override fun getPreferredFocusedComponent() = mySettingsComponent!!.myInputPathText

    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        var modified = mySettingsComponent!!.inputPathText != AppSettingsState.inputPath
        modified = modified or (mySettingsComponent!!.inputPathText != AppSettingsState.inputPath)
        return modified
    }

    override fun apply() {
        AppSettingsState.inputPath = mySettingsComponent!!.inputPathText
    }

    override fun reset() {
        mySettingsComponent!!.inputPathText = AppSettingsState.inputPath
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
