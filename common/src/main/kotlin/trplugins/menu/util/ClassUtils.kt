package trplugins.menu.util

import taboolib.common.io.runningClasses
import java.util.function.Consumer

object ClassUtils {
    @JvmStatic
    val staticClass: Class<*> by lazy {
        if (System.getProperty("java.version").contains("1.8."))
            Class.forName("jdk.internal.dynalink.beans.StaticClass")
        else
            Class.forName("jdk.dynalink.beans.StaticClass")
    }

    @JvmStatic
    fun staticClass(className: String): Any? {
        return try {
            staticClass.getMethod("forClass", Class::class.java).invoke(null, Class.forName(className))
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun <T> subClasses(`super`: Class<T>, consumer: Consumer<Class<out T>>) {
        runningClasses.forEach { `class` ->
            if (`class`.structure.isAbstract) return@forEach
            if (`class`.structure.superclass?.name != `super`.name) return@forEach

            consumer.accept(`class`.structure.owner.instance!!.asSubclass(`super`))
        }
    }
}