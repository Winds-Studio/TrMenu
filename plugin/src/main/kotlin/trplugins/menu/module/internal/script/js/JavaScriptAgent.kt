package trplugins.menu.module.internal.script.js

import org.bukkit.Bukkit
import taboolib.common.util.unsafeLazy
import trplugins.menu.module.display.MenuSession
import trplugins.menu.module.internal.data.Metadata
import trplugins.menu.module.internal.hook.HookPlugin
import trplugins.menu.module.internal.script.Assist
import trplugins.menu.module.internal.script.Bindings
import trplugins.menu.util.EvalResult
import java.util.function.Function
import javax.script.ScriptContext
import javax.script.SimpleBindings
import javax.script.SimpleScriptContext

/**
 * @author Arasple
 * @date 2021/1/31 11:44
 */
object JavaScriptAgent {

    private val prefixes = arrayOf(
        "js: ",
        "$ ",
    )
    private val graalvm by unsafeLazy { HookPlugin.getGraalvm().isHooked }

    private val bindings = mutableMapOf(
        "bukkitServer" to Bukkit.getServer(),
        "utils" to Assist.INSTANCE,
    )

    fun putBinding(key: String, value: Any) {
        bindings[key] = value
    }

    fun serialize(script: String): Pair<Boolean, String?> {
        prefixes.firstOrNull { script.startsWith(it) }?.let {
            return true to script.removePrefix(it)
        }
        return false to null
    }

    fun preCompile(script: String) {
        (Bindings.bootloaderCode + script).let {
            if (graalvm) {
                GraalJSAgent.preCompile(it)
            } else {
                NashornAgent.preCompile(it)
            }
        }
    }

    fun eval(session: MenuSession, script: String, cacheScript: Boolean = true): EvalResult {
        val context = SimpleScriptContext()
        context.setBindings(SimpleBindings(bindings).also {
            it["session"] = session
            it["player"] = session.viewer
            it["sender"] = session.viewer
            it["data"] = Metadata.getData(session.viewer).data
            it["meta"] = Metadata.getMeta(session.viewer).data
            it["config"] = session.menu?.conf
        }, ScriptContext.ENGINE_SCOPE)
        val setAttribute: (String, Function<Any, Any?>) -> Unit = { name, func ->
            context.setAttribute(name, func, ScriptContext.ENGINE_SCOPE)
        }

        setAttribute(
            "vars", java.util.function.Function<Any, Any?> { session.parse(it.toString()) },
        )
        setAttribute(
            "varInt", java.util.function.Function<Any, Any?> { session.parse(it.toString()).toIntOrNull() ?: 0 },
        )
        setAttribute(
            "varDouble",
            java.util.function.Function<Any, Any?> { session.parse(it.toString()).toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "funs", java.util.function.Function<Any, Any?> { session.parse("{$it}") },
        )
        setAttribute(
            "funInt", java.util.function.Function<Any, Any?> { session.parse("{$it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "funDouble", java.util.function.Function<Any, Any?> { session.parse("{$it}").toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "kes", java.util.function.Function<Any, Any?> { session.parse("{ke: $it}") },
        )
        setAttribute(
            "keInt", java.util.function.Function<Any, Any?> { session.parse("{ke: $it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "keDouble", java.util.function.Function<Any, Any?> { session.parse("{ke: $it}").toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "nodes", java.util.function.Function<Any, Any?> { session.parse("{node: $it}") },
        )
        setAttribute(
            "nodeInt", java.util.function.Function<Any, Any?> { session.parse("{node: $it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "nodeDouble",
            java.util.function.Function<Any, Any?> { session.parse("{node: $it}").toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "metas", java.util.function.Function<Any, Any?> { session.parse("{meta: $it}") },
        )
        setAttribute(
            "metaInt", java.util.function.Function<Any, Any?> { session.parse("{meta: $it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "metaDouble",
            java.util.function.Function<Any, Any?> { session.parse("{meta: $it}").toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "datas", java.util.function.Function<Any, Any?> { session.parse("{data: $it}") },
        )
        setAttribute(
            "dataInt", java.util.function.Function<Any, Any?> { session.parse("{data: $it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "dataDouble",
            java.util.function.Function<Any, Any?> { session.parse("{data: $it}").toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "funcs", java.util.function.Function<Any, Any?> { session.parse("\${$it}") },
        )
        setAttribute(
            "funcInt", java.util.function.Function<Any, Any?> { session.parse("\${$it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "funcDouble", java.util.function.Function<Any, Any?> { session.parse("\${$it}").toDoubleOrNull() ?: 0.0 },
        )
        setAttribute(
            "gdatas", java.util.function.Function<Any, Any?> { session.parse("{gdata: $it}") },
        )
        setAttribute(
            "gdataInt", java.util.function.Function<Any, Any?> { session.parse("{gdata: $it}").toIntOrNull() ?: 0 },
        )
        setAttribute(
            "gdataDouble",
            java.util.function.Function<Any, Any?> { session.parse("{gdata: $it}").toDoubleOrNull() ?: 0.0 },
        )

        val rawCode = Bindings.bootloaderCode + script
        return if (graalvm) {
            GraalJSAgent.eval(context, rawCode, cacheScript)
        } else {
            NashornAgent.eval(context, rawCode, cacheScript)
        }
    }

}
