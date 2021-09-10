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
        var modifiedInputPath = mySettingsComponent!!.inputPathText != AppSettingsState.inputPath
        modifiedInputPath = modifiedInputPath or (mySettingsComponent!!.inputPathText != AppSettingsState.inputPath)

        var modifiedSelectedModel = mySettingsComponent!!.selectedCommand != AppSettingsState.selectedCommand
        modifiedSelectedModel =
            modifiedSelectedModel or (mySettingsComponent!!.selectedCommand != AppSettingsState.selectedCommand)

        var modifiedNPMScriptsText = mySettingsComponent!!.npmScriptsText != AppSettingsState.npmScriptsText
        modifiedNPMScriptsText =
            modifiedNPMScriptsText or (mySettingsComponent!!.npmScriptsText != AppSettingsState.npmScriptsText)

        return modifiedInputPath || modifiedSelectedModel || modifiedNPMScriptsText
    }

    override fun apply() {
        AppSettingsState.inputPath = mySettingsComponent!!.inputPathText
        AppSettingsState.selectedCommand = mySettingsComponent!!.selectedCommand
        AppSettingsState.npmScriptsText = mySettingsComponent!!.npmScriptsText
    }

    override fun reset() {
        mySettingsComponent!!.inputPathText = AppSettingsState.inputPath
        mySettingsComponent!!.selectedCommand = AppSettingsState.selectedCommand
        mySettingsComponent!!.npmScriptsText = AppSettingsState.npmScriptsText
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
