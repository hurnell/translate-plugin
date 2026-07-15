package com.hurnell.translationnavigation

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.PsiElementResolveResult

class TranslationReference(
    element: PsiElement,
    private val key: String
) : PsiReferenceBase<PsiElement>(element), PsiPolyVariantReference {

    override fun getVariants(): Array<Any> {
        return arrayOf(
            "🔤 Translation: $key"
        )
    }

    override fun getCanonicalText(): String {
        return "🔤 Translation key: $key"
    }

    /**
     * Standard resolve used for single-target checks.
     * We default to the first one found if multi-resolve is not supported by a specific action.
     */
    override fun resolve(): PsiElement? {
        val results = multiResolve(false)
        return if (results.isNotEmpty()) results[0].element else null
    }

    /**
     * This is the magic method. Returning multiple results here signals
     * the IDE to show a navigation popup when CMD/Ctrl+Clicked.
     */
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {

        val matches = TranslationKeyFinder(element.project).findAll(key)

        return matches.map { property ->
            PsiElementResolveResult(property)
        }.toTypedArray()
    }

    override fun isSoft(): Boolean {
        return false
    }
}