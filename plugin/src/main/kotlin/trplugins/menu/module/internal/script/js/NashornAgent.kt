package trplugins.menu.module.internal.script.js

import com.google.common.collect.Maps
import taboolib.common5.compileJS
import trplugins.menu.util.EvalResult
import javax.script.CompiledScript
import javax.script.SimpleScriptContext

object NashornAgent {
    private val compiledScripts = Maps.newConcurrentMap<String, CompiledScript>();
    fun preCompile(script: String): CompiledScript {
        return compiledScripts.computeIfAbsent(script) {
            script.compileJS()
        }
    }
    fun eval(context:SimpleScriptContext, script: String, cacheScript: Boolean = true): EvalResult {
        val compiledScript =
            if (cacheScript) preCompile(script)
            else script.compileJS()

        return EvalResult(compiledScript?.eval(context))
    }
}