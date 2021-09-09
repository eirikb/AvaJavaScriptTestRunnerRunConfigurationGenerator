package no.eirikb.avatest.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "no.eirikb.avatest.settings.AppSettingsState", storages = [Storage("SdkSettingsPlugin.xml")])
object AppSettingsState : PersistentStateComponent<AppSettingsState?> {
    var inputPath = "node_modules/ava/cli.js"
    var selectedCommand = true
    var npmScriptsText = ""

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
