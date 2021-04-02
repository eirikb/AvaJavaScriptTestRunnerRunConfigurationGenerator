package no.eirikb.avatest.actions

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import java.nio.file.Paths

fun JSCallExpression.isTest() = this.methodExpression?.text.equals("test")

class AvaJavaScriptTestRunnerRunConfigurationGenerator : AnAction() {
    private fun writeError(text: String) {
        val notification =
            Notification(
                "no.eirikb.avatest",
                "AVA test run configuration generator error",
                text,
                NotificationType.ERROR
            )
        Notifications.Bus.notify(notification)
    }

    private fun getTestName(element: PsiElement?): String? {
        if (element == null ||  element.parent== null) {
            return null
        }

        if( element !is JSCallExpression ){
            return getTestName(element.parent)
        }

        val jsCallExpression: JSCallExpression = element
        if (jsCallExpression.isTest()) {
            val arguments: Array<JSExpression> = jsCallExpression.arguments
            if (arguments.isNotEmpty()) {
                if (arguments[0] is JSLiteralExpression) {
                    val expression: JSLiteralExpression = arguments[0] as JSLiteralExpression
                    return expression.stringValue
                }
                return null
            }
        }
        return getTestName(element.parent)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            writeError("Project not found")
            return
        }
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val currentFile = FileDocumentManager.getInstance().getFile(editor.document)
        if (currentFile == null) {
            writeError("Current file not found")
            return
        }
        var testName: String? = null
        val file = e.getData(PlatformDataKeys.PSI_FILE)
        if (file != null) {
            val element = file.findElementAt(editor.caretModel.offset)
            testName = getTestName(element)
        }
        val filePath = currentFile.path
        val fileName = Paths.get(filePath).fileName.toString()
        val basePath = project.basePath
        val relPath = if (basePath == null) fileName else currentFile.path.substring(basePath.length +1)
        val node: NodeJsRunConfiguration? =
            NodeJsRunConfiguration.getDefaultRunConfiguration(project)?.clone() as NodeJsRunConfiguration?
        if (node == null) {
            writeError("NodeJS run configuration type not found")
            return
        }
        val factory: ConfigurationFactory? = node.factory
        if (factory == null) {
            writeError("Factory not found")
            return
        }
        node.workingDirectory = basePath
        node.inputPath = "node_modules/ava/cli.js"
        if (testName != null) {
            node.name = "ava $fileName $testName"
            node.applicationParameters = "-m \"$testName\" -v $relPath"
        } else {
            node.name = "ava $fileName"
            node.applicationParameters = "-v $relPath"
        }
        val runManager = RunManager.getInstance(project)
        val configuration: RunnerAndConfigurationSettings = runManager.createConfiguration(node, factory)
        runManager.addConfiguration(configuration)
        runManager.selectedConfiguration = configuration
        ExecutionUtil.runConfiguration(configuration, DefaultRunExecutor.getRunExecutorInstance())
    }
}
