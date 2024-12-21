package trplugins.menu.api.action

import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionEntry
import trplugins.menu.api.action.base.ActionEval
import trplugins.menu.api.action.impl.logic.Delay
import trplugins.menu.util.ClassUtils
import trplugins.menu.util.EvalResult
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.BiFunction

/**
 * TrMenu
 * trplugins.menu.api.action.ActionHandle
 *
 * @author Score2
 * @since 2022/02/09 21:36
 */
class ActionHandle(
    val conditionParser: BiFunction<ProxyPlayer, String, EvalResult> =
        BiFunction { _, _ -> EvalResult.TRUE },
    val placeholderParser: BiFunction<ProxyPlayer, String, String> =
        BiFunction { _, s -> s },
    private val default: String = "tell"
) {

    private val registries = mutableSetOf<ActionBase>()

    init {
        ClassUtils.subClasses(ActionBase::class.java) { action ->
            register(action.invokeConstructor(this))
        }
    }

    fun register(vararg bases: ActionBase) {
        bases.forEach {
            registries.add(it)
        }
    }

    fun unregister(vararg bases: ActionBase) {
        bases.forEach { base ->
            registries.remove(base)
        }
    }

    private fun unregister(vararg names: String) {
        return unregister(*names.map { getRegisteredAction(it) }.toTypedArray())
    }

    fun unregister(vararg classes: Class<*>) {
        return unregister(*classes.map { it.simpleName }.toTypedArray())
    }

    fun getRegisteredAction(key: String): ActionBase =
        registries.find { key.lowercase() == it.lowerName || it.regex.matches(key.lowercase()) } ?: defaultAction

    private val defaultAction by lazy { registries.find { it.name.equals(default, true) }!! }

    fun runAction(player: ProxyPlayer, actions: List<String>) {
        runAction(player, ActionEntry.of(this, actions))
    }

    fun runAction(player: ProxyPlayer, actions: List<ActionEntry>): Boolean {
        return runAction(player, actions.iterator())
    }

    fun runAction(player: ProxyPlayer, actions: Iterator<ActionEntry>): Boolean {
        val run = mutableListOf<ActionEntry>()
        var result = true
        var delay = 0L
        val allowed = AtomicBoolean(true)

        run filter@{
            while (actions.hasNext()) {
                val action = actions.next()
                if (!action.option.evalChance()) {
                    continue
                }
                when {
                    action.base is ActionEval -> {
                        if (delay > 0) {
                            submit(delay = delay) {
                                allowed.set(action.eval(player))
                            }
                        } else if (!action.eval(player)) {
                            result = false
                            return@filter
                        }
                    }
                    action.base is Delay -> delay += action.base.getDelay(player, action.contents.stringContent())
                    delay > 0 -> submit(delay = delay) {
                        if (allowed.get()) {
                            action.execute(player)
                        }
                    }
                    else -> run.add(action)
                }
            }
        }
        run.forEach { it.execute(player) }
        return result
    }

    companion object {

        val actionsBound = " ?(_\\|\\|_|&&&) ?".toRegex()

    }

}