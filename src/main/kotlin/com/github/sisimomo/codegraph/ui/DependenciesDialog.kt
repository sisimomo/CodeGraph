package com.github.sisimomo.codegraph.ui

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.sisimomo.codegraph.CodeGraphBundle
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiFile
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.BorderLayout
import java.awt.datatransfer.StringSelection
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

class DependenciesDialog(
    private val project: Project,
    private val rootFiles: List<PsiFile>,
    private val dependencyGraph: Map<PsiFile, List<PsiFile>>
) : DialogWrapper(project, false) {

    private val disabledNodes = mutableSetOf<String>()
    private val fileToId = mutableMapOf<PsiFile, String>()
    private val objectMapper = jacksonObjectMapper()

    companion object {
        private const val WIDTH_KEY = "DependenciesDialog.width"
        private const val HEIGHT_KEY = "DependenciesDialog.height"
        private const val X_KEY = "DependenciesDialog.x"
        private const val Y_KEY = "DependenciesDialog.y"
    }

    init {
        init()
        title = CodeGraphBundle.message("dialog.dependencies.title", rootFiles.joinToString(", ") { it.name })
        isModal = false
    }

    override fun show() {
        super.show()
        restoreSizeAndPosition()
    }

    override fun dispose() {
        super.dispose()
        saveSizeAndPosition()
    }

    private fun saveSizeAndPosition() {
        window?.let {
            val properties = PropertiesComponent.getInstance()
            properties.setValue(WIDTH_KEY, it.width, -1)
            properties.setValue(HEIGHT_KEY, it.height, -1)
            properties.setValue(X_KEY, it.x, -1)
            properties.setValue(Y_KEY, it.y, -1)
        }
    }

    private fun restoreSizeAndPosition() {
        val properties = PropertiesComponent.getInstance()
        val width = properties.getInt(WIDTH_KEY, -1)
        val height = properties.getInt(HEIGHT_KEY, -1)
        val x = properties.getInt(X_KEY, -1)
        val y = properties.getInt(Y_KEY, -1)

        SwingUtilities.invokeLater {
            if (width != -1 || height != -1) {
                window?.setSize(width, height)
            }
            if (x != -1 || y != -1) {
                window?.setLocation(x, y)
            }
        }
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout())
        assignFileIds()

        val browser = JBCefBrowser()
        val jsQuery = getJBCefJSQuery(browser)

        browser.loadHTML(getHTML())

        setupLoadHandler(browser, jsQuery)

        mainPanel.add(browser.component, BorderLayout.CENTER)
        return mainPanel
    }

    private fun getJBCefJSQuery(browser: JBCefBrowser): JBCefJSQuery {
        val jsQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)

        jsQuery.addHandler { message ->
            try {
                val disabledList = objectMapper.readValue(message, Array<String>::class.java).toList()
                disabledNodes.clear()
                disabledNodes.addAll(disabledList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }
        return jsQuery
    }

    private fun assignFileIds() {
        var idCounter = 1
        fun getId(file: PsiFile) = fileToId.getOrPut(file) { "node_${idCounter++}" }

        rootFiles.forEach(::getId)  // Assign IDs to all root files
        dependencyGraph.keys.forEach(::getId)
        dependencyGraph.values.flatten().forEach(::getId)
    }

    private fun setupLoadHandler(browser: JBCefBrowser, jsQuery: JBCefJSQuery) {
        val loadHandler = object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
                if (!frame.isMain) {
                    return
                }

                executeJS(browser, "setGraphDataWhenReady('${getGraphJson()}');")
                executeJS(browser, "window.sendToJava = function(sendToJava) { ${jsQuery.inject("sendToJava")} };")
            }
        }

        browser.jbCefClient.addLoadHandler(loadHandler, browser.cefBrowser)
    }

    private fun executeJS(browser: CefBrowser, script: String) {
        browser.executeJavaScript(script, browser.url, 0)
    }


    private fun getGraphJson(): String {
        val nodes = fileToId.map { (file, id) ->
            mapOf(
                "data" to mapOf("id" to id, "label" to file.name),
                "classes" to if (file in rootFiles) "root" else ""
            )
        }

        val edges = dependencyGraph.flatMap { (fromFile, toList) ->
            fileToId[fromFile]?.let { fromId ->
                toList.mapNotNull { toFile ->
                    fileToId[toFile]?.let { toId ->
                        mapOf("data" to mapOf("id" to "edge_${fromId}_$toId", "source" to fromId, "target" to toId))
                    }
                }
            } ?: emptyList()
        }

        return objectMapper.writeValueAsString(mapOf("nodes" to nodes, "edges" to edges))
    }

    override fun createActions(): Array<Action> = arrayOf(createConcatenateAction(), createAiPromptAction())

    private fun createConcatenateAction(): Action {
        return object : DialogWrapperAction(CodeGraphBundle.message("button.concatenateToClipboard")) {
            override fun doAction(e: java.awt.event.ActionEvent?) {
                copyConcatenatedFilesToClipboard()
                close(OK_EXIT_CODE)
            }

            init {
                putValue(DEFAULT_ACTION, true)
            }
        }
    }

    private fun createAiPromptAction(): Action {
        return object : DialogWrapperAction(CodeGraphBundle.message("button.aiPromptToClipboard.label")) {
            override fun doAction(e: java.awt.event.ActionEvent?) {
                copyAiPromptToClipboard()
                close(OK_EXIT_CODE)
            }
        }
    }

    private fun copyConcatenatedFilesToClipboard() {
        val content = fileToId.entries
            .filterNot { disabledNodes.contains(it.value) }
            .joinToString("\n\n") { (file, _) -> "// File: ${getRelativePath(file)}\n${file.text}" }

        CopyPasteManager.getInstance().setContents(StringSelection(content))
    }

    private fun copyAiPromptToClipboard() {
        val files = getEnabledFiles()
        if (files.isEmpty()) return
        val prompt = buildAiPrompt(files)
        CopyPasteManager.getInstance().setContents(StringSelection(prompt.trim()))
    }

    private fun getEnabledFiles(): List<PsiFile> {
        return fileToId.entries
            .filterNot { disabledNodes.contains(it.value) }
            .map { it.key }
    }

    private fun buildAiPrompt(files: List<PsiFile>): String {
        return buildString {
            appendLine(CodeGraphBundle.message("button.aiPromptToClipboard.prompt"))
            files.forEach { file ->
                appendLine("- ${getRelativePath(file)}")
            }
        }
    }

    private fun getRelativePath(file: PsiFile): String {
        val vFile = file.virtualFile
        return vFile?.let {
            project.basePath.let { base ->
                try {
                    val basePath = Paths.get(base)
                    val filePath = Paths.get(it.path)
                    basePath.relativize(filePath).toString()
                } catch (_: IllegalArgumentException) {
                    it.path
                }
            } ?: it.path
        } ?: file.name
    }

    private fun getHTML(): String {
        val jsContent = loadResource("/com/github/sisimomo/codegraph/ui/graph.js")
        val cssContent = loadResource("/com/github/sisimomo/codegraph/ui/graph.css")

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Dependency Graph</title>
                <style>${cssContent}</style>
                <script src="https://unpkg.com/cytoscape/dist/cytoscape.min.js"></script>
                <script src="https://unpkg.com/dagre@0.8.5/dist/dagre.min.js"></script>
                <script src="https://unpkg.com/cytoscape-dagre@2.5.0/cytoscape-dagre.js"></script>
            </head>
            <body>
                <div id="cy"></div>
                <script>${jsContent}</script>
            </body>
            </html> 
        """.trimIndent()
    }

    private fun loadResource(path: String): String {
        return javaClass.getResourceAsStream(path)?.use {
            InputStreamReader(it, StandardCharsets.UTF_8).readText()
        } ?: throw RuntimeException("Resource not found: $path")
    }
}
