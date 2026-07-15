package com.hurnell.translationnavigation

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.project.guessProjectDir
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.AlignX
import javax.swing.JComponent

class TranslationSettingsConfigurable(private val project: Project) : Configurable {

    private val settings = TranslationSettings.getInstance(project)
    private var directoryField: TextFieldWithBrowseButton? = null

    override fun getDisplayName(): String = "Translation Navigation"

    override fun createComponent(): JComponent {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
            title = "Select Translation Directory"
            description = "Choose the folder containing your translation .json files"
        }

        val field = TextFieldWithBrowseButton()

        // Use the overload that allows us to intercept and customize the path on selection
        field.addBrowseFolderListener(
            project,
            descriptor,
            object : TextComponentAccessor<javax.swing.JTextField> {
                override fun getText(component: javax.swing.JTextField): String {
                    return component.text
                }

                override fun setText(component: javax.swing.JTextField, text: String) {
                    val projectDir = project.guessProjectDir()
                    if (projectDir != null) {
                        // Find the selected file in the Virtual File System
                        val selectedFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(text)
                        if (selectedFile != null) {
                            // Compute relative path from project root (e.g., "app/languages")
                            val relativePath = VfsUtilCore.getRelativePath(selectedFile, projectDir)
                            if (relativePath != null) {
                                component.text = relativePath
                                return
                            }
                        }
                    }

                    // Fallback to absolute path if we can't make it relative (e.g. outside project root)
                    component.text = text
                }
            }
        )

        directoryField = field

        return panel {
            row("Translation directory path:") {
                cell(field).align(AlignX.FILL)
            }.comment("Relative to the project root directory (e.g., 'app/languages')")
        }
    }

    override fun isModified(): Boolean {
        return directoryField?.text != settings.translationDirectory
    }

    override fun apply() {
        directoryField?.let {
            // Standardize slashes so paths behave nicely
            settings.translationDirectory = it.text.trim().replace("\\", "/").removePrefix("/").removeSuffix("/")
        }
    }

    override fun reset() {
        directoryField?.text = settings.translationDirectory
    }

    override fun disposeUIResources() {
        directoryField = null
    }
}