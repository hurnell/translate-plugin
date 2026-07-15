package com.hurnell.translationnavigation

import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiManager
import com.intellij.openapi.project.guessProjectDir

class TranslationKeyFinder(
    private val project: Project
) {

    /**
     * Finds the property matching [key] in ALL available translation files.
     */
    fun findAll(key: String): List<JsonProperty> {
        val projectDir = project.guessProjectDir() ?: return emptyList()
        val configuredPath = TranslationSettings.getInstance(project).translationDirectory
        val languages = VfsUtil.findRelativeFile(configuredPath, projectDir) ?: return emptyList()
        val pathSegments = key.split(".")

        return languages.children
            .filter { it.name.endsWith(".json") }
            .mapNotNull { file ->
                val psiFile = PsiManager.getInstance(project).findFile(file)
                if (psiFile is JsonFile) {
                    findPropertyByPath(psiFile.topLevelValue, pathSegments)
                } else {
                    null
                }
            }
    }

    private fun findPropertyByPath(
        element: com.intellij.json.psi.JsonValue?,
        pathSegments: List<String>
    ): JsonProperty? {
        if (element == null || pathSegments.isEmpty()) return null

        val obj = element as? com.intellij.json.psi.JsonObject ?: return null
        val currentSegment = pathSegments.first()

        val property = obj.propertyList.find { it.name == currentSegment }
            ?: return null

        if (pathSegments.size == 1) {
            return property
        }

        return findPropertyByPath(
            property.value,
            pathSegments.drop(1)
        )
    }
}