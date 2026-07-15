package com.hurnell.translationnavigation

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue

class TranslationGotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {
        if (sourceElement == null) return null

        // --- Handle JSON Aliases: "@:ASCRIPTION_CASE_DASHBOARD.ADD.LABEL_STEP2" ---
        val jsonAliasTarget = findJsonAliasTarget(sourceElement)
        if (jsonAliasTarget != null) {
            // Since we're jumping locally in the same file, we can just return
            // the matching JsonProperty directly to navigate instantly.
            return arrayOf(jsonAliasTarget)
        }

        // --- Handle normal template/TS references ---
        val key = extractTranslationKey(sourceElement) ?: return null
        if (key.isEmpty()) return null

        val targets = TranslationKeyFinder(sourceElement.project).findAll(key)
        if (targets.isEmpty()) return null

        return targets.map { property ->
            val fileName = property.containingFile?.name ?: "unknown.json"
            val langCode = fileName.substringBeforeLast(".json")

            TranslationNavigationElement(property, langCode)
        }.toTypedArray()
    }

    /**
     * Checks if the clicked element is a JSON string literal starting with "@:"
     * and returns the resolved property within the same file.
     */
    private fun findJsonAliasTarget(element: PsiElement): PsiElement? {
        // When clicking inside a JSON value, the element's parent is typically the JsonStringLiteral
        val stringLiteral = element.parent as? JsonStringLiteral ?: return null
        val file = stringLiteral.containingFile as? JsonFile ?: return null

        val rawText = stringLiteral.value
        if (rawText.startsWith("@:")) {
            val targetPath = rawText.removePrefix("@:")

            val pathSegments = targetPath.split(".")

            // Re-use your helper method to traverse the current JSON file
            return findPropertyByPath(file.topLevelValue, pathSegments)
        }
        return null
    }

    /**
     * Helper traversal method (copied from your TranslationKeyFinder)
     */
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

    private fun extractTranslationKey(element: PsiElement): String? {
        val text = element.text ?: return null
        val cleanKey = text.removeSurrounding("\"").removeSurrounding("'")
        if (cleanKey.isEmpty()) return null

        // 1. Handle HTML attribute: translate="some.key"
        val attributeValue = element.parent as? XmlAttributeValue
        if (attributeValue != null) {
            val attribute = attributeValue.parent as? XmlAttribute
            if (attribute?.name == "translate") {
                return cleanKey
            }
        }

        // 2. Document Line Scan
        val containingFile = element.containingFile ?: return null
        val project = element.project
        val document = com.intellij.psi.PsiDocumentManager.getInstance(project).getDocument(containingFile)

        if (document != null) {
            val textOffset = element.textOffset
            val lineNumber = document.getLineNumber(textOffset)
            val lineStart = document.getLineStartOffset(lineNumber)
            val lineEnd = document.getLineEndOffset(lineNumber)
            val lineText = document.charsSequence.subSequence(lineStart, lineEnd).toString()

            val pipePattern = """['"]${Regex.escape(cleanKey)}['"]\s*\|\s*translate""".toRegex()
            val instantPattern = """\.instant\(\s*['"]${Regex.escape(cleanKey)}['"]\s*\)""".toRegex()

            if (pipePattern.containsMatchIn(lineText) || instantPattern.containsMatchIn(lineText)) {
                return cleanKey
            }
        }

        return null
    }
}