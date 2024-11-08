package trplugins.menu.module.internal.hook.impl

import org.bukkit.Bukkit
import taboolib.module.nms.MinecraftVersion
import trplugins.menu.module.internal.hook.HookAbstract


class HookGraalJS : HookAbstract() {

    override fun getPluginName(): String {
        return "TrMenu-Graal"
    }

    override val isHooked by lazy {
        if (!MinecraftVersion.isUniversal) return@lazy false
        Bukkit.getPluginManager().isPluginEnabled(getPluginName())
    }

}