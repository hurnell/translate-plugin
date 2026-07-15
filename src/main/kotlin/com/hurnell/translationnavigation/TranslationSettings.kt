package com.hurnell.translationnavigation

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "TranslationNavigationSettings",
    storages = [Storage("translationNavigationSettings.xml")]
)
class TranslationSettings : SimplePersistentStateComponent<TranslationSettings.State>(State()) {

    class State : BaseState() {
        // "app/languages" is our fallback default
        var translationDirectory by string("app/languages")
    }

    var translationDirectory: String
        get() = state.translationDirectory ?: "app/languages"
        set(value) {
            state.translationDirectory = value
        }

    companion object {
        fun getInstance(project: Project): TranslationSettings = project.service()
    }
}