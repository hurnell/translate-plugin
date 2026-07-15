package com.hurnell.translationnavigation

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.ProcessingContext

class TranslationReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<PsiReference> {

        if (element !is XmlAttributeValue) {
            return PsiReference.EMPTY_ARRAY
        }

        val attribute = element.parent as? XmlAttribute ?: return PsiReference.EMPTY_ARRAY


        if (attribute.name != "translate") {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(
            TranslationReference(element, element.value)
        )
    }
}