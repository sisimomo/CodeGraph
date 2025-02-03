package com.github.sisimomo.codegraph.actions

import com.github.sisimomo.codegraph.CodeGraphBundle
import com.github.sisimomo.codegraph.ui.DependenciesDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile

class ShowDependenciesAction : AnAction("Show Dependencies") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Get the currently open file as a PsiJavaFile
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile
        if (psiFile == null) {
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
            psiFile.packageName,  // Pre-filled with the detected base package
            null
        )

        // If the user pressed cancel, stop the flow
        if (packageFilterRaw == null) {
            return
        }
        val packageFilter = packageFilterRaw.trim()

        val visited = mutableSetOf<PsiJavaFile>()
        val dependencyGraph = mutableMapOf<PsiJavaFile, MutableList<PsiJavaFile>>() // <-- Store full dependency map

        // Collect dependencies into the graph
        collectDependencies(psiFile, packageFilter, visited, dependencyGraph)

        // Show dependencies (for now, in a simple list)
        DependenciesDialog(project, psiFile, dependencyGraph).show()
    }

    private fun collectDependencies(
        file: PsiJavaFile,
        packageFilter: String,
        visited: MutableSet<PsiJavaFile>,
        dependencyGraph: MutableMap<PsiJavaFile, MutableList<PsiJavaFile>> // <-- Now tracks dependencies
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
}
