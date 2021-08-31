package no.eirikb.avatest.utils

import com.intellij.lang.javascript.psi.JSLiteralExpression

/**
 * remove StringTemplate
 *      e.g:
 *          `${namespace} - test - ${suffix}` => `* - test - *`
 * remove blank string at the beginning and end
 */
fun getTestNameByClearUnnecessaryString(expression: JSLiteralExpression): String? {
    val testName = expression.stringValue

    if (testName != null) {
        return testName
    }

    val paramSourceCode = expression.text

    if (paramSourceCode.isNotEmpty() && paramSourceCode.startsWith("`") && paramSourceCode.endsWith("`")) {
        val stringTemplateRegex = Regex("\\$\\{.?\\}")
        return paramSourceCode.replace("`", "").replace(stringTemplateRegex, "*").trim()
    }

    return null
}
