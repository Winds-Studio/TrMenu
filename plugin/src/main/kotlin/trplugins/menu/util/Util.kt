package trplugins.menu.util

import taboolib.common.io.runningClasses
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.configuration.Configuration
import java.lang.reflect.Modifier

/**
 * @author Arasple
 * @date 2021/2/19 22:40
 */
fun Throwable.print(title: String) {
    println("§c[TrMenu] §8$title")
    println("         §8${localizedMessage}")
    stackTrace.forEach {
        println("         §8$it")
    }
}

val Boolean.trueOrNull get() = if (this) true else null

// 未来需要改进该功能
fun String.parseSimplePlaceholder(map: Map<Regex, String>): String {
    var raw = this
    map.forEach { raw = raw.replace(it.key, it.value) }
    return raw
}

// 未来需要改进该功能
fun String.parseIconId(iconId: String) = parseSimplePlaceholder(mapOf("(?i)@iconId@".toRegex() to iconId))

fun Configuration.ignoreCase(path: String) = getKeys(true).find { it.equals(path, ignoreCase = true) } ?: path


// 极其不稳定的方法, 已停用
/*

inline fun <reified T> fromClassesCollect(`super`: Class<T>) = mutableListOf<T>().also { list ->
    runningClasses.forEach { `class` ->
        if (Modifier.isAbstract(`class`.modifiers)) return@forEach
        list.add(runCatching {
            `class`.asSubclass(`super`).getConstructor().newInstance()
        }.getOrNull() ?: return@forEach)
    }
}
*/


@Suppress("UNCHECKED_CAST")
fun <T> fromCompanionClassesCollect(`super`: Class<T>) = mutableListOf<T>().also { list ->
    runningClasses.forEach { `class` ->
        val instance = runCatching { `class`.getProperty<Any>("Companion", true) as T }.getOrNull() ?: return@forEach
        list.add(instance)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> fromObjectClassesCollect(`super`: Class<T>) = mutableListOf<T>().also { list ->
    runningClasses.forEach { `class` ->
        val instance = runCatching { `class`.getProperty<Any>("INSTANCE", true) as T }.getOrNull() ?: return@forEach
        list.add(instance)
    }
}
