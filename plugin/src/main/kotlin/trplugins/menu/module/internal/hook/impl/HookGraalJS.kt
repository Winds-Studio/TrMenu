package trplugins.menu.module.internal.hook.impl

import io.lilingfeng.trmenu.graal.JavaScriptAgent
import trplugins.menu.module.internal.hook.HookAbstract
import trplugins.menu.util.EvalResult
import javax.script.SimpleScriptContext


class HookGraalJS : HookAbstract() {

    override fun getPluginName(): String {
        return "TrMenu-Graal"
    }
    override val isHooked by lazy {
        plugin != null && plugin!!.isEnabled
    }

}

object GraalJSAgent {
    fun eval(context: SimpleScriptContext, script: String, cacheScript: Boolean = true): EvalResult {
        return EvalResult(JavaScriptAgent.eval(context, script, cacheScript))
    }
    fun preCompile(script: String) {
        JavaScriptAgent.preCompile(script)
    }
}