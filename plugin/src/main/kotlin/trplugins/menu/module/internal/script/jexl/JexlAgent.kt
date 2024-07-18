package trplugins.menu.module.internal.script.jexl

import com.google.common.collect.Maps
import org.bukkit.Bukkit
import taboolib.expansion.JexlCompiledScript
import taboolib.expansion.defaultJexlCompiler
import trplugins.menu.module.display.MenuSession
import trplugins.menu.module.internal.data.Metadata
import trplugins.menu.module.internal.script.Assist
import trplugins.menu.util.EvalResult

object JexlAgent {
    private val prefixes = arrayOf(
        "jexl: ",
    )


    private val bindings = mutableMapOf<String,Any?>(
        "bukkitServer" to Bukkit.getServer(),
        "utils" to Assist.INSTANCE,
    )

    fun putBinding(key: String, value: Any) {
        bindings[key] = value
    }

    private val compiledScripts = Maps.newConcurrentMap<String, JexlCompiledScript>()

    fun serialize(script: String): Pair<Boolean, String?> {
        prefixes.firstOrNull { script.startsWith(it) }?.let {
            return true to script.removePrefix(it)
        }
        return false to null
    }

    fun preCompile(script: String): JexlCompiledScript {
        return compiledScripts.computeIfAbsent(script) {
            defaultJexlCompiler.compileToScript(script)
        }
    }

    fun eval(session: MenuSession, script: String, cacheScript: Boolean = true): EvalResult {
        val context = Maps.newHashMap(bindings).also {
            it["session"] = session
            it["player"] = session.viewer
            it["sender"] = session.viewer
            it["data"] = Metadata.getData(session.viewer).data
            it["meta"] = Metadata.getMeta(session.viewer).data
            it["config"] = session.menu?.conf
        }
        val compiledScript =
            if (cacheScript) preCompile(script)
            else defaultJexlCompiler.compileToScript(script)
        return EvalResult(compiledScript.eval(context))
    }
}