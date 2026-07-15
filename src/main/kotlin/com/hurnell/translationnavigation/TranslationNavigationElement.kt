package com.hurnell.translationnavigation

import com.intellij.json.psi.JsonProperty
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import javax.swing.Icon

class TranslationNavigationElement(
    val property: JsonProperty,
    private val langCode: String
) : FakePsiElement() {

    override fun getParent(): PsiElement = property.parent
    override fun getContainingFile() = property.containingFile
    override fun getTextRange() = property.textRange
    override fun getTextOffset() = property.textOffset
    override fun getProject() = property.project
    override fun getManager() = property.manager
    override fun isValid() = property.isValid

    override fun canNavigate() = property.canNavigate()
    override fun canNavigateToSource() = property.canNavigateToSource()
    override fun navigate(requestFocus: Boolean) {
        property.navigate(requestFocus)
    }

    override fun getIcon(open: Boolean): Icon? = property.getIcon(0)

    override fun getPresentation(): ItemPresentation {
        val propertyName = property.name
        val propertyValue = (property.value?.text ?: "")
            .removeSurrounding("\"")
            .removeSurrounding("'")

        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return "$langCode => $propertyName (\"$propertyValue\")"
            }

            override fun getIcon(unused: Boolean): Icon? {
                return property.getIcon(0)
            }
        }
    }
}