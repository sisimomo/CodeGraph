package com.github.sisimomo.codegraph.actions

import com.github.sisimomo.codegraph.CodeGraphBundle
import com.github.sisimomo.codegraph.ui.DependenciesDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager


class ShowDependenciesAction : AnAction(
    CodeGraphBundle.message("dialog.showDependenciesAction.text"),
    CodeGraphBundle.message("dialog.showDependenciesAction.description"),
    null
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val selectedFiles: List<PsiJavaFile> = getSelectedJavaFiles(e, project)

        if (selectedFiles.isEmpty()) {
            Messages.showMessageDialog(
                project,
                CodeGraphBundle.message("dialog.noJavaFile.message"),
                CodeGraphBundle.message("dialog.noJavaFile.title"),
                Messages.getErrorIcon()
            )
            return
        }

        // Ask the user for an optional package filter (pre-filling with the default package)
        val packageFilterRaw = Messages.showInputDialog(
            project,
            CodeGraphBundle.message("dialog.packageFilter.message"),
            CodeGraphBundle.message("dialog.packageFilter.title"),
            Messages.getQuestionIcon(),
            findCommonPackagePrefix(selectedFiles.map { it.packageName }),
            null
        )

        // If the user pressed cancel, stop the flow
        if (packageFilterRaw == null) {
            return
        }
        val packageFilter = packageFilterRaw.trim()
        val dependencyGraph = collectDependencies(selectedFiles, packageFilter)

        DependenciesDialog(project, selectedFiles, dependencyGraph).show()
    }

    private fun getSelectedJavaFiles(event: AnActionEvent, project: Project): List<PsiJavaFile> {
        val selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(event.dataContext) ?: return emptyList()
        val psiManager = PsiManager.getInstance(project)

        return selectedFiles.mapNotNull { file ->
            psiManager.findFile(file) as? PsiJavaFile
        }
    }

    private fun collectDependencies(
        files: List<PsiJavaFile>,
        packageFilter: String
    ): MutableMap<PsiJavaFile, MutableList<PsiJavaFile>> {
        val visited = mutableSetOf<PsiJavaFile>()
        val dependencyGraph = mutableMapOf<PsiJavaFile, MutableList<PsiJavaFile>>()

        // Collect dependencies for each selected file
        files.forEach { file ->
            collectDependencies(file, packageFilter, visited, dependencyGraph)
        }

        return dependencyGraph
    }

    private fun collectDependencies(
        file: PsiJavaFile,
        packageFilter: String,
        visited: MutableSet<PsiJavaFile>,
        dependencyGraph: MutableMap<PsiJavaFile, MutableList<PsiJavaFile>>
    ) {
        // If already visited, stop recursion
        if (!visited.add(file)) return

        val dependencies = mutableListOf<PsiJavaFile>()

        file.importList?.allImportStatements?.forEach { importStatement ->
            val importedElement = importStatement.resolve() as? PsiClass ?: return@forEach
            val qualifiedName = importedElement.qualifiedName ?: return@forEach

            // Apply package filter
            if (packageFilter.isNotEmpty() && !qualifiedName.startsWith(packageFilter)) {
                return@forEach
            }

            val dependencyFile = importedElement.containingFile as? PsiJavaFile ?: return@forEach

            // Store dependency info
            dependencies.add(dependencyFile)

            // Recursively collect dependencies of this file
            collectDependencies(dependencyFile, packageFilter, visited, dependencyGraph)
        }

        // Store in the graph (even if it has no dependencies)
        dependencyGraph[file] = dependencies
    }

    private fun findCommonPackagePrefix(files: List<String>): String {
        if (files.isEmpty()) return ""

        val packageParts = files.map { it.split(".") }
        val minLength = packageParts.minOfOrNull { it.size } ?: 0

        return (0 until minLength)
            .takeWhile { index -> packageParts.map { it[index] }.distinct().size == 1 }
            .map { index -> packageParts[0][index] }
            .joinToString(".")
    }
}
