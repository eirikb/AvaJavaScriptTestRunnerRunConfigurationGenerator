package no.eirikb.avatest.markers

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.RunConfigurations.TestState
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import no.eirikb.avatest.actions.AvaJavaScriptTestRunnerRunConfigurationGenerator
import no.eirikb.avatest.actions.isTest
import javax.swing.Icon

class AvaRunContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (element !is JSCallExpression) {
            return null
        }

        val jsCallExpression: JSCallExpression = element
        if (jsCallExpression.isTest()) {
            val arguments: Array<JSExpression> = jsCallExpression.arguments
            if (arguments.isNotEmpty()) {
                if (arguments[0] is JSLiteralExpression) {
                    val expression: JSLiteralExpression = arguments[0] as JSLiteralExpression
                    return Info(
                        TestState.Run,
                        getActions(element.textOffset, "${expression.stringValue}")
                    ) { "Run Test" }
                }
                return null
            }
        }

        return null
    }

    private fun getActions(offset: Int, testName: String): Array<AnAction> {
        return listOf(
            object : RunAction("Run", testName, TestState.Run) {
                override fun actionPerformed(e: AnActionEvent) {
                    AvaJavaScriptTestRunnerRunConfigurationGenerator.performAction(e, false, offset)
                }
            },
            object : RunAction("Debug", testName, AllIcons.Actions.StartDebugger) {
                override fun actionPerformed(e: AnActionEvent) {
                    AvaJavaScriptTestRunnerRunConfigurationGenerator.performAction(e, true, offset)
                }
            }
        ).toTypedArray()
    }
}

abstract class RunAction(prefix: String, testName: String, icon: Icon) :
    AnAction("$prefix '$testName'", "$prefix '$testName'", icon)
