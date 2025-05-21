package no.eirikb.avatest.actions

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.lang.javascript.buildTools.npm.rc.NpmConfigurationType
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunConfiguration
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunSettings
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.psi.PsiElement
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import no.eirikb.avatest.settings.AppSettingsState
import no.eirikb.avatest.utils.getTestNameByClearUnnecessaryString
import java.nio.file.Paths

fun JSCallExpression.isTest(): Boolean {
    val text = this.methodExpression?.text

    if (text != null) {
        return text == "test" || text.startsWith("test.")
    }

    return false
}

fun getConfigurationName(fileName: String, testName: String?): String {
    return if (testName != null) {
        "ava $fileName $testName"
    } else {
        "ava $fileName"
    }
}

fun getRunArguments(relPath: String, testName: String?): String {
    return if (testName != null) {
        "-m \"$testName\" -v $relPath"
    } else {
        "-v $relPath"
    }
}

class AvaJavaScriptTestRunnerRunConfigurationGenerator : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
    companion object {
        fun performAction(e: AnActionEvent, debug: Boolean = false, offset: Int? = null) {
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
                val element = file.findElementAt(offset ?: editor.caretModel.offset)
                testName = getTestName(element)
            }
            val filePath = currentFile.path
            val fileName = Paths.get(filePath).fileName.toString()
            val basePath = project.basePath
            val relPath = if (basePath == null) fileName else currentFile.path.substring(basePath.length + 1)

            val configuration = if (AppSettingsState.instance.selectedCommand) {
                this.createNodeJsRunConfiguration(project, fileName, relPath, testName)
            } else {
                this.createNPMRunConfiguration(project, currentFile, fileName, relPath, testName)
            }

            if (configuration == null) {
                return
            }

            if (debug) {
                val executor = ExecutorRegistry.getInstance().getExecutorById(ToolWindowId.DEBUG)
                    ?: DefaultRunExecutor.getRunExecutorInstance()
                ExecutionUtil.runConfiguration(configuration, executor)
            } else {
                ExecutionUtil.runConfiguration(configuration, DefaultRunExecutor.getRunExecutorInstance())
            }
        }

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
            if (element == null || element.parent == null) {
                return null
            }

            if (element !is JSCallExpression) {
                return getTestName(element.parent)
            }

            val jsCallExpression: JSCallExpression = element
            if (jsCallExpression.isTest()) {
                val arguments: Array<JSExpression> = jsCallExpression.arguments
                if (arguments.isNotEmpty()) {
                    if (arguments[0] is JSLiteralExpression) {
                        val expression: JSLiteralExpression = arguments[0] as JSLiteralExpression
                        return getTestNameByClearUnnecessaryString(expression)
                    }
                    return null
                }
            }
            return getTestName(element.parent)
        }

        private fun createNodeJsRunConfiguration(
            project: Project,
            fileName: String,
            relPath: String,
            testName: String?,
        ): RunnerAndConfigurationSettings? {
            val node: NodeJsRunConfiguration? =
                NodeJsRunConfiguration.getDefaultRunConfiguration(project)?.clone() as NodeJsRunConfiguration?
            if (node == null) {
                writeError("NodeJS run configuration type not found")
                return null
            }
            val factory: ConfigurationFactory? = node.factory
            if (factory == null) {
                writeError("Factory not found")
                return null
            }
            node.workingDirectory = project.basePath
            node.inputPath = AppSettingsState.instance.inputPath
            if (node.inputPath == null) {
                val projectDir = project.guessProjectDir()
                node.inputPath = listOf(
                    "node_modules/ava/entrypoints/cli.mjs",
                    "node_modules/ava/cli.js"
                ).find { projectDir?.findFileByRelativePath(it)?.exists() == true }
            }
            node.name = getConfigurationName(fileName, testName)
            node.applicationParameters = getRunArguments(relPath, testName)

            val runManager = RunManager.getInstance(project)
            val configuration: RunnerAndConfigurationSettings = runManager.createConfiguration(node, factory)
            runManager.addConfiguration(configuration)
            runManager.selectedConfiguration = configuration

            return configuration
        }

        private fun createNPMRunConfiguration(
            project: Project,
            currentFile: VirtualFile,
            fileName: String,
            relPath: String,
            testName: String?,
        ): RunnerAndConfigurationSettings? {
            val npmRunSettingsBuilder = NpmRunSettings.builder()
            val packageJsonPath = PackageJsonUtil.findUpPackageJson(currentFile)?.path

            if (packageJsonPath == null) {
                return null
            }

            npmRunSettingsBuilder.setPackageJsonPath(packageJsonPath)

            npmRunSettingsBuilder.setArguments(getRunArguments(relPath, testName))
            npmRunSettingsBuilder.setScriptNames(listOf(AppSettingsState.instance.npmScriptsText))

            val npmRunConfiguration = NpmRunConfiguration(
                project,
                NpmConfigurationType.getInstance(),
                getConfigurationName(fileName, testName)
            )
            npmRunConfiguration.runSettings = npmRunSettingsBuilder.build()

            val runManager = RunManager.getInstance(project)
            val configuration = runManager.createConfiguration(npmRunConfiguration, NpmConfigurationType.getInstance())
            runManager.addConfiguration(configuration)
            runManager.selectedConfiguration = configuration

            return configuration
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        performAction(e)
    }
}
