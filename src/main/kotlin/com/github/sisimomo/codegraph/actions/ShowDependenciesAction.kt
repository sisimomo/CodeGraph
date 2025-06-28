package com.github.sisimomo.codegraph.actions

import com.github.sisimomo.codegraph.CodeGraphBundle
import com.github.sisimomo.codegraph.ui.DependenciesDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.PsiReference
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid


class ShowDependenciesAction : AnAction(
    CodeGraphBundle.message("dialog.showDependenciesAction.text"),
    CodeGraphBundle.message("dialog.showDependenciesAction.description"),
    null
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val selectedFiles: List<PsiFile> = getSelectedCodeFiles(e, project)

        if (selectedFiles.isEmpty()) {
            Messages.showMessageDialog(
                project,
                CodeGraphBundle.message("dialog.noJavaKotlinFile.message"),
                CodeGraphBundle.message("dialog.noJavaKotlinFile.title"),
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
            findCommonPackagePrefix(selectedFiles.mapNotNull { extractPackageName(it) }),
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

    private fun extractPackageName(file: PsiFile): String? {
        return when (file) {
            is PsiJavaFile -> file.packageName
            is KtFile -> file.packageFqName.asString().takeIf { it.isNotEmpty() }
            else -> null
        }
    }

    private fun getSelectedCodeFiles(event: AnActionEvent, project: Project): List<PsiFile> {
        val selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(event.dataContext) ?: return emptyList()
        val psiManager = PsiManager.getInstance(project)

        return selectedFiles.mapNotNull { file ->
            psiManager.findFile(file)
        }
    }

    private fun collectDependencies(
        files: List<PsiFile>,
        packageFilter: String
    ): MutableMap<PsiFile, MutableList<PsiFile>> {
        val visited = mutableSetOf<PsiFile>()
        val dependencyGraph = mutableMapOf<PsiFile, MutableList<PsiFile>>()

        files.forEach { file ->
            collectDependenciesRecursive(file, packageFilter, visited, dependencyGraph)
        }
        return dependencyGraph
    }

    private fun collectDependenciesRecursive(
        file: PsiFile,
        packageFilter: String,
        visited: MutableSet<PsiFile>,
        dependencyGraph: MutableMap<PsiFile, MutableList<PsiFile>>
    ) {
        if (!visited.add(file)) return
        val dependencies = mutableSetOf<PsiFile>()

        when (file) {
            is KtFile -> collectKotlinDependencies(file, packageFilter, file, dependencies, visited, dependencyGraph)
            else -> collectJavaDependencies(file, packageFilter, file, dependencies, visited, dependencyGraph)
        }
        dependencyGraph[file] = dependencies.toMutableList()
    }

    private fun collectKotlinDependencies(
        file: KtFile,
        packageFilter: String,
        currentFile: PsiFile,
        dependencies: MutableSet<PsiFile>,
        visited: MutableSet<PsiFile>,
        dependencyGraph: MutableMap<PsiFile, MutableList<PsiFile>>
    ) {
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitReferenceExpression(expression: KtReferenceExpression) {
                super.visitReferenceExpression(expression)
                val resolved = expression.references.firstOrNull()?.resolve()
                processDependency(resolved, packageFilter, currentFile, dependencies, visited, dependencyGraph)
            }
        })
    }

    private fun collectJavaDependencies(
        file: PsiFile,
        packageFilter: String,
        currentFile: PsiFile,
        dependencies: MutableSet<PsiFile>,
        visited: MutableSet<PsiFile>,
        dependencyGraph: MutableMap<PsiFile, MutableList<PsiFile>>
    ) {
        file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                if (element is PsiReference) {
                    val resolved = element.resolve()
                    processDependency(resolved, packageFilter, currentFile, dependencies, visited, dependencyGraph)
                }
            }
        })
    }

    private fun processDependency(
        resolved: PsiElement?,
        packageFilter: String,
        currentFile: PsiFile,
        dependencies: MutableSet<PsiFile>,
        visited: MutableSet<PsiFile>,
        dependencyGraph: MutableMap<PsiFile, MutableList<PsiFile>>
    ) {
        val dependencyFile = resolveDependencyFile(resolved ?: return)
        val qualifiedName = resolveQualifiedName(resolved)
        if (dependencyFile != null && qualifiedName != null) {
            if ((packageFilter.isEmpty() || qualifiedName.startsWith(packageFilter)) && dependencyFile != currentFile && dependencies.add(
                    dependencyFile
                )
            ) {
                collectDependenciesRecursive(dependencyFile, packageFilter, visited, dependencyGraph)
            }
        }
    }

    private fun resolveDependencyFile(resolved: PsiElement): PsiFile? {
        return when (resolved) {
            is PsiClass -> resolved.containingFile
            is PsiMember -> resolved.containingClass?.containingFile
            is KtClassOrObject -> resolved.containingKtFile
            is KtFile -> resolved
            else -> null
        } as? PsiFile
    }

    private fun resolveQualifiedName(resolved: PsiElement): String? {
        return when (resolved) {
            is PsiClass -> resolved.qualifiedName
            is KtClassOrObject -> resolved.fqName?.asString()
            is KtFile -> resolved.packageFqName.asString()
            else -> null
        }
    }

    private fun findCommonPackagePrefix(files: List<String>): String {
        if (files.isEmpty()) return ""

        val packageParts = files.map { it.split(".") }
        val minLength = packageParts.minOfOrNull { it.size } ?: 0

        return (0 until minLength)
            .takeWhile { index -> packageParts.map { it[index] }.distinct().size == 1 }
            .joinToString(".") { index -> packageParts[0][index] }
    }
}
