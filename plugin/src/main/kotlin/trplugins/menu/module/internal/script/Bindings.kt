package trplugins.menu.module.internal.script

import trplugins.menu.TrMenu

object Bindings {
    var exportHook = true;
    var bootloaderCode="";
    fun load() {
        val bindings = TrMenu.SETTINGS.getConfigurationSection("Script")
        exportHook = bindings?.getBoolean("Export-Hook-Plugin", true) ?: true
        val builder = StringBuilder()
        bindings?.getConfigurationSection("Binding-Map")?.let {
            it.getKeys(false).forEach { key ->
                val value = it.getString(key)
                builder.append("var $key = Java.type('$value');") // 生成 JS 引导代码
            }
        }
        bootloaderCode = builder.toString()
    }
}