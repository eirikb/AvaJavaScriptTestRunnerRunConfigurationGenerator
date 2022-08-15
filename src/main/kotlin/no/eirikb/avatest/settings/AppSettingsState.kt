package no.eirikb.avatest.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "no.eirikb.avatest.settings.AppSettingsState", storages = [Storage("SdkSettingsPlugin.xml")])
class AppSettingsState : BaseState(), PersistentStateComponent<AppSettingsState> {
    var inputPath by string(null)
    var selectedCommand by property(true)
    var npmScriptsText by string("")

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }

    override fun getState(): AppSettingsState = this

    override fun loadState(state: AppSettingsState) {
        copyFrom(state)
    }
}
