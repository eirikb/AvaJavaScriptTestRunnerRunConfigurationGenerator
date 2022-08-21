package no.eirikb.avatest.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null
    private val settings = AppSettingsState.instance

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String =
        "AVA Test Runner Run Configuration Generator"

    override fun getPreferredFocusedComponent() = mySettingsComponent!!.myInputPathText

    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        var modifiedInputPath = mySettingsComponent!!.inputPathText != settings.inputPath
        modifiedInputPath = modifiedInputPath or (mySettingsComponent!!.inputPathText != settings.inputPath)

        var modifiedSelectedModel = mySettingsComponent!!.selectedCommand != settings.selectedCommand
        modifiedSelectedModel =
            modifiedSelectedModel or (mySettingsComponent!!.selectedCommand != settings.selectedCommand)

        var modifiedNPMScriptsText = mySettingsComponent!!.npmScriptsText != settings.npmScriptsText
        modifiedNPMScriptsText =
            modifiedNPMScriptsText or (mySettingsComponent!!.npmScriptsText != settings.npmScriptsText)

        return modifiedInputPath || modifiedSelectedModel || modifiedNPMScriptsText
    }

    override fun apply() {
        settings.inputPath = mySettingsComponent!!.inputPathText
        settings.selectedCommand = mySettingsComponent!!.selectedCommand
        settings.npmScriptsText = mySettingsComponent!!.npmScriptsText
    }

    override fun reset() {
        mySettingsComponent!!.inputPathText = settings.inputPath
        mySettingsComponent!!.selectedCommand = settings.selectedCommand
        mySettingsComponent!!.npmScriptsText = settings.npmScriptsText ?: ""
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
