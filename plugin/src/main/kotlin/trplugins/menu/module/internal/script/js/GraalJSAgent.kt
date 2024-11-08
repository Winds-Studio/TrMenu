package trplugins.menu.module.internal.script.js

import io.lilingfeng.trmenu.graal.JavaScriptAgent
import trplugins.menu.util.EvalResult
import javax.script.SimpleScriptContext

object GraalJSAgent {
    fun eval(context: SimpleScriptContext, script: String, cacheScript: Boolean = true): EvalResult {
        return EvalResult(JavaScriptAgent.eval(context, script, cacheScript))
    }

    fun preCompile(script: String) {
        JavaScriptAgent.preCompile(script)
    }
}